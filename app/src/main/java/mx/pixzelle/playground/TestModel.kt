package mx.pixzelle.playground

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestModel : ViewModel() {
    val x = MutableLiveData(View.VISIBLE)
}