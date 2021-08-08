package id.lukasdylan.grpc.protodroid.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object DetailScreen {
    const val SCREEN_NAME = "detail"
}

internal typealias LabelValue = Pair<String, String>

@Composable
internal fun DetailScreen(
    viewModel: DetailViewModel,
    dataId: Long,
    onCopyListener: (String) -> Unit,
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
        title = viewModel.title.collectAsState(initial = "").value,
        dataOverviews = viewModel.overviewInfo.collectAsState(initial = emptyList()).value,
        dataRequest = viewModel.requestInfo.collectAsState(initial = emptyList()).value,
        dataResponse = viewModel.responseInfo.collectAsState(initial = emptyList()).value,
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
    onCopyListener: (String) -> Unit,
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
                backgroundColor = if (isSystemInDarkTheme()) {
                    Color.Black
                } else {
                    Color.White
                },
                contentColor = MaterialTheme.colors.onSurface,
                title = {
                    Text(
                        text = title,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackListener) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "")
                    }
                }
            )
        }) {
        val tabMenus = listOf("Overview", "Request", "Response")
        val pagerState = rememberPagerState(
            pageCount = tabMenus.size,
            initialOffscreenLimit = tabMenus.size - 1,
            initialPage = 0
        )
        val tabIndex = pagerState.currentPage
        DetailTabMenu(
            pagerState = pagerState,
            tabMenus = tabMenus,
            dataOverviews = dataOverviews,
            dataRequest = dataRequest,
            dataResponse = dataResponse,
            selectedTabIndex = tabIndex,
            onCopyListener = {
                snackbarVisibleState.value = true
                onCopyListener.invoke(it)
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
    selectedTabIndex: Int = 0,
    onCopyListener: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        TabRow(
            backgroundColor = if (isSystemInDarkTheme()) {
                Color.Black
            } else {
                Color.White
            },
            contentColor = MaterialTheme.colors.onSurface,
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            tabMenus.forEachIndexed { index, title ->
                Tab(selected = selectedTabIndex == index, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = {
                    Text(
                        text = title.uppercase(),
                        color = MaterialTheme.colors.onSurface,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
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
                    Button(
                        onClick = {
                            val formattedText = data.joinToString("\n") {
                                "${it.first} = ${it.second}"
                            }
                            onCopyListener(formattedText)
                        },
                        elevation = null,
                        colors = ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        content = {
                            Text(
                                text = "Copy Screen Information ".uppercase(),
                                style = MaterialTheme.typography.button
                            )
                        })
                }

            }
        }
    }
}

@Composable
private fun DetailItem(labelValue: LabelValue) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val labelBackgroundColor = if (isSystemInDarkTheme()) {
            Color.Black
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
@Preview
private fun Preview_DetailScreen() {
    DetailScreen(
        title = "Service Name",
        dataOverviews = listOf("Label" to "Value", "Test" to "Test"),
        dataRequest = emptyList(),
        dataResponse = emptyList(),
        onCopyListener = {},
        onBackListener = {}
    )
}