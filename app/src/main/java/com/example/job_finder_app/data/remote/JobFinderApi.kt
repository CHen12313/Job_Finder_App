package com.example.job_finder_app.data.remote

import com.example.job_finder_app.data.remote.model.AuthUserResponse
import com.example.job_finder_app.data.remote.model.SendCodeRequest
import com.example.job_finder_app.data.remote.model.SuccessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit description of the Job Finder server API.
 * Paths are relative (no leading "/") because BuildConfig.BASE_URL ends in "/".
 * See Django_Server_README.md for every route's contract.
 */
interface JobFinderApi {

    // TODO: authUser() — GET api/auth/user → Response<AuthUserResponse>

    // TODO: sendVerificationCode(body) — POST api/send_email_verification_code → Response<SuccessResponse>

    // TODO: logout() — POST api/logout → Response<SuccessResponse>
}
