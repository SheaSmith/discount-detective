package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.CountdownApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.countdown.CountdownSetStoreRequest
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class CountdownScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val countdownService =
            generateRequest(CountdownApi::class.java, "https://www.countdown.co.nz")

        val stores: ArrayList<Store> = arrayListOf()
        val products: ArrayList<RetailerProductInformation> = arrayListOf()
        countdownService.getStores("https://api.cdx.nz/site-location/api/v1/sites/").siteDetails.forEach { countdownStore ->
            if (countdownStore.site.suburb == "Dunedin") {
                val addressList = arrayListOf(
                    countdownStore.site.addressLine1.replace(
                        String.format(", %s", countdownStore.site.suburb),
                        ""
                    )
                )

                if (countdownStore.site.addressLine2 != null) {
                    addressList.add(countdownStore.site.addressLine2)
                }

                addressList.add(countdownStore.site.suburb)
                addressList.add(countdownStore.site.postcode)

                val store = Store(
                    countdownStore.site.name,
                    addressList.joinToString(", "),
                    countdownStore.site.latitude,
                    countdownStore.site.longitude,
                    true
                )
                stores.add(store)

                countdownService.setStore(CountdownSetStoreRequest(countdownStore.site.storeId))

                val departments =
                    countdownService.getDepartments().map { department -> department.url }
                departments.forEach { countdownDepartment ->
                    var page = 1
                    val response = countdownService.getProducts(
                        1,
                        String.format("Department;;%s;false", countdownDepartment)
                    ).products

                    response.items.forEach { countdownProduct ->

                    }
                }
            }
        }

        return ScraperResult(Retailer(), listOf())
    }
}