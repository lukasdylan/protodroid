package id.lukasdylan.grpc.protodroid.internal.ui

import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Lukas Dylan on 05/05/21.
 */
fun <T : View> RecyclerView.ViewHolder.bindItem(@IdRes resId: Int) = lazy {
    itemView.findViewById<T>(resId)
}

fun <T : View> AppCompatActivity.bind(@IdRes resId: Int) = lazy {
    findViewById<T>(resId)
}

fun <T : View> Fragment.bind(@IdRes resId: Int) = lazy {
    requireView().findViewById<T>(resId)
}