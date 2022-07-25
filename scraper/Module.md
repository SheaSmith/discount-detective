# Module Scrapers

This is the module that defines the different scrapers that are responsible for retrieving data from
the different supermarkets and shops.

## Overview

Each scraper is split into four or five different parts. The first part is the models. These define
the data that is returned from the shop's API.

We define API rather loosely here, but effectively it is either an internal JSON-based API that we
parse, or it is raw HTML that we scrape and convert into the appropriate models.

The second part is the definition for that API. This contains the individual endpoints we hit, any
parameters we need to specify, along with what object is returned by the API. These are then
transformed by Retrofit into usable methods that we can use in the scrapers themselves.

The third part is the actual scraper. For many shops, this will be an abstract, generic scraper, as
their shop's underlying software is shared across several different shops. For example, New World
and PAK'nSAVE share the same shopping software, as do several smaller retailers using Shopify.

This scraper is responsible for hitting the individual endpoints and then converting the data
received by the domain-specific models into the generic models we use for all shops. This generally
means extracting data, processing it, and then cleaning up some fields. Often we also need to
cross-reference this data to other parts of the store, to ensure a complete and accurate result.

The optional fourth part is the shop specific scraper. For some shops (e.g. Countdown), this is not
necessary, as their underlying ecommerce software is bespoke, and so they don't need the separation
between generic and shop-specific scrapers, however for shops where this separation is necessary,
the shop-specific scraper is mostly responsible for passing shop-specific data (for example, the
name or what URL to hit) to the generic scraper. However, in some cases, we need to get extra data
in order to ensure accurate results (for example, the Mad Butcher, which despite using WooCommerce,
requires a special scraper extension in order to determine which products are sold by weight rather
than as units).

Finally, the last part is the unit tests for each scraper. These are reasonably basic, but
essentially check that we have all of the required fields, along with providing some useful metrics
around how long each scraper takes to process, and the number of prices per product.

# Package com.example.cosc345.scraper.api

The definitions of the different online shopping APIs that we use to obtain the data from the
different shops.

Each API defines either a JSON or HTML interface that we call from the scraper, but actual parsing
logic for converting this raw HTML/JSON to Kotlin models is handled by the individuals models
themselves. Rather these APIs simply define the different endpoints, what parameters they accept,
and what they return.

# Package com.example.cosc345.scraper.converter

The converters we use to incorporate JSON objects in HTML based APIs. This is predominantly used for
MyFoodLink (SuperValue & FreshChoice), as their HTML pages contain JSON objects that contain useful
product information, which we want to incorporate into the response provided from the server.

# Package com.example.cosc345.scraper.helpers

The helpers that contain commonly used functions that are needed across the project.

# Package com.example.cosc345.scraper.interceptors

Retrofit interceptors, which allow us to specify custom logic that applies to all HTTP requests made
by this module.

# Package com.example.cosc345.scraper.interfaces

The package containing the generic interfaces we use across the module. For now this only contains
the base Scraper class.

# Package com.example.cosc345.scraper.models

The parent package for all models used for the scraping, including the model that defines the common
object that the scrapers actually return.

# Package com.example.cosc345.scraper.models.countdown

The parent package for all models that the Countdown scraper needs, including the direct parent for
single-model requests (e.g. those that only need a single object to describe either their request or
response bodies).

# Package com.example.cosc345.scraper.models.countdown.departments

The package which contains all of the models related to retrieving the different departments from
the Countdown API.

# Package com.example.cosc345.scraper.models.countdown.products

The package which contains all of the models related to retrieving the different products from the
Countdown API.

# Package com.example.cosc345.scraper.models.countdown.stores

The package which contains all of the models related to retrieving the different stores from the
Countdown API.

# Package com.example.cosc345.scraper.models.foodstuffs

The parent package which contains all of the sub-packages defining the different FoodStuffs API
models for each endpoint. This also contains any models which are the only ones needed by their
endpoint.

