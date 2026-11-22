package com.example.job_finder_app.data.remote

import com.example.job_finder_app.data.remote.model.AuthUserResponse
import com.example.job_finder_app.data.remote.model.GetSearchParametersResponse
import com.example.job_finder_app.data.remote.model.JobCardPage
import com.example.job_finder_app.data.remote.model.JobDetailDto
import com.example.job_finder_app.data.remote.model.JsonReportResponse
import com.example.job_finder_app.data.remote.model.LoginRequest
import com.example.job_finder_app.data.remote.model.ReportListResponse
import com.example.job_finder_app.data.remote.model.SendCodeRequest
import com.example.job_finder_app.data.remote.model.SkillsTrendResponse
import com.example.job_finder_app.data.remote.model.SuccessResponse
import com.example.job_finder_app.data.remote.model.UploadParametersRequest
import com.example.job_finder_app.data.remote.model.UploadParametersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit description of the Job Finder server API.
 * Paths are relative (no leading "/") because BuildConfig.BASE_URL ends in "/".
 * See Django_Server_README.md for every route's contract.
 */
interface JobFinderApi {
    @GET("api/auth/user")
    suspend fun authUser(): Response<AuthUserResponse>

    @POST("api/send_email_verification_code")
    suspend fun sendVerificationCode(@Body body: SendCodeRequest): Response<SuccessResponse>

    @POST("api/login")
    suspend fun login(@Body body: LoginRequest): Response<SuccessResponse>

    @POST("api/logout")
    suspend fun logout(): Response<SuccessResponse>

    @GET("api/get_report_jobs")
    suspend fun getReportJobs(
        @Query("limit") limit: Int = 3,
        @Query("cursor") cursor: String? = null
    ): Response<JobCardPage>

    @GET ("api/get_report_job_section/{jobId}")
    suspend fun getReportJobSection(@Path("jobId") jobId: String): Response<JobDetailDto>

    @GET("api/get_skills_trend")
    suspend fun getSkillsTrend(@Query("limit") limit: Int = 3): Response<SkillsTrendResponse>

    @GET("api/list_all_reports")
    suspend fun listAllReports(): Response<ReportListResponse>

    @GET ("api/getJsonReport")
    suspend fun getJsonReport(): Response<JsonReportResponse>

    @GET ("api/get_search_parameters")
    suspend fun getSearchParameters(): Response<GetSearchParametersResponse>
    
    @POST ("api/upload_parameters")
    suspend fun uploadParameters(@Body body: UploadParametersRequest): Response<UploadParametersResponse>
}
