package com.example.cosc345.scraperapp.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.example.cosc345.scraperapp.TemporaryDatabase
import com.example.cosc345.scraperapp.dao.RetailerDao
import com.example.cosc345.scraperapp.dao.RetailerProductInformationDao
import com.example.cosc345.scraperapp.dao.StoreDao
import com.example.cosc345.scraperapp.dao.StorePricingInformationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides access to the temporary database for dependency injection.
 */
// https://svvashishtha.medium.com/using-room-with-hilt-cb57a1bc32f
@Module
@InstallIn(SingletonComponent::class)
object TemporaryDatabaseModule {
    /**
     * Provides the retailer DAO for dependency injection.
     *
     * @param temporaryDatabase The database dependency required.
     * @return An instance of the [RetailerDao] for making retailer related DB operations.
     */
    @Provides
    fun provideRetailerDao(temporaryDatabase: TemporaryDatabase): RetailerDao =
        temporaryDatabase.retailerDao()

    /**
     * Provides the store DAO for dependency injection.
     *
     * @param temporaryDatabase The database dependency required.
     * @return An instance of the [StoreDao] for making store related DB operations.
     */
    @Provides
    fun provideStoreDao(temporaryDatabase: TemporaryDatabase): StoreDao =
        temporaryDatabase.storeDao()

    /**
     * Provides the retailer product information DAO for dependency injection.
     *
     * @param temporaryDatabase The database dependency required.
     * @return An instance of the [RetailerProductInformationDao] for making retailer product information related DB operations.
     */
    @Provides
    fun provideProductInformationDao(temporaryDatabase: TemporaryDatabase): RetailerProductInformationDao =
        temporaryDatabase.retailerProductInformationDao()

    /**
     * Provides the store pricing information DAO for dependency injection.
     *
     * @param temporaryDatabase The database dependency required.
     * @return An instance of the [StorePricingInformationDao] for making store pricing information related DB operations.
     */
    @Provides
    fun provideStorePricingInformationDao(temporaryDatabase: TemporaryDatabase): StorePricingInformationDao =
        temporaryDatabase.storePricingInformationDao()

    /**
     * Provide an instance of the temporary database for dependency injection.
     *
     * @param context The context needed to create the database.
     * @return The temporary database that contains the retailer information as the workers are run.
     */
    @Provides
    @Singleton
    fun providesTemporaryDatabase(@ApplicationContext context: Context): TemporaryDatabase =
        Room.databaseBuilder(context, TemporaryDatabase::class.java, "TemporaryDatabase")
            .fallbackToDestructiveMigration()
            .build()
}