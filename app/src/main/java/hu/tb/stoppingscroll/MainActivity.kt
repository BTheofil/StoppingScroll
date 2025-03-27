package hu.tb.stoppingscroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import hu.tb.stoppingscroll.ui.theme.StoppingScrollTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoppingScrollTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val listState = rememberLazyListState()

    val mockList = mutableListOf<String>()
    repeat(100) {
        mockList.add(it.toString())
    }
    val scope = rememberCoroutineScope()

    var userScrollEnabled by remember {
        mutableStateOf(true)
    }

    val isReachPromotedItem by remember {
        derivedStateOf {
            val fullyVisibleItemKey = if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                listState.layoutInfo.visibleItemsInfo.filter { item ->
                    val containerHeight = listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
                    item.offset >= 0 && (item.offset + item.size) <= containerHeight
                }.map { it.key }.first()
            } else {
                -1
            }
            fullyVisibleItemKey.toString().toInt() == SELECTED_ITEM_INDEX
        }
    }

    LaunchedEffect(isReachPromotedItem) {
        if (isReachPromotedItem) {
            scope.launch {
                listState.stopScroll()
            }
            scope.launch {
                userScrollEnabled = false
                delay(500)
                userScrollEnabled = true
            }
        }
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = listState,
            userScrollEnabled = userScrollEnabled
        ) {
            items(
                items = mockList,
                key = { it }
            ) { item ->
                if (item.toInt() == SELECTED_ITEM_INDEX) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    ) {
                        Text(item)
                    }
                } else {
                    ListItem(
                        headlineContent = {
                            Text(item)
                        }
                    )
                }
            }
        }
    }
}

private const val SELECTED_ITEM_INDEX = 50

@Preview()
@Composable
fun GreetingPreview() {
    StoppingScrollTheme {
        MainScreen()
    }
}