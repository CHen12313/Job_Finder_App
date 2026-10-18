package com.example.job_finder_app

import com.example.job_finder_app.data.remote.model.ApiError
import com.example.job_finder_app.data.remote.model.AuthUserResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Checks the API models parse real server JSON (no network needed). */
class ModelParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesLoggedInUser() {
         val raw = """
             {
               "authenticated": true,
               "user": {
                 "id": 1,
                 "username": "ling",
                 "user_email": "ling@example.com",
                 "user_subscription_type": "free",
                 "user_cv_text": null,
                 "user_params": {
                   "region": "gb",
                   "query": "android developer",
                   "where": "London",
                   "extra_parameters": { "sort_by": "date" }
                 },
                 "user_rules": {
                   "filter_mode": "blacklist",
                   "blacklist_companies": ["Meta"],
                   "whitelist_companies": []
                 },
                 "user_cv_set": false,
                 "user_search_set": true,
                 "first_report_generated": false,
                 "user_setup_complete": false
               }
             }
        """.trimIndent()

        val response = json.decodeFromString<AuthUserResponse>(raw)

        assertTrue(response.authenticated)
        val user = response.user
        assertNotNull(user)
        assertEquals("ling@example.com", user!!.userEmail)
        assertNull(user.cvText)
        assertEquals("London", user.params?.where)
        assertEquals(listOf("Meta"), user.rules?.blacklistCompanies)
        assertTrue(user.searchSet)
    }

    @Test
    fun parsesLoggedOutResponse() {
        val response = json.decodeFromString<AuthUserResponse>("""{"authenticated": false}""")
        assertFalse(response.authenticated)
        assertNull(response.user)
    }

    @Test
    fun apiErrorPicksWhicheverFieldIsSet() {
         val login = json.decodeFromString<ApiError>("""{"error": "Invalid credentials"}""")
         val code = json.decodeFromString<ApiError>("""{"success": false, "message": "Too many attempts"}""")
         val empty = json.decodeFromString<ApiError>("""{}""")

        assertEquals("Invalid credentials", login.text)
         assertEquals("Too many attempts", code.text)
         assertEquals("unknown error", empty.text)
    }
}
