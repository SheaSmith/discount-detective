package com.example.cosc345.scraperapp.repositories

import androidx.room.withTransaction
import com.example.cosc345.scraperapp.TemporaryDatabase
import com.example.cosc345.scraperapp.dao.RetailerDao
import com.example.cosc345.scraperapp.dao.RetailerProductInformationDao
import com.example.cosc345.scraperapp.dao.StoreDao
import com.example.cosc345.scraperapp.dao.StorePricingInformationDao
import com.example.cosc345.scraperapp.models.StorageRetailer
import com.example.cosc345.scraperapp.models.StorageRetailerProductInformation
import com.example.cosc345.scraperapp.models.StorageStore
import com.example.cosc345.scraperapp.models.StorageStorePricingInformation
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemporaryDatabaseRepository @Inject constructor(
    private val temporaryDatabase: TemporaryDatabase,
    private val retailerProductInformationDao: RetailerProductInformationDao,
    private val storePricingInformationDao: StorePricingInformationDao,
    private val retailerDao: RetailerDao,
    private val storeDao: StoreDao
) {
    fun clearDatabase() {
        temporaryDatabase.clearAllTables()
    }

    suspend fun insertProducts(products: Map<String, Product>) {
        val storageRetailerProductInformation = products.map { map ->
            map.value.information!!
        }.flatten()

        insertRetailerProductInfo(storageRetailerProductInformation)
    }

    suspend fun insertRetailerProductInfo(retailerProductInformation: List<RetailerProductInformation>) {
        val storageRetailerProductInformation = retailerProductInformation.map {
            StorageRetailerProductInformation(
                it,
                null
            )
        }

        val storagePricingInformation = retailerProductInformation.map { info ->
            info.pricing!!.map {
                StorageStorePricingInformation(
                    info,
                    it
                )
            }
        }.flatten()

        temporaryDatabase.withTransaction {
            retailerProductInformationDao.setRetailerProductInfo(storageRetailerProductInformation)
            storePricingInformationDao.insertPricingInformation(storagePricingInformation)
        }
    }

    suspend fun insertRetailers(retailers: Map<String, Retailer>) {
        val retailersList = retailers.map { StorageRetailer(it.value, it.key) }
        val storesList =
            retailers.flatMap { map -> map.value.stores!!.map { StorageStore(it, map.key) } }

        temporaryDatabase.withTransaction {
            retailerDao.insertRetailers(retailersList)
            storeDao.insertStores(storesList)
        }
    }

    suspend fun getRetailerProductInfo(): MutableList<RetailerProductInformation> {
        val storageInfo = retailerProductInformationDao.getRetailerProductInfo()

        return storageInfo.map { map ->
            map.key.toRetailerProductInformation(map.value.map { it.toStorePricingInformation() }
                .toMutableList())
        }.toMutableList()
    }

    suspend fun getProducts(): Map<String, Product> {
        val storageInfo = retailerProductInformationDao.getRetailerProductInfo()

        return storageInfo
            .mapValues { storageMap ->
                storageMap.key.toRetailerProductInformation(storageMap.value.map { it.toStorePricingInformation() }
                    .toMutableList())
            }
            .map { Pair(it.key, it.value) }
            .groupBy { it.first.productId!! }
            .mapValues { map -> Product(map.value.map { it.second }.toMutableList()) }
    }

    suspend fun getRetailers(): Map<String, Retailer> {
        val storageRetailers = retailerDao.getRetailers()

        return storageRetailers
            .mapValues { map -> map.key.toRetailer(map.value.map { it.toStore() }) }
            .mapKeys { it.key.id }
    }
}