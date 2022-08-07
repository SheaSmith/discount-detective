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

@Database(
    entities = [StorageRetailerProductInformation::class, StorageStorePricingInformation::class, StorageRetailer::class, StorageStore::class],
    version = 3,
)
@TypeConverters(RoomStringListConverter::class)
abstract class TemporaryDatabase : RoomDatabase() {
    abstract fun retailerDao(): RetailerDao

    abstract fun storeDao(): StoreDao

    abstract fun retailerProductInformationDao(): RetailerProductInformationDao

    abstract fun storePricingInformationDao(): StorePricingInformationDao
}