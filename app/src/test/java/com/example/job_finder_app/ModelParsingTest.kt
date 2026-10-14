package com.example.job_finder_app

import com.example.job_finder_app.data.remote.model.AuthUserResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Checks the API models parse real server JSON (no network needed). */
class ModelParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesLoggedInUser() {
        // TODO: raw JSON string of a logged-in `auth/user` response (copy from Django_Server_README.md),
        //       decode it into AuthUserResponse, and assert a few fields
    }

    @Test
    fun parsesLoggedOutResponse() {
        // TODO: decode {"authenticated": false} and assert that user is null
    }
}
