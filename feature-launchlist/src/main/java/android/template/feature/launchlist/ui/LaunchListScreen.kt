package android.template.feature.launchlist.ui

import android.template.feature.ui.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rocketreserver.LaunchListQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchListScreen(
    onLaunchClick: (launchId: String) -> Unit,
) {
    val viewModel: LaunchListViewModel = hiltViewModel()
    val launchListState by viewModel.launchList.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Launch List") }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            when(launchListState) {
                is LaunchListState.isLoading -> {
                    LoadingItem()
                }
                is LaunchListState.Error -> {
                    ErrorMessage(
                        text = (launchListState as LaunchListState.Error).errorMessage
                    )
                }
                is LaunchListState.Success -> {
                    val launchList = (launchListState as LaunchListState.Success).data
                    LaunchListScreenContent(
                        modifier = Modifier,
                        launchList = launchList,
                        onLaunchClick = onLaunchClick,
                        loadMore = { viewModel.loadMore() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchListScreenContent(
    modifier: Modifier = Modifier,
    launchList: List<LaunchListQuery.Launch> = emptyList(),
    onLaunchClick: (launchId: String) -> Unit,
    loadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(launchList) {launch ->
            LaunchItem(launch = launch, onClick = onLaunchClick)
        }
        item {
            LoadingItem()
        }
    }

    if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == launchList.size - 1) {
        LaunchedEffect(Unit) {
            loadMore()
        }
    }
}

@Composable
private fun LaunchItem(launch: LaunchListQuery.Launch, onClick: (launchId: String) -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick(launch.id) },
        headlineContent = {
             //Mission name
            Text(text = launch.mission?.name ?: "")
        },
        supportingContent = {
            Text(text = launch.site ?: "")
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier.size(68.dp, 68.dp),
                model = launch.mission?.missionPatch,
                contentDescription = "Mission patch",
                error =  painterResource(R.drawable.ic_placeholder),
                placeholder = painterResource(R.drawable.ic_placeholder),
            )
        }
    )
}

@Composable
private fun LoadingItem() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}
