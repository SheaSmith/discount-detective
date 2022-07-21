package com.example.cosc345.scraper.converter

import com.example.cosc345.scraper.models.myfoodlink.products.MyFoodLinkGtmData

class MyFoodLinkGtmDataConverter :
    MoshiElementConverter<Array<MyFoodLinkGtmData>>(
        Array<MyFoodLinkGtmData>::class.java,
        Regex("\\n*\\s*window.gtmDataLayer\\s=\\s(.+]);")
    )