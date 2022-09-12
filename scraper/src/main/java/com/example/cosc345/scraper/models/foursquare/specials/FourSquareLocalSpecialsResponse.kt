package com.example.cosc345.scraper.models.foursquare.specials

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * The response for a Four Square local specials request.
 *
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class FourSquareLocalSpecialsResponse(
    /**
     * A list of the different specials.
     */
    @Selector(".sxa-specials-card__card-wrap")
    var specials: List<FourSquareLocalSpecial>? = null
)
