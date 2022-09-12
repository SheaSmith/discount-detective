package com.example.cosc345.scraperapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cosc345.scraperapp.dao.RetailerDao
import com.example.cosc345.scraperapp.dao.RetailerProductInformationDao
import com.example.cosc345.scraperapp.dao.StoreDao
import com.example.cosc345.scraperapp.dao.StorePricingInformationDao
import com.example.cosc345.scraperapp.models.StorageRetailer
import com.example.cosc345.scraperapp.models.StorageRetailerProductInformation
import com.example.cosc345.scraperapp.models.StorageStore
import com.example.cosc345.scraperapp.models.StorageStorePricingInformation
import com.example.cosc345.scraperapp.typeconverters.RoomStringListConverter

/**
 * The database responsible for holding the product information between worker runs.
 */
@Database(
    entities = [StorageRetailerProductInformation::class, StorageStorePricingInformation::class, StorageRetailer::class, StorageStore::class],
    version = 3,
)
@TypeConverters(RoomStringListConverter::class)
abstract class TemporaryDatabase : RoomDatabase() {
    /**
     * Get the dao for handling retailer info.
     */
    abstract fun retailerDao(): RetailerDao

    /**
     * Get the dao for handling store info.
     */
    abstract fun storeDao(): StoreDao

    /**
     * Get the dao for handling retailer product information.
     */
    abstract fun retailerProductInformationDao(): RetailerProductInformationDao

    /**
     * Get the dao for handling store pricing information.
     */
    abstract fun storePricingInformationDao(): StorePricingInformationDao
}