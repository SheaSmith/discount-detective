package com.example.cosc345.scraper.converter

import com.example.cosc345.scraper.helpers.MoshiHelper
import com.squareup.moshi.JsonAdapter
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

abstract class MoshiElementConverter<T>(private val cls: Class<T>, private val regex: Regex) :
    ElementConverter<T> {
    override fun convert(node: Element, selector: Selector): T? {
        val moshi = MoshiHelper.getMoshi()

        val jsonAdapter: JsonAdapter<T> = moshi.adapter(cls)

        val nodeText = node.data()

        if (regex.containsMatchIn(nodeText)) {
            val json = regex.find(nodeText)!!.groups[1]!!.value.trim()

            return jsonAdapter.fromJson(json)
        }

        return null
    }
}