package mx.pixzelle.utils.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView?.getLastVisiblePosition() : Int {
    return (this?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
}

fun RecyclerView?.getFirstVisiblePosition() : Int {
    return (this?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
}
