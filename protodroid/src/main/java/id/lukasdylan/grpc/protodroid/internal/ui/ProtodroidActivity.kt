package id.lukasdylan.grpc.protodroid.internal.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import id.lukasdylan.grpc.protodroid.Protodroid
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepository
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepositoryImpl
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModel
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModelFactory
import id.lukasdylan.grpc.protodroid.internal.viewmodel.MainViewModel
import id.lukasdylan.grpc.protodroid.internal.viewmodel.MainViewModelFactory

internal class ProtodroidActivity : ComponentActivity() {

    private val repository: InternalProtodroidRepository by lazy {
        val dao = Protodroid.getInstance(this).protodroidDao
        return@lazy InternalProtodroidRepositoryImpl(dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colors = if (isSystemInDarkTheme()) {
                darkColors()
            } else {
                lightColors()
            }
            MaterialTheme(colors = colors) {
                val navHostController = rememberNavController()
                NavHost(
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    navController = navHostController,
                    startDestination = MainScreen.SCREEN_NAME
                ) {
                    composable(route = MainScreen.SCREEN_NAME) {
                        val mainViewModel by viewModels<MainViewModel>(factoryProducer = {
                            MainViewModelFactory(repository)
                        })
                        MainScreen(
                            viewModel = mainViewModel,
                            title = applicationInfo.loadLabel(packageManager).toString(),
                            onSelectedDataLog = {
                                navHostController.navigate(route = "${DetailScreen.SCREEN_NAME}?id=${it.id}")
                            },
                            onClearAllDataListener = {
                                val notificationManager =
                                    NotificationManagerCompat.from(applicationContext)
                                notificationManager.cancelAll()
                            }
                        )
                    }
                    composable(
                        route = "${DetailScreen.SCREEN_NAME}?id={id}",
                        deepLinks = listOf(navDeepLink {
                            uriPattern = DetailScreen.deeplink("{id}")
                        })
                    ) {
                        val dataId = it.arguments?.getString("id")?.toLong() ?: 0L
                        val detailViewModel by viewModels<DetailViewModel>(factoryProducer = {
                            DetailViewModelFactory(repository)
                        })
                        DetailScreen(
                            viewModel = detailViewModel,
                            dataId = dataId,
                            onCopyListener = { label, text ->
                                val clipboard =
                                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("protodroid $label", text)
                                clipboard.setPrimaryClip(clip)
                            },
                            onBackListener = {
                                navHostController.navigateUp()
                            })
                    }
                }
            }
        }
    }

    // https://issuetracker.google.com/issues/139738913
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && isTaskRoot) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}