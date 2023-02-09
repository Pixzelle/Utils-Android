package mx.pixzelle.utils.interfaces

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

interface OnSwipeRefreshListener {
    fun onRecyclerRefreshed(swipeRefreshLayout: SwipeRefreshLayout)
}