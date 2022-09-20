package io.github.sheasmith.discountdetective.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.view.updatePadding
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import io.github.sheasmith.discountdetective.R

/**
 * The fragment responsible for inflating the settings page, which uses AndroidX preferences.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    /**
     * Creates the preferences options from the specified XML file.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener { preferences, key ->

        }
    }

    /**
     * Creates the recyclerview for the preferences items. We use this to disable nested scrolling.
     */
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

    /**
     * Creates the overall view for the settings screen.
     */
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