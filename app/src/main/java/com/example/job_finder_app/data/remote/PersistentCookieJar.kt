package com.example.job_finder_app.data.remote

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import androidx.core.content.edit
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * Stores the server's cookies (sessionid, csrftoken) and keeps them across app restarts.
 *
 * OkHttp calls [saveFromResponse] after every response that has Set-Cookie headers,
 * and [loadForRequest] before every request to decide which cookies to send.
 * Cookies are cached in memory and mirrored to SharedPreferences as JSON.
 */

class PersistentCookieJar(context: Context) : CookieJar {

    private val prefs = context.getSharedPreferences("cookies", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private val store: MutableMap<String, Cookie> = loadFromDisk()

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies){
            if (cookie.isExpired()){
                store.remove(keyOf(cookie))
            } else {
                store[keyOf(cookie)] = cookie
            }
        }
        saveToDisk()
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val removedAny = store.entries.removeAll {it.value.isExpired()}
        if (removedAny) saveToDisk()
        return store.values.filter {it.matches(url)}
    }

    //forget cookie when logout or rejected
    @Synchronized
    fun clear() {
        store.clear()
        prefs.edit { remove(PREFS_KEY) }
    }

    private fun keyOf(cookie: Cookie): String {
         return "${cookie.name}|${cookie.domain}|${cookie.path}"
    }

    private fun loadFromDisk(): MutableMap<String, Cookie> {
        val raw = prefs.getString(PREFS_KEY, null)?: return mutableMapOf()
        //runCatching is for running whatever is inside and catch the exceptions.
        // getOrElse is to either return the successful response or whatever inside
        val stored = runCatching {json.decodeFromString<List<StoredCookie>>(raw)}.getOrElse { emptyList() }
        return stored.map{ it.toCookie() }.associateBy { keyOf(it) }.toMutableMap()
    }

    private fun saveToDisk() {
        val raw = json.encodeToString(store.values.map { it.toStored() })
        prefs.edit { putString(PREFS_KEY, raw) }
    }

    private fun Cookie.isExpired(now: Long = System.currentTimeMillis()): Boolean {

         return expiresAt <= now
    }

     private companion object {
         const val PREFS_KEY = "cookies"
     }
}
//Disk format of one cookie. okhttp3.Cookie isn't @Serializable, so we copy its fields into this class.
@Serializable
private data class StoredCookie(
    val name: String,
    val value: String,
    val expiresAt: Long,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val httpOnly: Boolean,
    val hostOnly: Boolean,
    val persistent: Boolean
)

private fun Cookie.toStored(): StoredCookie {
     return StoredCookie(
         name = name,
         value = value,
         expiresAt = expiresAt,
         domain = domain,
         path = path,
         secure = secure,
         httpOnly = httpOnly,
         hostOnly = hostOnly,
         persistent = persistent,
     )

}

private fun StoredCookie.toCookie(): Cookie {
    val builder = Cookie.Builder().name(name).value(value).path(path)
    if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
    if (persistent) builder.expiresAt(expiresAt)
    if (secure) builder.secure()
    if (httpOnly) builder.httpOnly()
    return builder.build()
}
