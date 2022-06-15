package com.junfeng.mvidemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.junfeng.mvidemo.demo2.PageIntent
import com.junfeng.mvidemo.demo2.PageViewModel
import com.junfeng.mvidemo.demo2.PageViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PageActivity : ComponentActivity() {

    companion object {
        private const val TAG = "PageActivity"
    }

    private val viewModel by lazy { ViewModelProvider(this)[PageViewModel::class.java] }

    private var state by mutableStateOf(PageViewState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleViewState()

        setContent {
            Scaffold(topBar = {
                TopAppBar(title = { Text(text = "Page") })
            }) {
                Column {
                    Button(onClick = {
                        lifecycleScope.launch { viewModel.intent.send(PageIntent.ClickButton) }
                    }, modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()) {
                        TestTextCompose(clickCount = state.buttonClickCount)
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    SwipeRefresh(
                        state = SwipeRefreshState(state.isRefreshing), onRefresh = {
                            lifecycleScope.launch { viewModel.intent.send(PageIntent.Refresh(10)) }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        ItemList(itemList = state.itemList, hasMore = state.hasMore, loadingMore = state.isLoadingMore)
                    }
                }
            }
        }

        Log.i(TAG,"onCreate()")
        lifecycleScope.launch {
            viewModel.intent.send(PageIntent.Refresh(10))
        }
    }

    @Composable
    private fun TestTextCompose(clickCount:Int) {
        Log.i(TAG, "TestTextCompose clickCount = $clickCount")
        Text(text = "click count : $clickCount")
    }

    private fun handleViewState() {
        lifecycleScope.launch {
            viewModel.state.collect {
                state = it
            }
        }
    }

    @Composable
    fun ItemList(itemList: List<ItemInfo>, loadingMore: Boolean, hasMore: Boolean) {
        LazyColumn {
            items(itemList) { item ->
//                Log.i(TAG, "items "+ item.name)
                ItemCard(item = item)
            }

            if (itemList.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val text = if (hasMore) {
                            if (loadingMore) {
                                "加载中"
                            } else {
                                "上拉加载更多"
                            }
                        } else "没有更多了"
                        Text(text = text)
                    }
                }
                item {
                    if (hasMore && !loadingMore)
                        LaunchedEffect(Unit) {
                            viewModel.intent.send(PageIntent.LoadMore(5))
                        }
                }
            }
        }
    }

    @Composable
    fun ItemCard(item: ItemInfo) {
        Log.i(TAG, "ItemCard " + item.name)

        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .fillMaxWidth()
                .height(60.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp))
                .padding(horizontal = 8.dp)
        ) {
            val (name) = createRefs()

            Text(
                text = item.name,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
    }
}