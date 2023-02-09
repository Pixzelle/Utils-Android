package mx.pixzelle.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import mx.pixzelle.utils.interfaces.OnSwipeRefreshListener

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, imageResource: Int?) {
    imageResource?.let {
        imageView.setImageResource(imageResource)
    }
}

@BindingAdapter("backgroundColor")
fun setLayoutColor(viewGroup: ViewGroup, color: Int) {
    viewGroup.setBackgroundColor(color)
}

@BindingAdapter("onSwipeRefreshed")
fun bindOnSwipeRefreshed(
    swipeRefreshLayout: SwipeRefreshLayout,
    onRefreshListener: OnSwipeRefreshListener,
) {
    swipeRefreshLayout.setOnRefreshListener {
        onRefreshListener.onRecyclerRefreshed(swipeRefreshLayout)
    }
}

@BindingAdapter("isEnabled")
fun setIsEnabled(view: View, isEnabled: Boolean?) {
    isEnabled?.let {
        view.isEnabled = isEnabled
    }
}

@BindingAdapter("isRefreshing")
fun setIsRefreshing(swipeRefreshLayout: SwipeRefreshLayout, isRefreshing: Boolean?) {
    isRefreshing?.let {
        swipeRefreshLayout.isRefreshing = it
    }
}

@BindingAdapter("app:tint")
fun setImageTint(imageView: ImageView, color: Int) {
    imageView.setColorFilter(ContextCompat.getColor(imageView.context, color))
}

@BindingAdapter(value = ["alphaVisibility", "alphaDuration"], requireAll = false)
fun setAlphaVisibility(
    view: View,
    visibility: Int,
    duration: Long = 250
) {
    val endAnimVisibility = view.getTag(R.id.finalVisibility) as Int?
    val oldVisibility = endAnimVisibility ?: view.visibility
    if (oldVisibility == visibility) {
        return
    }
    val isVisible = oldVisibility == View.VISIBLE
    val willBeVisible = visibility == View.VISIBLE
    view.visibility = View.VISIBLE
    var startAlpha = if (isVisible) 1f else 0f
    if (endAnimVisibility != null) {
        startAlpha = view.alpha
    }
    val endAlpha = if (willBeVisible) 1f else 0f

    val alpha: ObjectAnimator = ObjectAnimator.ofFloat(
        view,
        View.ALPHA, startAlpha, endAlpha
    )
    alpha.setAutoCancel(true)
    alpha.addListener(object : Animator.AnimatorListener {
        private var isCanceled = false

        override fun onAnimationStart(animator: Animator) {
            view.setTag(R.id.finalVisibility, visibility);
        }

        override fun onAnimationEnd(animator: Animator) {
            view.setTag(R.id.finalVisibility, null);
            if (!isCanceled) {
                view.alpha = 1f;
                view.visibility = visibility;
            }
        }

        override fun onAnimationCancel(p0: Animator) {
            isCanceled = true;
        }

        override fun onAnimationRepeat(p0: Animator) {

        }
    })
    alpha.duration = duration
    alpha.start()
}