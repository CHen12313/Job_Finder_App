# Job Finder Server — API Reference

The HTTP API that the Android app calls: every route, its request and response contract, and a Kotlin example for each.

Examples use **Retrofit + OkHttp + kotlinx.serialization**. They're illustrative: adapt names and error handling to the app's architecture.

## Contents

- [Conventions](#conventions)
  - [Base URL](#base-url)
  - [Authentication: session cookie](#authentication-session-cookie)
  - [CSRF and the Origin header](#csrf-and-the-origin-header)
  - [Not-logged-in responses (302, not 401)](#not-logged-in-responses-302-not-401)
  - [Error shapes](#error-shapes)
- [Client setup (Kotlin)](#client-setup-kotlin)
- [Route map](#route-map)
- [Auth & session](#auth--session)
- [CV & search parameters](#cv--search-parameters)
- [Onboarding status](#onboarding-status)
- [Tier & quota](#tier--quota)
- [Report generation (streaming)](#report-generation-streaming)
- [Reports (PDF)](#reports-pdf)
- [Full JSON reports](#full-json-reports)
- [Matched jobs](#matched-jobs)
- [Saved jobs](#saved-jobs)
- [Skills trend](#skills-trend)
- [Billing](#billing)
- [Health check](#health-check)

---

## Conventions

### Base URL

| Build | Base URL |
|---|---|
| debug | `http://10.0.2.2:8000/` (Django dev server on the host machine, as seen from the Android emulator) |
| release | `https://job-finder.co/` |

The base URL is exposed as `BuildConfig.BASE_URL` and always ends in `/`, so the Retrofit paths below have no leading slash.

Always use the apex host `job-finder.co`. `www.job-finder.co` redirects to it.

Request and response bodies are JSON unless noted. Timestamps are ISO-8601 strings; keep them as `String` and parse them where they're displayed.

### Authentication: session cookie

The server uses Django **session** auth, not tokens:

- `POST api/login` or `POST api/register` sets a `sessionid` cookie.
- Every later request must send it back.
- The client needs a `CookieJar` that **persists cookies to disk**, so the user stays logged in across app restarts.
- Send `"remember": true` on login so the session lasts 30 days.

In production both cookies are marked `Secure`. Keep that flag when you persist them.

### CSRF and the Origin header

Every `POST` must carry:

| Header | Value |
|---|---|
| `X-CSRFToken` | The current value of the `csrftoken` cookie |
| `Origin` | The base URL without the trailing slash, e.g. `https://job-finder.co` |

- **Getting the cookie:** `GET api/auth/user` sets `csrftoken`, even when it returns `401`. Call it once on app start.
- **Read it fresh on every request.** Django issues a new token at login.
- **`Origin` is required in production.** Over HTTPS, Django rejects a POST with `403` if it has neither `Origin` nor `Referer`, and a native client sends neither by default.

### Not-logged-in responses (302, not 401)

Routes marked **auth** use Django's `login_required`. Without a valid session they don't return `401`. They return:

```
HTTP/1.1 302 Found
Location: /accounts/login/?next=/api/<route>
```

If OkHttp follows that redirect, it lands on the website's HTML page with a `200`, and JSON parsing fails confusingly. So:

- build the client with `followRedirects(false)`;
- treat **any `3xx` from an `api/` route as "session expired → go to login"**.

Only `GET api/auth/user` returns a real `401`.

### Error shapes

Error fields differ between routes, and some failures come back with **status `200`** and `"success": false`. Always check the body, not just the status code.

```json
{ "success": false, "error": "..." }      // most routes
{ "success": false, "message": "..." }    // email-code, quota, save/unsave DB errors, portal
{ "error": "..." }                        // login (no "success" key)
```

A single tolerant model covers all of them:

```kotlin
@Serializable
data class ApiError(
    val success: Boolean? = null,
    val error: String? = null,
    val message: String? = null,
) {
    val text: String get() = error ?: message ?: "Unknown error"
}
```

---

## Client setup (Kotlin)

Gradle dependencies:
- `com.squareup.okhttp3:okhttp`
- `com.squareup.retrofit2:retrofit`
- `com.squareup.retrofit2:converter-kotlinx-serialization`
- `org.jetbrains.kotlinx:kotlinx-serialization-json`
- the `org.jetbrains.kotlin.plugin.serialization` Gradle plugin

```kotlin
// Adds X-CSRFToken + Origin to every unsafe request.
class CsrfInterceptor(
    private val cookieJar: CookieJar,
    private val baseUrl: HttpUrl,
) : Interceptor {
    private val origin = baseUrl.toString().trimEnd('/')

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method in setOf("GET", "HEAD", "OPTIONS")) return chain.proceed(request)

        val csrf = cookieJar.loadForRequest(baseUrl).firstOrNull { it.name == "csrftoken" }?.value
        val withHeaders = request.newBuilder()
            .header("Origin", origin)
            .apply { if (csrf != null) header("X-CSRFToken", csrf) }
            .build()
        return chain.proceed(withHeaders)
    }
}

val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

fun buildApi(cookieJar: CookieJar): Pair<JobFinderApi, OkHttpClient> {
    val baseUrl = BuildConfig.BASE_URL.toHttpUrl()
    val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)                     // must persist to disk — see Conventions
        .addInterceptor(CsrfInterceptor(cookieJar, baseUrl))
        .followRedirects(false)                   // 302 = not logged in
        .build()

    val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(JobFinderApi::class.java)
    return api to client
}

// Turns a non-2xx Retrofit response into an ApiError.
fun Response<*>.apiError(): ApiError =
    errorBody()?.string()?.let { runCatching { json.decodeFromString<ApiError>(it) }.getOrNull() }
        ?: ApiError(error = "HTTP ${code()}")
```

All Retrofit methods below belong to one interface:

```kotlin
interface JobFinderApi {
    // …methods from each section below…
}
```

---

## Route map

| Method | Route | Auth | Purpose |
|---|---|---|---|
| GET | `api/auth/user` | – | Session check; sets the CSRF cookie |
| POST | `api/login` | Turnstile | Log in by username or email |
| POST | `api/logout` | – | Clear the session |
| POST | `api/send_email_verification_code` | – | Email a 6-digit code before registering |
| POST | `api/register` | Turnstile | Verify the code, create the user, log in |
| POST | `api/upload_cv` | auth | Upload a `.pdf`/`.txt` CV (multipart) |
| POST | `api/upload_parameters` | auth | Save search parameters and company filters |
| GET | `api/get_search_parameters` | auth | Read saved search parameters and filters |
| GET | `api/user_setup_status` | auth | Onboarding flags |
| POST | `api/get_user_tier_quota` | auth | Tier, remaining quota, whether a run is active |
| POST | `api/generate_report` | auth | Start a run; streams SSE progress frames |
| GET | `api/list_all_reports` | auth | List report runs with PDF download links |
| GET | `api/reports/{run_id}` | auth | Download one run's PDF |
| GET | `api/list_json_reports` | auth | List full JSON reports |
| GET | `api/json_reports/{run_id}` | auth | One full JSON report and its stats |
| GET | `api/get_report_jobs` | auth | Matched-job cards (cursor-paginated) |
| GET | `api/get_report_job_section/{job_id}` | auth | Full detail for one matched job |
| POST | `api/save_job` | auth | Save a matched job |
| POST | `api/unsave_job` | auth | Unsave a matched job |
| GET | `api/get_saved_jobs` | auth | List saved jobs |
| GET | `api/get_skills_trend` | auth | Aggregated matched/missing skill counts |
| POST | `api/create_checkout_session` | auth | Start a Stripe subscription checkout |
| POST | `api/create_portal_session` | auth | Open the Stripe billing portal |
| any | `check_work` | – | Health probe |

**Turnstile:** login and register also need a Cloudflare Turnstile token in the body (`cf-turnstile-response`). The token must have been solved with the matching action (`"login"` / `"sign_up"`) on a hostname the server accepts.

---

## Auth & session

### `GET api/auth/user`

Checks the session and sets the `csrftoken` cookie.

| Status | Body |
|---|---|
| `200` | `{"authenticated": true, "user": User}` |
| `401` | `{"authenticated": false}` (logged out — expected, not an error) |

```kotlin
@Serializable
data class User(
    val id: Int,
    val username: String,
    @SerialName("user_email") val userEmail: String,
    @SerialName("user_subscription_type") val subscriptionType: String,   // "free" | "paid"
    @SerialName("user_cv_text") val cvText: String? = null,
    @SerialName("user_params") val params: SearchParameters? = null,
    @SerialName("user_rules") val rules: FilterRules? = null,
    @SerialName("user_cv_set") val cvSet: Boolean,
    @SerialName("user_search_set") val searchSet: Boolean,
    @SerialName("first_report_generated") val firstReportGenerated: Boolean,
    @SerialName("user_setup_complete") val setupComplete: Boolean? = null,
)

@Serializable
data class AuthUserResponse(val authenticated: Boolean, val user: User? = null)

@GET("api/auth/user")
suspend fun authUser(): Response<AuthUserResponse>
```

```kotlin
// On app start: primes the CSRF cookie and decides which screen to show.
val res = api.authUser()
val loggedInUser: User? = if (res.isSuccessful) res.body()?.user else null   // 401 → null
```

### `POST api/login`

Request:

```json
{
  "username": "ling or ling@example.com",
  "password": "...",
  "remember": true,
  "cf-turnstile-response": "<token>"
}
```

`username` accepts either a username or an email address.

| Status | Body | Meaning |
|---|---|---|
| `200` | `{"success": true, "redirect": "/"}` | Logged in; `sessionid` cookie set. Ignore `redirect`. |
| `400` | `{"error": "Invalid credentials"}` | Unknown user or wrong password (deliberately the same message) |
| `400` | `{"error": "Invalid request body"}` | Malformed JSON |
| `401` | `{"error": "Invalid Credentials"}` | `username` or `password` missing |
| `403` | `{"error": "Verification failed"}` | Turnstile check failed |

```kotlin
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val remember: Boolean = true,
    @SerialName("cf-turnstile-response") val turnstileToken: String,
)

@Serializable
data class SuccessResponse(val success: Boolean, val message: String? = null, val error: String? = null)

@POST("api/login")
suspend fun login(@Body body: LoginRequest): Response<SuccessResponse>
```

```kotlin
val res = api.login(LoginRequest(usernameOrEmail, password, turnstileToken = token))
if (res.isSuccessful) {
    val user = api.authUser().body()?.user          // fetch the profile after login
} else {
    showError(res.apiError().text)                  // e.g. "Invalid credentials"
}
```

### `POST api/logout`

No body. Returns `200 {"success": true}`. Clear any cached user state client-side afterwards.

```kotlin
@POST("api/logout")
suspend fun logout(): Response<SuccessResponse>
```

### `POST api/send_email_verification_code`

Emails a 6-digit code to the address. Call this before `register`. The code expires after **10 minutes**.

Request: `{"user_email": "ling@example.com"}`

| Status | Body | Meaning |
|---|---|---|
| `200` | `{"success": true}` | Code sent |
| `200` | `{"success": false, "message": "Too many attempts"}` | Resend limit reached; try again later |
| `200` | `{"success": false, "message": "Data base error while granting verification code"}` | Server-side failure |
| `400` | `"malformed request body"` / `"no user email provided"` | Bad request |
| `502` | `{"success": false, "message": "Failed to send verification email"}` | The email provider rejected the send |

```kotlin
@Serializable
data class SendCodeRequest(@SerialName("user_email") val email: String)

@POST("api/send_email_verification_code")
suspend fun sendVerificationCode(@Body body: SendCodeRequest): Response<SuccessResponse>
```

```kotlin
val res = api.sendVerificationCode(SendCodeRequest(email))
val body = res.body()
when {
    res.isSuccessful && body?.success == true -> showCodeField()
    res.isSuccessful -> showError(body?.message ?: "Could not send code")   // 200 + success:false
    else -> showError(res.apiError().text)
}
```

### `POST api/register`

Request. The email key here is `email`, **not** `user_email`:

```json
{
  "username": "ling",
  "email": "ling@example.com",
  "password": "...",
  "verification_code": "123456",
  "cf-turnstile-response": "<token>"
}
```

On success the user is created **and logged in** (the `sessionid` cookie is set).

| Status | Body | Meaning |
|---|---|---|
| `201` | `{"success": true, "redirect": "/"}` | Registered and logged in |
| `200` | `{"success": false, "error": "Invalid verification code"}` | Wrong code, or no code requested for this email |
| `200` | `{"success": false, "error": "Verification code expired"}` | Older than 10 minutes |
| `200` | `{"success": false, "error": "Too many attempts, request a new code"}` | Request a new code |
| `400` | `"Invalid parameters"` / `"malformed request body"` | Empty username/password, bad email, malformed JSON |
| `403` | `{"error": "Verification Failed"}` | Turnstile check failed |
| `409` | `"username already exists"` / `"email already exists"` / `"Username or email already exists"` | Duplicate account |

```kotlin
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerialName("verification_code") val verificationCode: String,
    @SerialName("cf-turnstile-response") val turnstileToken: String,
)

@POST("api/register")
suspend fun register(@Body body: RegisterRequest): Response<SuccessResponse>
```

```kotlin
val res = api.register(RegisterRequest(username, email, password, code, token))
val body = res.body()
when {
    res.code() == 201 && body?.success == true -> goToOnboarding()
    res.isSuccessful -> showError(body?.error ?: "Registration failed")    // 200 + success:false
    else -> showError(res.apiError().text)                                 // 400/403/409
}
```

---

## CV & search parameters

### `POST api/upload_cv` *(auth)*

`multipart/form-data` with one field, `pdf_file`. Only `.pdf` and `.txt` files are accepted; the server picks the parser **by file extension**, so the filename must end in one of those.

| Status | Body |
|---|---|
| `200` | `{"success": true, "message": "CV uploaded successfully", "cv_text": "..."}` |
| `400` | Missing file, unsupported extension, or no text could be extracted |
| `500` | `{"error": "Failed to process CV"}` (e.g. a corrupt PDF) |

```kotlin
@Serializable
data class UploadCvResponse(
    val success: Boolean,
    val message: String? = null,
    @SerialName("cv_text") val cvText: String? = null,
)

@Multipart
@POST("api/upload_cv")
suspend fun uploadCv(@Part file: MultipartBody.Part): Response<UploadCvResponse>
```

```kotlin
// uri comes from the system file picker (ActivityResultContracts.OpenDocument)
val bytes = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
val part = MultipartBody.Part.createFormData(
    "pdf_file",
    "cv.pdf",                                    // extension decides the parser
    bytes.toRequestBody("application/pdf".toMediaType()),
)
val res = api.uploadCv(part)
```

### `POST api/upload_parameters` *(auth)*

Request:

```json
{
  "region": "gb",
  "query": "python developer",
  "where": "London",
  "extra_parameters": { "sort_by": "date" },
  "filter_mode": "off",
  "blacklist_companies": ["Google", "Meta"],
  "whitelist_companies": ["Monzo"]
}
```

| Field | Rules |
|---|---|
| `region`, `query` | Required before a report can be generated |
| `extra_parameters` | Optional object; defaults to `{"sort_by": "date"}` |
| `filter_mode` | `"off"`, `"blacklist"` or `"whitelist"` |
| `blacklist_companies` | Must be a non-empty list when `filter_mode` is `"blacklist"` |
| `whitelist_companies` | Must be a non-empty list when `filter_mode` is `"whitelist"` |

Company names are normalized and de-duplicated by the server.

| Status | Body |
|---|---|
| `200` | `{"success": true, "redirect": "/", "parameters": SearchParameters, "user_rules": FilterRules}` |
| `400` | Validation failure |

```kotlin
@Serializable
data class SearchParameters(
    val region: String? = null,
    val query: String? = null,
    val where: String? = null,
    @SerialName("extra_parameters") val extraParameters: JsonObject? = null,
)

@Serializable
data class FilterRules(
    @SerialName("filter_mode") val filterMode: String = "off",   // off | blacklist | whitelist
    @SerialName("blacklist_companies") val blacklist: List<String> = emptyList(),
    @SerialName("whitelist_companies") val whitelist: List<String> = emptyList(),
)

@Serializable
data class UploadParametersRequest(
    val region: String,
    val query: String,
    val where: String? = null,
    @SerialName("extra_parameters") val extraParameters: JsonObject? = null,
    @SerialName("filter_mode") val filterMode: String = "off",
    @SerialName("blacklist_companies") val blacklist: List<String> = emptyList(),
    @SerialName("whitelist_companies") val whitelist: List<String> = emptyList(),
)

@Serializable
data class UploadParametersResponse(
    val success: Boolean,
    val parameters: SearchParameters? = null,
    @SerialName("user_rules") val rules: FilterRules? = null,
)

@POST("api/upload_parameters")
suspend fun uploadParameters(@Body body: UploadParametersRequest): Response<UploadParametersResponse>
```

```kotlin
val res = api.uploadParameters(
    UploadParametersRequest(region = "gb", query = "android developer", where = "London")
)
```

### `GET api/get_search_parameters` *(auth)*

```json
{
  "success": true,
  "search_parameters": { "region": "gb", "query": "...", "where": "...", "extra_parameters": {} },
  "user_filter_parameters": { "filter_mode": "off", "blacklist_companies": [], "whitelist_companies": [] },
  "user_subscription_type": "free"
}
```

`search_parameters` is `null` until the user has saved parameters once.

```kotlin
@Serializable
data class GetSearchParametersResponse(
    val success: Boolean,
    @SerialName("search_parameters") val searchParameters: SearchParameters? = null,
    @SerialName("user_filter_parameters") val filterRules: FilterRules? = null,
    @SerialName("user_subscription_type") val subscriptionType: String,
)

@GET("api/get_search_parameters")
suspend fun getSearchParameters(): Response<GetSearchParametersResponse>
```

---

## Onboarding status

### `GET api/user_setup_status` *(auth)*

```json
{
  "success": true,
  "user_setup_status": {
    "user_cv_set": true,
    "user_search_set": true,
    "first_report_generated": false,
    "user_setup_complete": false
  }
}
```

`user_setup_complete` is `user_cv_set AND user_search_set AND first_report_generated`. Use these flags to decide which onboarding step to show next.

```kotlin
@Serializable
data class SetupStatus(
    @SerialName("user_cv_set") val cvSet: Boolean,
    @SerialName("user_search_set") val searchSet: Boolean,
    @SerialName("first_report_generated") val firstReportGenerated: Boolean,
    @SerialName("user_setup_complete") val setupComplete: Boolean? = null,
)

@Serializable
data class SetupStatusResponse(
    val success: Boolean,
    @SerialName("user_setup_status") val status: SetupStatus,
)

@GET("api/user_setup_status")
suspend fun userSetupStatus(): Response<SetupStatusResponse>
```

---

## Tier & quota

### `POST api/get_user_tier_quota` *(auth)*

The body must be valid JSON. Send `{}`.

| Free tier | Paid tier |
|---|---|
| 3 runs / month | 30 runs / period |

```json
{
  "success": true,
  "quota_remaining": 2,
  "user_subscription_type": "free",
  "demoted": false,
  "run_active": false
}
```

| Field | Meaning |
|---|---|
| `demoted` | `true` if this call just moved a lapsed paid user back to free |
| `run_active` | `true` while a report run is still in progress on the server |

Errors: `400` for a non-JSON body; `404`/`500` with `{"success": false, "message": "..."}`.

**Polling for a run's end.** A run keeps going on the server even if the app disconnects from its stream (app backgrounded, network drop). To find out when it finishes, poll this route every ~5 seconds until `run_active` is `false`, then refresh the report list.

```kotlin
@Serializable
data class TierQuotaResponse(
    val success: Boolean,
    @SerialName("quota_remaining") val quotaRemaining: Int,
    @SerialName("user_subscription_type") val subscriptionType: String,
    val demoted: Boolean = false,
    @SerialName("run_active") val runActive: Boolean = false,
)

@POST("api/get_user_tier_quota")
suspend fun getTierQuota(@Body body: JsonObject = JsonObject(emptyMap())): Response<TierQuotaResponse>
```

```kotlin
// Wait for a run that's still going server-side
suspend fun awaitRunFinished(api: JobFinderApi) {
    while (api.getTierQuota().body()?.runActive == true) delay(5_000)
}
```

---

## Report generation (streaming)

### `POST api/generate_report` *(auth)*

No request body. The server uses the saved CV and search parameters.

**Rejections, before any streaming**, come back as ordinary JSON:

| Status | Body | Meaning |
|---|---|---|
| `400` | JSON error | `region` or `query` not set in search parameters |
| `409` | `{"success": false, "error": "already running"}` | A run is already in progress. Poll `get_user_tier_quota`. |
| `429` | `{"success": false, "error": "out of quota"}` | No quota left |

**On success** the response is `text/event-stream` (Server-Sent Events). Each frame looks like:

```
event: <type>
data: <json>

```

The JSON's `type` matches the `event:` label.

| `type` | Data | Terminal? |
|---|---|---|
| `progress` | `{type, completed, total}` | no |
| `info` | `{type, message, tag?}` (`tag: "warning"` marks a warning) | no |
| `warning` | `{type, message, tag?}` (reserved; warnings currently arrive as `info`) | no |
| `error` | `{type, message}` (the reserved quota unit is refunded) | **yes** |
| `done` | One of three variants, below | **yes** |

`done` variants:

| Variant | Data | What to do |
|---|---|---|
| clean | `{type: "done", success: true, url, id}` | `id` is the run id; `url` is a PDF download link |
| early exit | `{type: "done", success: true, exit_reason}` | The run finished but found too few jobs to report on. No `id`/`url`. |
| degraded | `{type: "done", partial: true, message}` | A report was produced but couldn't be recorded; no `id` to link to |

There is exactly one terminal frame per run; stop reading after it.

Retrofit doesn't suit a streamed body, so call OkHttp directly. Runs can be quiet for long stretches, so **turn off the read timeout** for this call:

```kotlin
@Serializable
data class StreamFrame(
    val type: String,
    val completed: Int? = null,
    val total: Int? = null,
    val message: String? = null,
    val tag: String? = null,
    val success: Boolean? = null,
    val url: String? = null,
    val id: Long? = null,
    @SerialName("exit_reason") val exitReason: String? = null,
    val partial: Boolean? = null,
)

fun generateReport(client: OkHttpClient): Flow<StreamFrame> = flow {
    val streamingClient = client.newBuilder().readTimeout(0, TimeUnit.MILLISECONDS).build()
    val request = Request.Builder()
        .url(BuildConfig.BASE_URL + "api/generate_report")
        .post(ByteArray(0).toRequestBody(null))
        .build()

    streamingClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            // 400 / 409 / 429 arrive as plain JSON, not as a stream
            val err = json.decodeFromString<ApiError>(response.body!!.string())
            emit(StreamFrame(type = "error", message = err.text))
            return@flow
        }
        val source = response.body!!.source()
        val data = StringBuilder()
        while (true) {
            val line = source.readUtf8Line() ?: break           // stream closed
            when {
                line.startsWith("data:") -> data.append(line.removePrefix("data:").trim())
                line.isEmpty() && data.isNotEmpty() -> {        // blank line ends a frame
                    val frame = json.decodeFromString<StreamFrame>(data.toString())
                    data.clear()
                    emit(frame)
                    if (frame.type == "done" || frame.type == "error") return@use
                }
                // "event:" lines are ignored — "type" in the data carries the same value
            }
        }
    }
}.flowOn(Dispatchers.IO)
```

```kotlin
// In a ViewModel
generateReport(client).collect { frame ->
    when (frame.type) {
        "progress" -> progress.value = frame.completed!! to frame.total!!
        "info", "warning" -> log(frame.message)
        "error" -> showError(frame.message)
        "done" -> when {
            frame.id != null -> openReport(frame.id)
            frame.exitReason != null -> showNotEnoughJobs(frame.exitReason)
            frame.partial == true -> showPartial(frame.message)
        }
    }
}
// If the stream drops before a terminal frame, the run is still going: poll get_user_tier_quota.
```

---

## Reports (PDF)

### `GET api/list_all_reports` *(auth)*

The user's report runs, newest first. Also accepts `POST`.

```json
{
  "success": true,
  "reports": [
    {
      "run_id": 17,
      "report_date": "2026-07-03",
      "query": "python developer",
      "location": "gb",
      "job_count": 12,
      "avg_score": 72,
      "top_score": 88,
      "filename": "Job_Listing_Brief_2026-07-03_(1).pdf",
      "download_url": "..."
    }
  ]
}
```

`avg_score` and `top_score` are `null` when a run matched no jobs. `download_url` is a time-limited link; fetch the list again rather than storing it.

```kotlin
@Serializable
data class ReportRun(
    @SerialName("run_id") val runId: Long,
    @SerialName("report_date") val reportDate: String,
    val query: String? = null,
    val location: String? = null,
    @SerialName("job_count") val jobCount: Int,
    @SerialName("avg_score") val avgScore: Int? = null,
    @SerialName("top_score") val topScore: Int? = null,
    val filename: String,
    @SerialName("download_url") val downloadUrl: String,
)

@Serializable
data class ReportListResponse(val success: Boolean, val reports: List<ReportRun>)

@GET("api/list_all_reports")
suspend fun listAllReports(): Response<ReportListResponse>
```

### `GET api/reports/{run_id}` *(auth)*

Streams the run's PDF back as `application/pdf` with an attachment filename. Returns `404` if the run doesn't exist or isn't the user's.

```kotlin
@Streaming
@GET("api/reports/{runId}")
suspend fun downloadReport(@Path("runId") runId: Long): Response<ResponseBody>
```

```kotlin
val res = api.downloadReport(run.runId)
if (res.isSuccessful) {
    val file = File(context.cacheDir, run.filename)
    res.body()!!.byteStream().use { input -> file.outputStream().use { input.copyTo(it) } }
    // then open it with a FileProvider URI + ACTION_VIEW
}
```

---

## Full JSON reports

The complete structured output of one run: every job's analysis plus run-level improvement suggestions. Use this to render a report natively instead of showing the PDF.

### `GET api/list_json_reports` *(auth)*

```json
{
  "success": true,
  "reports": [
    { "run_id": 18, "filename": "Job_Listing_Brief_2026-07-08_(1).json" },
    { "run_id": 17, "filename": "Job_Listing_Brief_2026-07-03_(1).json" }
  ]
}
```

```kotlin
@Serializable
data class JsonReportEntry(@SerialName("run_id") val runId: Long, val filename: String)

@Serializable
data class JsonReportListResponse(val success: Boolean, val reports: List<JsonReportEntry>)

@GET("api/list_json_reports")
suspend fun listJsonReports(): Response<JsonReportListResponse>
```

### `GET api/json_reports/{run_id}` *(auth)*

```json
{
  "success": true,
  "run_id": 17,
  "report_date": "2026-07-03",
  "query": "python developer",
  "location": "gb",
  "job_count": 12,
  "avg_score": 72,
  "top_score": 88,
  "report": {
    "jobs": [
      {
        "job_id": "5712835284",
        "matches": [ { "point": "...", "evidence": "..." } ],
        "non_matches": [ { "point": "...", "evidence": "..." } ],
        "matched_skills": ["Python", "Flask"],
        "missing_skills": ["AWS", "GCP"],
        "fit_score": 55,
        "fit_score_reason": "..."
      }
    ],
    "improvements": [ { "title": "...", "detail": "..." } ]
  }
}
```

Errors: `404` if the run doesn't exist or isn't the user's; `500` if the report file is missing on the server.

```kotlin
@Serializable
data class Point(val point: String, val evidence: String)

@Serializable
data class ReportJob(
    @SerialName("job_id") val jobId: String,
    val matches: List<Point> = emptyList(),
    @SerialName("non_matches") val nonMatches: List<Point> = emptyList(),
    @SerialName("matched_skills") val matchedSkills: List<String> = emptyList(),
    @SerialName("missing_skills") val missingSkills: List<String> = emptyList(),
    @SerialName("fit_score") val fitScore: Int? = null,
    @SerialName("fit_score_reason") val fitScoreReason: String? = null,
)

@Serializable
data class Improvement(val title: String, val detail: String)

@Serializable
data class FullReport(val jobs: List<ReportJob>, val improvements: List<Improvement> = emptyList())

@Serializable
data class JsonReportResponse(
    val success: Boolean,
    @SerialName("run_id") val runId: Long,
    @SerialName("report_date") val reportDate: String,
    val query: String? = null,
    val location: String? = null,
    @SerialName("job_count") val jobCount: Int,
    @SerialName("avg_score") val avgScore: Int? = null,
    @SerialName("top_score") val topScore: Int? = null,
    val report: FullReport,
)

@GET("api/json_reports/{runId}")
suspend fun getJsonReport(@Path("runId") runId: Long): Response<JsonReportResponse>
```

---

## Matched jobs

### `GET api/get_report_jobs` *(auth)*

Lightweight job cards, ordered by day discovered (newest first), then fit score (highest first).

| Query param | Type | Notes |
|---|---|---|
| `limit` | int | Default `3`, clamped to `1..50` |
| `cursor` | string | Optional. Pass back `next_cursor` from the previous page **verbatim**; it's opaque. |

```json
{
  "success": true,
  "jobs": [
    {
      "job_id": "5712835284",
      "job_title": "Junior Software Engineer",
      "company_name": "Monzo",
      "location": "London · Hybrid",
      "estimated_fit_score": 84,
      "discovered_at": "2026-07-01T09:30:00"
    }
  ],
  "next_cursor": "<opaque string, or null when there are no more pages>"
}
```

```kotlin
@Serializable
data class JobCard(
    @SerialName("job_id") val jobId: String,
    @SerialName("job_title") val title: String? = null,
    @SerialName("company_name") val company: String? = null,
    val location: String? = null,
    @SerialName("estimated_fit_score") val fitScore: Int? = null,
    @SerialName("discovered_at") val discoveredAt: String,
)

@Serializable
data class JobCardPage(
    val success: Boolean,
    val jobs: List<JobCard>,
    @SerialName("next_cursor") val nextCursor: String? = null,
)

@GET("api/get_report_jobs")
suspend fun getReportJobs(
    @Query("limit") limit: Int = 20,
    @Query("cursor") cursor: String? = null,     // null → Retrofit omits the param
): Response<JobCardPage>
```

```kotlin
// Load every page
val all = mutableListOf<JobCard>()
var cursor: String? = null
do {
    val page = api.getReportJobs(limit = 50, cursor = cursor).body() ?: break
    all += page.jobs
    cursor = page.nextCursor
} while (cursor != null)
```

### `GET api/get_report_job_section/{job_id}` *(auth)*

Full detail for one job. Use the **top-level** `matched_skills`/`missing_skills`, not the copies inside `job_report_section`.

```json
{
  "success": true,
  "job_id": "5712835284",
  "job_title": "Junior Software Engineer",
  "company_name": "Monzo",
  "location": "London · Hybrid",
  "estimated_fit_score": 84,
  "discovered_at": "2026-07-01T09:30:00",
  "job_report_section": {
    "job_description": "One-sentence summary of the role.",
    "matches": [ { "point": "...", "evidence": "..." } ],
    "non_matches": [ { "point": "...", "evidence": "..." } ],
    "matched_skills": ["Python", "Flask"],
    "missing_skills": ["AWS", "GCP"],
    "fit_score": 84,
    "fit_score_reason": "..."
  },
  "matched_skills": ["Python", "Flask", "LLM automation", "OpenAI API"],
  "missing_skills": ["LangGraph", "AWS", "GCP"],
  "original_link": "https://www.linkedin.com/jobs/view/..."
}
```

Errors: `404 {"error": "Job does not exist"}` if the job doesn't exist or isn't the user's.

```kotlin
@Serializable
data class JobReportSection(
    @SerialName("job_description") val description: String? = null,
    val matches: List<Point> = emptyList(),
    @SerialName("non_matches") val nonMatches: List<Point> = emptyList(),
    @SerialName("fit_score") val fitScore: Int? = null,
    @SerialName("fit_score_reason") val fitScoreReason: String? = null,
)

@Serializable
data class JobDetail(
    val success: Boolean,
    @SerialName("job_id") val jobId: String,
    @SerialName("job_title") val title: String? = null,
    @SerialName("company_name") val company: String? = null,
    val location: String? = null,
    @SerialName("estimated_fit_score") val fitScore: Int? = null,
    @SerialName("discovered_at") val discoveredAt: String,
    @SerialName("job_report_section") val section: JobReportSection? = null,
    @SerialName("matched_skills") val matchedSkills: List<String> = emptyList(),
    @SerialName("missing_skills") val missingSkills: List<String> = emptyList(),
    @SerialName("original_link") val originalLink: String,
)

@GET("api/get_report_job_section/{jobId}")
suspend fun getJobDetail(@Path("jobId") jobId: String): Response<JobDetail>
```

`original_link` can be `"#"` when the posting URL is unknown; hide the "open posting" button in that case.

---

## Saved jobs

### `POST api/save_job` / `POST api/unsave_job` *(auth)*

Request: `{"job_id": "5712835284"}`

| Status | Body |
|---|---|
| `200` | `{"success": true, "message": "Job saved successfully"}` (or `"Job unsaved successfully"`) |
| `400` | `{"success": false, "error": "Invalid request body"}` |
| `500` | `{"success": false, "message": "Failed to save job"}` (or `"Failed to unsave job"`) |

Only pass `job_id`s that came from this user's own job lists.

```kotlin
@Serializable
data class JobIdRequest(@SerialName("job_id") val jobId: String)

@POST("api/save_job")
suspend fun saveJob(@Body body: JobIdRequest): Response<SuccessResponse>

@POST("api/unsave_job")
suspend fun unsaveJob(@Body body: JobIdRequest): Response<SuccessResponse>
```

```kotlin
val res = if (saved) api.unsaveJob(JobIdRequest(id)) else api.saveJob(JobIdRequest(id))
```

### `GET api/get_saved_jobs` *(auth)*

Newest-saved first. An empty list is still `"success": true`.

```json
{
  "success": true,
  "saved_jobs": [
    {
      "job_id": "5712835284",
      "job_title": "Junior Software Engineer",
      "company_name": "Monzo",
      "location": "London · Hybrid",
      "estimated_fit_score": 84,
      "original_link": "https://www.linkedin.com/jobs/view/...",
      "saved_at": "2026-07-01T09:30:00Z",
      "discovered_at": "2026-06-28T09:30:00Z"
    }
  ]
}
```

```kotlin
@Serializable
data class SavedJob(
    @SerialName("job_id") val jobId: String,
    @SerialName("job_title") val title: String? = null,
    @SerialName("company_name") val company: String? = null,
    val location: String? = null,
    @SerialName("estimated_fit_score") val fitScore: Int? = null,
    @SerialName("original_link") val originalLink: String,
    @SerialName("saved_at") val savedAt: String,
    @SerialName("discovered_at") val discoveredAt: String,
)

@Serializable
data class SavedJobsResponse(val success: Boolean, @SerialName("saved_jobs") val savedJobs: List<SavedJob>)

@GET("api/get_saved_jobs")
suspend fun getSavedJobs(): Response<SavedJobsResponse>
```

---

## Skills trend

### `GET api/get_skills_trend` *(auth)*

| Query param | Type | Notes |
|---|---|---|
| `limit` | int | Default `3`, clamped to `1..50` |

Returns the top `limit` skills by **missing** count merged with the top `limit` by **matched** count, so between `limit` and `2 × limit` skills. Each value is `[missing_occurrences, matched_occurrences]`.

```json
{
  "success": true,
  "skills": {
    "AWS": [7, 1],
    "Python": [0, 12]
  }
}
```

```kotlin
@Serializable
data class SkillsTrendResponse(val success: Boolean, val skills: Map<String, List<Int>>)

@GET("api/get_skills_trend")
suspend fun getSkillsTrend(@Query("limit") limit: Int = 3): Response<SkillsTrendResponse>
```

```kotlin
val skills = api.getSkillsTrend(limit = 5).body()?.skills.orEmpty()
val gaps = skills.entries.sortedByDescending { it.value[0] }.take(5)        // "Your gaps"
val demand = skills.entries.sortedByDescending { it.value[1] }.take(5)      // "All demand"
```

---

## Billing

Subscriptions go through Stripe's hosted pages. The app opens the returned `url` in a browser (Chrome Custom Tab); it never handles card details. When the user comes back to the app, call `get_user_tier_quota` to pick up the new tier.

### `POST api/create_checkout_session` *(auth)*

Request: `{"plan": "monthly"}` or `{"plan": "annual"}` (defaults to `monthly`).

| Status | Body |
|---|---|
| `200` | `{"success": true, "url": "https://checkout.stripe.com/..."}` |
| `400` | Unknown plan or malformed JSON |
| `502` | `{"error": "Could not start checkout"}` |

After checkout, Stripe sends the browser back to the **website**, not the app.

```kotlin
@Serializable
data class CheckoutRequest(val plan: String)        // "monthly" | "annual"

@Serializable
data class UrlResponse(val success: Boolean, val url: String)

@POST("api/create_checkout_session")
suspend fun createCheckoutSession(@Body body: CheckoutRequest): Response<UrlResponse>
```

```kotlin
val url = api.createCheckoutSession(CheckoutRequest("monthly")).body()?.url ?: return
CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
```

### `POST api/create_portal_session` *(auth)*

No body. Opens Stripe's billing portal, where a subscriber can manage or cancel their plan.

| Status | Body |
|---|---|
| `200` | `{"success": true, "url": "https://billing.stripe.com/..."}` |
| `400` | `{"message": "user stripe_customer_id not found"}` (the user has no subscription to manage) |

```kotlin
@POST("api/create_portal_session")
suspend fun createPortalSession(): Response<UrlResponse>
```

Only show "Manage subscription" to paid users.

---

## Health check

### `check_work` (any method)

Unauthenticated liveness probe. Returns `{"success": true}`. Useful as a "can I reach the server?" check before showing a network error.

```kotlin
@GET("check_work")
suspend fun healthCheck(): Response<SuccessResponse>
```
