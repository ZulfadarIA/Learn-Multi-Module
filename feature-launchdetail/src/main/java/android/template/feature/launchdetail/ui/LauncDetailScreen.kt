package android.template.feature.launchdetail.ui

import android.template.core.ui.MyApplicationTheme
import android.template.feature.ui.launchdetail.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import com.example.rocketreserver.LaunchDetailQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchDetailScreen(
    launchId: String,
    navigateToLogin: () -> Unit
) {
    val viewModel: LaunchDetailViewModel = hiltViewModel()
    val launchDetailState by viewModel.launchDetail.collectAsStateWithLifecycle()
    
    LaunchedEffect(launchId) {
        if (viewModel.launchDetail.value !is LaunchDetailState.Success) {
            viewModel.loadLaunchDetail(launchId)
        }
    }

    Scaffold(
        topBar =  {
            TopAppBar(
                title = {
                    Text("Launch Detail")
                },
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            when(launchDetailState) {
                is LaunchDetailState.isLoading -> {
                    Loading()
                }
                is LaunchDetailState.Error -> {
                    ErrorMessage(
                        text = (launchDetailState as LaunchDetailState.Error).errorMessage
                    )
                }
                is LaunchDetailState.Success -> {
                    val launchDetailData = (launchDetailState as LaunchDetailState.Success).data
                    LaunchDetailScreenContent(
                        modifier = Modifier,
                        data = launchDetailData,
                        navigateToLogin = navigateToLogin
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchDetailScreenContent(
    modifier: Modifier = Modifier,
    data: LaunchDetailQuery.Data, navigateToLogin: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mission patch
            AsyncImage(
                modifier = Modifier.size(160.dp, 160.dp),
                model = data?.launch?.mission?.missionPatch,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_placeholder),
                contentDescription = "Mission patch"
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column {
                // Mission name
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    text = data?.launch?.mission?.name ?: ""
                )

                // Rocket name
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    text = data?.launch?.rocket?.name?.let { "🚀 $it" } ?: "",
                )

                // Site
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = data?.launch?.site ?: "",
                )
            }
        }

        // Book button
        Button(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            onClick = {
            }
        ) {
            Text(text = "Book now")
        }
    }
}

@Composable
private fun ErrorMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}

@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SmallLoading() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = LocalContentColor.current,
        strokeWidth = 2.dp,
    )
}

@Preview
@Composable
private fun LaunchDetalScreenContentPrev() {
    MyApplicationTheme {
        LaunchDetailScreen(
            launchId = "42",
            navigateToLogin = {}
        )
    }
}