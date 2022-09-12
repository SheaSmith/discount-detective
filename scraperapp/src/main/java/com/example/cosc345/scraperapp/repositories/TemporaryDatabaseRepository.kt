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

/**
 * The repository responsible for saving items into the temporary local Room database.
 */
@Singleton
class TemporaryDatabaseRepository @Inject constructor(
    private val temporaryDatabase: TemporaryDatabase,
    private val retailerProductInformationDao: RetailerProductInformationDao,
    private val storePricingInformationDao: StorePricingInformationDao,
    private val retailerDao: RetailerDao,
    private val storeDao: StoreDao
) {
    /**
     * Clear the entire database.
     */
    fun clearDatabase() {
        temporaryDatabase.clearAllTables()
    }

    /**
     * Insert/update new products into the database.
     *
     * @param products The products to insert, with the product ID as the key, and the product itself as the value.
     */
    suspend fun insertProducts(products: Map<String, Product>) {
        val storageRetailerProductInformation = products.map { (key, value) ->
            value.information!!.map { Pair(it, key) }
        }.flatten()

        insertRetailerProductInfo(
            storageRetailerProductInformation.map { it.first },
            storageRetailerProductInformation.map { it.second })
    }

    /**
     * Insert/update new retailer information into the database.
     *
     * @param retailerProductInformation The retailer product information that should be inserted.
     * @param productIds The product IDs that should be associated, if applicable.
     */
    suspend fun insertRetailerProductInfo(
        retailerProductInformation: List<RetailerProductInformation>,
        productIds: List<String>? = null
    ) {
        val storageRetailerProductInformation = retailerProductInformation.mapIndexed { index, it ->
            StorageRetailerProductInformation(
                it,
                productIds?.get(index)
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

    /**
     * Insert/update retailers into the database.
     *
     * @param retailers The retailers to insert, with the key being the key, and the value being the retailer itself.
     */
    suspend fun insertRetailers(retailers: Map<String, Retailer>) {
        val retailersList = retailers.map { StorageRetailer(it.value, it.key) }
        val storesList =
            retailers.flatMap { (key, value) -> value.stores!!.map { StorageStore(it, key) } }

        temporaryDatabase.withTransaction {
            retailerDao.insertRetailers(retailersList)
            storeDao.insertStores(storesList)
        }
    }

    /**
     * Get product information from the database.
     *
     * @return A list of product information from the database.
     */
    suspend fun getRetailerProductInfo(): MutableList<RetailerProductInformation> {
        val storageInfo = retailerProductInformationDao.getRetailerProductInfo()

        return storageInfo.map { (key, value) ->
            key.toRetailerProductInformation(value.map { it.toStorePricingInformation() }
                .toMutableList())
        }.toMutableList()
    }

    /**
     * Get products from the database.
     *
     * @return A map of products, with the key being the product ID, and the value being the product itself.
     */
    suspend fun getProducts(): Map<String, Product> {
        val storageInfo = retailerProductInformationDao.getRetailerProductInfo()
        print("Test")

        return storageInfo
            .mapValues { (key, value) ->
                key.toRetailerProductInformation(value.map { it.toStorePricingInformation() }
                    .toMutableList())
            }
            .map { Pair(it.key, it.value) }
            .groupBy { it.first.productId!! }
            .mapValues { map -> Product(map.value.map { it.second }.toMutableList()) }
    }

    /**
     * Get retailers from the database.
     *
     * @return A map of retailers, with the ID being the retailer ID and the value being the retailer itself.
     */
    suspend fun getRetailers(): Map<String, Retailer> {
        val storageRetailers = retailerDao.getRetailers()

        return storageRetailers
            .mapValues { (key, value) -> key.toRetailer(value.map { it.toStore() }) }
            .mapKeys { it.key.id }
    }
}