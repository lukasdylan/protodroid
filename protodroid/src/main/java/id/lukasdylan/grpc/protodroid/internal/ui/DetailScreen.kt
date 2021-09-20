package id.lukasdylan.grpc.protodroid.internal.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object DetailScreen {
    const val SCREEN_NAME = "detail"

    fun deeplink(dataId: String): String {
        return "protodroid://$SCREEN_NAME/$dataId"
    }
}

internal typealias LabelValue = Pair<String, String>

@Composable
internal fun DetailScreen(
    viewModel: DetailViewModel,
    dataId: Long,
    onCopyListener: (String, String) -> Unit,
    onBackListener: () -> Unit
) {
    val scope = rememberCoroutineScope()
    BackHandler {
        scope.launch {
            onBackListener.invoke()
        }
    }

    viewModel.fetchDetail(dataId = dataId)

    DetailScreen(
        title = viewModel.title.collectAsState(initial = "", Dispatchers.Main.immediate).value,
        dataOverviews = viewModel.overviewInfo.collectAsState(
            initial = emptyList(),
            Dispatchers.Main.immediate
        ).value,
        dataRequest = viewModel.requestInfo.collectAsState(
            initial = emptyList(),
            Dispatchers.Main.immediate
        ).value,
        dataResponse = viewModel.responseInfo.collectAsState(
            initial = emptyList(),
            Dispatchers.Main.immediate
        ).value,
        onBackListener = onBackListener,
        onCopyListener = onCopyListener
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DetailScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    title: String,
    dataOverviews: List<LabelValue>,
    dataRequest: List<LabelValue>,
    dataResponse: List<LabelValue>,
    onCopyListener: (String, String) -> Unit,
    onBackListener: () -> Unit
) {
    val snackbarVisibleState = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                title = {
                    Text(
                        text = title,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackListener) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "")
                    }
                }
            )
        }) {
        val tabMenus = listOf("Overview", "Request", "Response")
        val pagerState = rememberPagerState(
            pageCount = tabMenus.size,
            initialOffscreenLimit = tabMenus.lastIndex,
            initialPage = 0
        )

        DetailTabMenu(
            pagerState = pagerState,
            tabMenus = tabMenus,
            dataOverviews = dataOverviews,
            dataRequest = dataRequest,
            dataResponse = dataResponse,
            onCopyListener = { label, value ->
                snackbarVisibleState.value = true
                onCopyListener.invoke(label, value)
            }
        )
    }

    LaunchedEffect(key1 = snackbarVisibleState.value) {
        if (snackbarVisibleState.value) {
            launch {
                val result = scaffoldState.snackbarHostState.showSnackbar("Copied successfully!")
                delay(300)
                if (result == SnackbarResult.Dismissed) {
                    snackbarVisibleState.value = false
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DetailTabMenu(
    pagerState: PagerState,
    tabMenus: List<String>,
    dataOverviews: List<LabelValue>,
    dataRequest: List<LabelValue>,
    dataResponse: List<LabelValue>,
    onCopyListener: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        TabRow(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            selectedTabIndex = 0,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            tabMenus.forEachIndexed { index, title ->
                Tab(selected = 0 == index, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = {
                    Text(
                        text = title.uppercase(),
                        color = MaterialTheme.colors.onSurface,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                })
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            LazyColumn(state = rememberLazyListState(), modifier = Modifier.fillMaxSize()) {
                val data = when (index) {
                    0 -> dataOverviews
                    1 -> dataRequest
                    2 -> dataResponse
                    else -> emptyList()
                }

                items(data) { item ->
                    DetailItem(labelValue = item)
                }

                item {
                    CopyItem {
                        val formattedText = data.joinToString("\n") {
                            "${it.first} = ${it.second}"
                        }
                        onCopyListener(tabMenus[index], formattedText)
                    }
                }

            }
        }
    }
}

@Composable
private fun DetailItem(labelValue: LabelValue) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val labelBackgroundColor = if (isSystemInDarkTheme()) {
            Color(0xFF333333)
        } else {
            Color(0xFFEEEEEE)
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(labelBackgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            text = labelValue.first,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body1
        )
        Text(
            text = labelValue.second,
            style = MaterialTheme.typography.body1,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CopyItem(onCopyListener: () -> Unit) {
    TextButton(
        onClick = onCopyListener,
        colors = ButtonDefaults.outlinedButtonColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        content = {
            Text(
                text = "Copy Screen Information ".uppercase(),
                style = MaterialTheme.typography.button,
            )
        },
        contentPadding = PaddingValues(0.dp)
    )
}

@Composable
@Preview
private fun Preview_DetailScreen() {
    DetailScreen(
        title = "Service Name",
        dataOverviews = listOf("Label" to "Value", "Test" to "Test"),
        dataRequest = emptyList(),
        dataResponse = emptyList(),
        onCopyListener = { _, _ -> },
        onBackListener = {}
    )
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun Preview_DetailScreen_DarkMode() {
    DetailScreen(
        title = "Service Name",
        dataOverviews = listOf("Label" to "Value", "Test" to "Test"),
        dataRequest = emptyList(),
        dataResponse = emptyList(),
        onCopyListener = { _, _ -> },
        onBackListener = {}
    )
}