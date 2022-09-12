# Module Scraper App

This is the app that actually runs the scraper and uploads the results to Firebase.

It only has a single UI page, which is dedicated to some debug options for the scraper. In the
background, there are four background workers. One for scrapping the main data from the retailers,
one for merging products based on their barcode, one for doing the same based on their name and
finally one for uploading to Firebase.

# Package com.example.cosc345.scraperapp

Overall classes used across the app.

# Package com.example.cosc345.scraperapp.dao

Interfaces which define how to interact with the local database.

# Package com.example.cosc345.scraperapp.dependencyinjection

Provides third party dependencies that can be injected elsewhere in the app, without needing to
manage constructors.

# Package com.example.cosc345.scraperapp.models

Models specific to the scraper app. This is essentially just the models we use to store the
information in the temporary database.

# Package com.example.cosc345.scraperapp.repositories

Repositories that handle the getting and setting of data from various data sources.

# Package com.example.cosc345.scraperapp.typeconverters

Type converters handle the conversion of complex Kotlin objects into something that can be inserted
into a SQL database.

# Package com.example.cosc345.scraperapp.ui.theme

Theming information for the app (Compose boilerplate essentially).

# Package com.example.cosc345.scraperapp.workers

The background workers that run the scraping process.