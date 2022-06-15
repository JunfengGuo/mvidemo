package com.junfeng.mvidemo.demo2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junfeng.mvidemo.ItemInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class PageViewModel : ViewModel() {
    companion object {
        private const val TAG = "PageViewModel"
    }

    val intent = Channel<PageIntent>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(PageViewState())
    val state: StateFlow<PageViewState>
        get() = _state

    init {
        handleIntent()

    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is PageIntent.LoadMore -> {
                        loadMore(it.num)
                    }
                    is PageIntent.Refresh -> {
                        refresh(it.num)
                    }
                    is PageIntent.ClickButton->{
                        clickButton()
                    }
                }
            }
        }
    }

    private fun clickButton(){
        Log.i(TAG, "clickButton()")
        val currentClickCount = _state.value.buttonClickCount
        _state.value = _state.value.copy(buttonClickCount = currentClickCount.inc())
    }

    private suspend fun loadMore(num: Int) {
        Log.i(TAG, "Start loading more")
        _state.value = _state.value.copy(isLoadingMore = true)

        delay(3000)
        Log.i(TAG, "Load more item")
        val result = mutableListOf<ItemInfo>()
        result.addAll(_state.value.itemList)
        result.addAll(generateList(_state.value.itemList.size, num))
        _state.value = _state.value.copy(itemList = result)

        Log.i(TAG, "stop loading more")
        _state.value = _state.value.copy(isLoadingMore = false)
    }

    private suspend fun refresh(num: Int) {
        Log.i(TAG, "Start refreshing")
        _state.value = _state.value.copy(isRefreshing = true)

        delay(3000)
        val newResult = generateList(0, num)
        Log.i(TAG, "Refresh item")
        _state.value = _state.value.copy(itemList = newResult)

        Log.i(TAG, "Stop refreshing")
        _state.value = _state.value.copy(isRefreshing = false)
    }

    private fun generateList(startIndex: Int, num: Int): List<ItemInfo> {
        val list = mutableListOf<ItemInfo>()
        for (i in startIndex..num + startIndex) {
            list.add(generateItem(i))
        }
        return list
    }

    private fun generateItem(order: Int): ItemInfo {
        return ItemInfo(name = "Item$order")
    }

}