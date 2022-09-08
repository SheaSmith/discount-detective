@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345.scraperapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.example.cosc345.scraperapp.ui.theme.DiscountDetectiveTheme
import com.example.cosc345.scraperapp.workers.BarcodeMergeWorker
import com.example.cosc345.scraperapp.workers.SaveToFirebaseWorker
import com.example.cosc345.scraperapp.workers.ScraperWorker
import com.example.cosc345.scraperapp.workers.ValuesMergeWorker
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        viewModel.handleLogin(res)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DiscountDetectiveTheme {
                MainScreen(signInLauncher = signInLauncher)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    signInLauncher: ActivityResultLauncher<Intent>?
) {
    val context = LocalContext.current
    // A surface container using the 'background' color from the theme
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Discount Detective Scraper") }
            )
        },
        content = { innerPadding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // https://stackoverflow.com/questions/64362801/how-to-handle-visibility-of-a-text-in-jetpack-compose
                AnimatedVisibility(visible = viewModel.showLogin) {
                    Button(onClick = {
                        if (signInLauncher != null)
                            viewModel.launchLogin(signInLauncher)
                    }) {
                        Text(text = "Login")
                    }
                }
                Button(onClick = {
                    val workRequest = OneTimeWorkRequestBuilder<ScraperWorker>()
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    WorkManager
                        .getInstance(context)
                        .cancelAllWork()

                    WorkManager
                        .getInstance(context)
                        .enqueueUniqueWork("scraper", ExistingWorkPolicy.REPLACE, workRequest)
                }) {
                    Text(text = "Run scraper now")
                }
                Button(onClick = {
                    val workRequest = PeriodicWorkRequestBuilder<ScraperWorker>(Duration.ofDays(1))
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    val workManager = WorkManager.getInstance(context)

                    workManager.cancelAllWork()

                    workManager.enqueueUniquePeriodicWork(
                        "scraper",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest
                    )
                }) {
                    Text(text = "Schedule scraper")
                }

                Button(onClick = {
                    val workRequest = OneTimeWorkRequestBuilder<BarcodeMergeWorker>()
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    WorkManager
                        .getInstance(context)
                        .cancelAllWork()

                    WorkManager
                        .getInstance(context)
                        .enqueueUniqueWork("barcode", ExistingWorkPolicy.REPLACE, workRequest)
                }) {
                    Text(text = "Run barcode matcher")
                }

                Button(onClick = {
                    val workRequest = OneTimeWorkRequestBuilder<ValuesMergeWorker>()
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    WorkManager
                        .getInstance(context)
                        .cancelAllWork()

                    WorkManager
                        .getInstance(context)
                        .enqueueUniqueWork("values", ExistingWorkPolicy.REPLACE, workRequest)
                }) {
                    Text(text = "Run value matcher")
                }

                Button(onClick = {
                    val workRequest = OneTimeWorkRequestBuilder<SaveToFirebaseWorker>()
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Duration.ofMinutes(30)
                        )
                        .build()

                    WorkManager
                        .getInstance(context)
                        .cancelAllWork()

                    WorkManager
                        .getInstance(context)
                        .enqueueUniqueWork("firebase", ExistingWorkPolicy.REPLACE, workRequest)
                }) {
                    Text(text = "Run Firebase uploader")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiscountDetectiveTheme {
        MainScreen(signInLauncher = null)
    }
}