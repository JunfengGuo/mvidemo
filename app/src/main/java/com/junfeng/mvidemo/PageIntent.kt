package com.junfeng.mvidemo.demo2

sealed class PageIntent {
    data class Refresh(val num: Int) : PageIntent()     // 刷新
    data class LoadMore(val num: Int) : PageIntent()    // 加载更多
    object ClickButton : PageIntent()                   // 点击按钮
}


