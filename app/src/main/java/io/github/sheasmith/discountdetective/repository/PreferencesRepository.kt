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
 * Comment todo
 */
@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getRegion(): LiveData<String> {

        return sharedPreferences.stringLiveData("city", Region.DUNEDIN)
    }
}