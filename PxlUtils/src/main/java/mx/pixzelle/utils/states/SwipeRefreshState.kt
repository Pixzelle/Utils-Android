package mx.pixzelle.utils.states

import android.view.View

enum class SwipeRefreshState(
    val swipeEnabled: Boolean,
    val loadingVisibility: Int,
    val recyclerVisibility : Int,
    val refreshing: Boolean
) {
    LOADING(swipeEnabled = false, loadingVisibility = View.VISIBLE, recyclerVisibility = View.GONE,refreshing = false),
    REFRESHING(swipeEnabled = true, loadingVisibility = View.GONE, recyclerVisibility = View.VISIBLE,refreshing = true),
    NORMAL(swipeEnabled = true, loadingVisibility = View.GONE, recyclerVisibility = View.VISIBLE, refreshing = false),
    DISABLED(swipeEnabled = false, loadingVisibility = View.GONE, recyclerVisibility = View.VISIBLE, refreshing = false)
}