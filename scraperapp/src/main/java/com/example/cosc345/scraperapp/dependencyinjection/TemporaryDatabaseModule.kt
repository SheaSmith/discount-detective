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

// https://svvashishtha.medium.com/using-room-with-hilt-cb57a1bc32f
@Module
@InstallIn(SingletonComponent::class)
object TemporaryDatabaseModule {
    @Provides
    fun provideRetailerDao(temporaryDatabase: TemporaryDatabase): RetailerDao =
        temporaryDatabase.retailerDao()

    @Provides
    fun provideStoreDao(temporaryDatabase: TemporaryDatabase): StoreDao =
        temporaryDatabase.storeDao()

    @Provides
    fun provideProductInformationDao(temporaryDatabase: TemporaryDatabase): RetailerProductInformationDao =
        temporaryDatabase.retailerProductInformationDao()

    @Provides
    fun provideStorePricingInformationDao(temporaryDatabase: TemporaryDatabase): StorePricingInformationDao =
        temporaryDatabase.storePricingInformationDao()

    @Provides
    @Singleton
    fun providesTemporaryDatabase(@ApplicationContext context: Context): TemporaryDatabase =
        Room.databaseBuilder(context, TemporaryDatabase::class.java, "TemporaryDatabase").build()
}