package id.lukasdylan.grpc.protodroid.internal.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import id.lukasdylan.grpc.protodroid.internal.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

internal object MainScreen {
    const val SCREEN_NAME = "main"

    val simpleDateFormat = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.ENGLISH)
}

@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
    title: String,
    onSelectedDataLog: (ProtodroidDataEntity) -> Unit,
    clearNotifications: () -> Unit
) {
    val data = viewModel.dataResponse.collectAsState(emptyList(), Dispatchers.Main.immediate)
    val filterList =
        viewModel.filterListFlow.collectAsState(emptyList(), Dispatchers.Main.immediate)
    MainScreen(
        title = title,
        listOfDataLog = data.value,
        onSelectedDataLog = onSelectedDataLog,
        clearAll = {
            viewModel.deleteAllData()
            clearNotifications()
        },
        filter = { filterType -> viewModel.updateFilter(filterType) },
        currentFilters = filterList.value
    )
}

@Composable
private fun MainScreen(
    title: String,
    listOfDataLog: List<ProtodroidDataEntity>,
    lazyListState: LazyListState = rememberLazyListState(),
    clearAll: () -> Unit,
    filter: (filterType: MainViewModel.FilterType) -> Unit = {},
    currentFilters: List<MainViewModel.FilterType>,
    onSelectedDataLog: (ProtodroidDataEntity) -> Unit
) {
    val showMenu = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSystemInDarkTheme()) {
                    Color.Black
                } else {
                    Color.White
                },
                contentColor = MaterialTheme.colors.onSurface,
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$title GRPC Logger",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "by Protodroid",
                            style = MaterialTheme.typography.caption,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu.value = !showMenu.value }) {
                        Icon(
                            Icons.Outlined.List,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            filter(MainViewModel.FilterType.Unique)
                            showMenu.value = false
                        }) {
                            Text(
                                if (currentFilters.contains(MainViewModel.FilterType.Unique)) {
                                    "Show duplicates"
                                } else {
                                    "Show unique"
                                }
                            )
                        }
                        DropdownMenuItem(onClick = {
                            filter(MainViewModel.FilterType.Errors)
                            showMenu.value = false
                        }) {
                            Text(
                                if (currentFilters.contains(MainViewModel.FilterType.Errors)) {
                                    "Show successful"
                                } else {
                                    "Errors only"
                                }
                            )
                        }
                    }
                    IconButton(onClick = clearAll) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
            )
        }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(12.dp)
        ) {
            items(listOfDataLog) {
                DataLog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    entity = it,
                    onSelectedDataLog = onSelectedDataLog
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DataLog(
    modifier: Modifier = Modifier,
    entity: ProtodroidDataEntity,
    onSelectedDataLog: (ProtodroidDataEntity) -> Unit
) {
    Card(
        elevation = 3.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        onClick = { onSelectedDataLog(entity) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = entity.serviceName.split("/").lastOrNull() ?: entity.serviceName,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                text = "${entity.statusName} (${entity.statusCode})",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.subtitle1,
                color = when (entity.statusLevel) {
                    0 -> Color.DarkGray
                    1 -> Color(0xff669900)
                    else -> Color(0xffcc0000)
                }
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                text = "Last Updated: ${getFormattedDate(entity.lastUpdatedAt)}",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

private fun getFormattedDate(timeInMillis: Long): String {
    return MainScreen.simpleDateFormat.format(Date(timeInMillis))
}

@Composable
@Preview
private fun Preview_MainScreen() {
    MainScreen(
        title = "Virgo Dev",
        listOfDataLog = listOf(getDummyData(0), getDummyData(1)),
        onSelectedDataLog = {},
        clearAll = {},
        currentFilters = emptyList()
    )
}

private fun getDummyData(statusCode: Int): ProtodroidDataEntity {
    return ProtodroidDataEntity(
        id = 0,
        serviceUrl = "service url",
        serviceName = "/Service Name",
        requestHeader = "request header",
        responseHeader = "response header",
        requestBody = "request body",
        responseBody = "response body",
        statusCode = statusCode,
        statusLevel = 1,
        statusName = "OK",
        statusDescription = "status description",
        statusErrorCause = "status error cause"
    )
}