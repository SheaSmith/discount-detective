package com.example.cosc345.scraper.models.foursquare.specials

import pl.droidsonroids.jspoon.annotation.Selector

data class FourSquareLocalSpecialsResponse(
    @Selector(".sxa-specials-card__card-wrap")
    var specials: List<FourSquareLocalSpecial>? = null
)
