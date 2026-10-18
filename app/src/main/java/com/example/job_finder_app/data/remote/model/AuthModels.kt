package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The logged-in user, as returned inside `GET api/auth/user`. */
@Serializable
data class User(
    val id: Int,
    val username: String,
    @SerialName("user_email") val userEmail: String,
    @SerialName("user_subscription_type") val subscriptionType: String = "free",
    @SerialName("user_cv_text") val cvText: String? = null,
    @SerialName("user_params") val params: SearchParameters? = null,
    @SerialName("user_rules") val rules: FilterRules? = null,
    @SerialName("user_cv_set") val cvSet: Boolean = false,
    @SerialName("user_search_set") val searchSet: Boolean = false,
    @SerialName("first_report_generated") val firstReportGenerated: Boolean = false,
    @SerialName("user_setup_complete") val setupComplete: Boolean? = null
)

/** Response of `GET api/auth/user`. `user` is absent when logged out (401). */
@Serializable
data class AuthUserResponse(
    val authenticated: Boolean,
    val user: User? = null
)

/** Body of `POST api/send_email_verification_code`. Note the JSON key is "user_email". */
@Serializable
data class SendCodeRequest(
    @SerialName("user_email") val email: String
)
