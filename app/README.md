# Module App

This is the module which contains the actual app, including the data display and searching logic.

The app has five different "screens" (essentially pages in the app): The search page, the shopping
list page, the settings page, the product information page, and the barcode scanning page. Each page
has its own screen composable, with many of them having sub-components as well.

# Package io.github.sheasmith.discountdetective

The root package which contains files relevant to the entire app.

# Package io.github.sheasmith.discountdetective.barcode

Specific classes related to handling barcode scanning.

# Package io.github.sheasmith.discountdetective.barcode.camera

Specific classes related to the camera for barcode scanning.

# Package io.github.sheasmith.discountdetective.dao

Interfaces which define how to interact with the shopping list database.

# Package io.github.sheasmith.discountdetective.database

Definitions for the local databases (currently just one for the shopping list).

# Package io.github.sheasmith.discountdetective.dependencyinjection

Provides third party dependencies that can be injected elsewhere in the app, without needing to
manage constructors.

# Package io.github.sheasmith.discountdetective.exceptions

Custom exceptions used for error handling in the app.

# Package io.github.sheasmith.discountdetective.fragments

Android Fragments that are used in places where it is not possible to do a full Compose-based view.

# Package io.github.sheasmith.discountdetective.models

The models for this app specifically. For example, the models we save into AppSearch, or the
shopping list database.

# Package io.github.sheasmith.discountdetective.paging

Pagers for automatically navigation between pages seamlessly with AndroidX Paging.

# Package io.github.sheasmith.discountdetective.repository

The repositories which handle getting, processing and save data.

# Package io.github.sheasmith.discountdetective.settings

Classes responsible for getting and saving app settings.

# Package io.github.sheasmith.discountdetective.ui

Classes responsible for the overall UI of the app.

# Package io.github.sheasmith.discountdetective.ui.components

UI components used across different screens in the app.

# Package io.github.sheasmith.discountdetective.ui.components.barcode

UI components used specifically in the barcode screen of the app.

# Package io.github.sheasmith.discountdetective.ui.components.main

UI components used specifically in the main, root page of the app, which contains the navigation and
the container for the rest of the app.

# Package io.github.sheasmith.discountdetective.ui.components.product

UI components used specifically for product display. Usually this is in the product page, but some
of these are shared with the search page.

# Package io.github.sheasmith.discountdetective.ui.components.search

UI components used specifically for the search screen.

# Package io.github.sheasmith.discountdetective.ui.components.shoppinglist

UI components used in the shopping list screen.

# Package io.github.sheasmith.discountdetective.ui.screens

The different root level screens in the app.

# Package io.github.sheasmith.discountdetective.ui.theme

Components which handle the overall theme of the app.

# Package io.github.sheasmith.discountdetective.ui.utils

UI utilities that are used in different screens.

# Package io.github.sheasmith.discountdetective.viewmodel

View models for the different screens in the app.

# Package io.github.sheasmith.discountdetective.workers

Background workers that handle processes that need to occur in the background of the app.