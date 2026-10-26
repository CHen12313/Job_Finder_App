package com.example.job_finder_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO (step 5 smoke test — TEMPORARY, delete once the network layer is verified):
        // type this out (then delete the comment markers). Needs these imports (Alt+Enter adds them):
        //   android.util.Log, androidx.lifecycle.lifecycleScope, kotlinx.coroutines.launch,
        //   okhttp3.HttpUrl.Companion.toHttpUrl, com.example.job_finder_app.data.remote.model.SendCodeRequest
        //
        // val container = (application as JobFinderApplication).container
        // val baseUrl = BuildConfig.BASE_URL.toHttpUrl()
        //
        // lifecycleScope.launch {
        //     // 0. Cookies loaded from disk at startup (empty on the very first run)
        //     Log.d("Smoke", "cookies at start: ${container.cookieJar.loadForRequest(baseUrl).map { it.name }}")
        //
        //     // 1. GET — expect 401 and a csrftoken cookie to be stored
        //     val auth = container.api.authUser()
        //     Log.d("Smoke", "authUser: ${auth.code()} ${auth.body() ?: auth.errorBody()?.string()}")
        //     Log.d("Smoke", "cookies after authUser: ${container.cookieJar.loadForRequest(baseUrl).map { it.name }}")
        //
        //     // 2. POST — expect 200 {"success": true}; proves the CSRF + Origin headers work
        //     //    (sends a real email — use your own address)
        //     val send = container.api.sendVerificationCode(SendCodeRequest("YOUR_EMAIL_HERE"))
        //     Log.d("Smoke", "sendCode: ${send.code()} ${send.body() ?: send.errorBody()?.string()}")
        // }

        enableEdgeToEdge()
        setContent {
            Job_Finder_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Job_Finder_AppTheme {
        Greeting("Android")
    }
}