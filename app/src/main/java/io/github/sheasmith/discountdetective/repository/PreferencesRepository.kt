package io.github.sheasmith.discountdetective.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.example.cosc345.shared.models.Region
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.sheasmith.discountdetective.settings.stringLiveData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contains functions for getting the region preference and dark mode preference.
 *
 * @param context State of the app.
 */
@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Gets the region preference value that the user has set using the live data class.
     *
     * @return A live data string of the current region.
     */
    fun getRegion(): LiveData<String> {

        return sharedPreferences.stringLiveData("city", Region.DUNEDIN)
    }

    /**
     * Gets the dark/light mode preference value that the user has set using the live data class.
     *
     * @return A live data string of the current dark/light mode.
     */
    fun getDarkMode(): LiveData<String> {
        return sharedPreferences.stringLiveData("dark_mode", "system")
    }
}