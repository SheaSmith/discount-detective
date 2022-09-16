package com.example.cosc345.scraper.converter

import com.example.cosc345.scraper.models.myfoodlink.products.MyFoodLinkGtmData

/**
 * A converter specifically for MyFoodLink, which specifies the model we wish to transform the data into, along with the regex to use.
 */
class MyFoodLinkGtmDataConverter :
    MoshiElementConverter<Array<MyFoodLinkGtmData>>(
        Array<MyFoodLinkGtmData>::class.java,
        Regex("\\n*\\s*window.gtmDataLayer\\s=\\s(.+]);")
    )