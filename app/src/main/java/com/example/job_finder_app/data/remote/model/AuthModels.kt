package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The logged-in user, as returned inside `GET api/auth/user`. */
@Serializable
data class User(
    // TODO: id, username, user_email, user_subscription_type, user_cv_text,
    //       user_params (SearchParameters), user_rules (FilterRules),
    //       user_cv_set, user_search_set, first_report_generated, user_setup_complete
)

/** Response of `GET api/auth/user`. `user` is absent when logged out (401). */
@Serializable
data class AuthUserResponse(
    // TODO: authenticated, user
)

/** Body of `POST api/send_email_verification_code`. Note the JSON key is "user_email". */
@Serializable
data class SendCodeRequest(
    // TODO: email
)
