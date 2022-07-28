package com.example.cosc345.scraper.converter

import com.example.cosc345.scraper.helpers.getMoshi
import com.squareup.moshi.JsonAdapter
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

/**
 * An element converter specifically for `<script>` classes to convert JSON text from HTML into Kotlin objects.
 *
 * @param T The type you wish to transform the JSON into.
 * @author Shea Smith
 * @constructor Create a new instance of this converter, specifying the conversion types.
 */
abstract class MoshiElementConverter<T>(
    /**
     * The class of the model you want to transform the JSON into.
     */
    private val cls: Class<T>,
    /**
     * The regex that should be used to extract the JSON from the tag body.
     */
    private val regex: Regex
) :
    ElementConverter<T> {

    /**
     * Convert the node from an HTML element into a Kotlin object based on the JSON data contained within the element.
     *
     * @param node The node that has been selected earlier.
     * @param selector The selector that was used to select this node.
     * @return The model that has been parsed from the JSON.
     */
    override fun convert(node: Element, selector: Selector): T? {
        val moshi = getMoshi()

        val jsonAdapter: JsonAdapter<T> = moshi.adapter(cls)

        val nodeText = node.data()

        if (regex.containsMatchIn(nodeText)) {
            val json = regex.find(nodeText)!!.groups[1]!!.value.trim()

            return jsonAdapter.fromJson(json)
        }

        return null
    }
}