package com.example.cosc345project.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.view.updatePadding
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cosc345project.R
import com.google.android.material.appbar.AppBarLayout

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val view = super.onCreateRecyclerView(inflater, parent, savedInstanceState)

        val params = view.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.layoutParams = params

        view.isNestedScrollingEnabled = false

        return view
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val appBar = view.findViewById<AppBarLayout>(R.id.appbar_layout)
        val appBarTopPadding = appBar.paddingTop

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowService = requireContext().getSystemService(Context.WINDOW_SERVICE)
            (windowService as WindowManager).apply {
                val statusBar =
                    currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars())
                val statusBarHeight = statusBar.top
                appBar.updatePadding(top = appBarTopPadding + statusBarHeight)
            }
        } else {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }

            appBar.updatePadding(top = appBarTopPadding + result)
        }

        return view
    }
}