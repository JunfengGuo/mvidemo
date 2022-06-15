package com.junfeng.mvidemo.demo2

import com.junfeng.mvidemo.ItemInfo

data class PageViewState(
    val itemList: List<ItemInfo> = listOf(),               // Item 信息列表
    val isRefreshing: Boolean = false,                     // 是否刷新中
    val isLoadingMore: Boolean = false,                    // 是否加载更多中
    val hasMore: Boolean = true,                           // 是否还有更多
    val buttonClickCount : Int = 0                         // button被点击的次数
)