# Package com.example.cosc345.scraper.models.foodstuffs.categories

The package which contains all of the models related to retrieving the different categories from the
different FoodStuffs-based APIs.

# Package com.example.cosc345.scraper.models.foodstuffs.products

The package which contains all of the models related to retrieving the different products from the
different FoodStuffs-based APIs.

# Package com.example.cosc345.scraper.models.foodstuffs.promotions

The package which contains all of the models related to retrieving the different promotions from the
different FoodStuffs-based APIs.

# Package com.example.cosc345.scraper.models.foodstuffs.stores

The package which contains all of the models related to retrieving the different stores from the
different FoodStuffs-based APIs.

# Package com.example.cosc345.scraper.models.foodstuffs.token

The package which contains all of the models related to retrieving an authorisation token from the
different FoodStuffs-based APIs.

# Package com.example.cosc345.scraper.models.foursquare

The parent package which contains the sub-packages which define the models for the different parts
of the Four Square API, along with containing models for endpoints that only need a single model.

# Package com.example.cosc345.scraper.models.foursquare.mailer

The package which contains all of the models related to getting products from the Four Square mailer
API.

# Package com.example.cosc345.scraper.models.foursquare.specials

The package which contains all of the models related to getting products from the Four Square local
specials page.

# Package com.example.cosc345.scraper.models.madbutcher

The package which contains the models needed to supplement the generic WooCommerce scraper for the
Mad Butcher.

# Package com.example.cosc345.scraper.models.myfoodlink

The parent package which contains the sub-packages defining models for the different parts of the
MyFoodLink API (SuperValue and FreshChoice), along with models that belong to endpoints with single
models.

# Package com.example.cosc345.scraper.models.myfoodlink.products

The package which contains the models related to getting products from the MyFoodLink API.

# Package com.example.cosc345.scraper.models.robertsonsmeats.categories

The package which contains the models related to getting categories from Robertsons Meats.

# Package com.example.cosc345.scraper.models.robertsonsmeats.products

The package which contains the models related to getting products from Robertsons Meats.

# Package com.example.cosc345.scraper.models.shopify

The package which contains the models related to getting products from Shopify based stores.

# Package com.example.cosc345.scraper.models.veggieboys

The package which contains the models related to getting products from Veggie Boys.

# Package com.example.cosc345.scraper.models.warehouse.products

The package which contains the models related to getting products from The Warehouse.

# Package com.example.cosc345.scraper.models.warehouse.stores

The package which contains the models related to getting all of the stores from The Warehouse.

# Package com.example.cosc345.scraper.models.wixstores.products

The package which contains all of the models related to getting a list of products from WixStores
based stores.

# Package com.example.cosc345.scraper.models.wixstores.token

The package which contains all of the models related to getting an authorisation token from
WixStores based stores.

# Package com.example.cosc345.scraper.models.woocommerce

The package which contains all of the models related to getting products from a WooCommerce based
store.

# Package com.example.cosc345.scraper.scrapers

The parent package which contains the scrapers for bespoke stores (those that don't require generic
scrapers), along with containing the sub-packages which contain the generic scrapers and
store-specific implementations of generic scrapers.

# Package com.example.cosc345.scraper.scrapers.foodstuffs

The retailer-specific implementations for FoodStuffs based scrapers.

# Package com.example.cosc345.scraper.scrapers.generic

The generic scrapers for stores that share ecommerce architectures.

# Package com.example.cosc345.scraper.scrapers.myfoodlink

The retailer-specific implementations for MyFoodLink based scrapers.

# Package com.example.cosc345.scraper.scrapers.shopify

The retailer-specific implementations for Shopify based scrapers.

# Package com.example.cosc345.scraper.scrapers.wixstores

The retailer-specific implementations for Shopify based scrapers.

# Package com.example.cosc345.scraper.scrapers.woocommerce

The retailer-specific implementations for WooCommerce based scrapers.