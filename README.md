# Discount Detective

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/490c7545aede4dd29264539c1794044e)](https://www.codacy.com/gh/SheaSmith/discount-detective/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SheaSmith/discount-detective&amp;utm_campaign=Badge_Grade)
[![Android CI](https://github.com/SheaSmith/COSC345-Project/actions/workflows/android.yml/badge.svg)](https://github.com/SheaSmith/COSC345-Project/actions/workflows/android.yml)
[![Deploy static content to Pages](https://github.com/SheaSmith/COSC345-Project/actions/workflows/build-docs.yml/badge.svg)](https://github.com/SheaSmith/COSC345-Project/actions/workflows/build-docs.yml)
[![codecov](https://codecov.io/gh/SheaSmith/discount-detective/branch/master/graph/badge.svg?token=6LT4FB6K2B)](https://codecov.io/gh/SheaSmith/discount-detective)
[![Deploy to Play Store](https://github.com/SheaSmith/discount-detective/actions/workflows/play-store.yml/badge.svg)](https://github.com/SheaSmith/discount-detective/actions/workflows/play-store.yml)

## Introduction

Discount Detective is a price comparison app for food and other essential products.

We compare prices from all of the major New Zealand supermarkets, along with a selection of local
retailers in some areas. Currently the app is available in Dunedin, Whitianga, and Invercargill.

You can search products, find the lowest prices, both at non-local and local stores and see what
local stores have in stock, so you can shop local. We also have support for adding items to a
shopping list, to make your shop more convenient.

## Compilation & Running

To run the app on your device, click the run button in Android Studio in the toolbar. Make sure the
dropdown is set to "app" to ensure you are running the proper app (rather than the scraper app).

In terms of devices, a physical device is recommended, as the results returned from Firebase can be
very slow on an emulator. Otherwise, a Pixel emulator running API 32 is recommended, as long as the
computer is sufficiently powerful, and the emulator allocated enough resources.

On some of our emulators, results didn't return at all. This appears to be a bug in Firebase, and
not something we can change at all. Physical devices worked perfectly, but two computers didn't.

This could be an architectural thing, as an emulator on an Apple M1 processor ran fine, but x86
emulators appeared to have problems.

When evaluating the app, it is strongly recommended that you wait until the "app is currently
processing" message goes away. This means you'll see the true performance and behavior of the
search, however, there is a fallback search technique that can be used during this optimisation.
Additionally, the app will be slower to run during this indexing. The indexing takes around 10
minutes on a physical device, but only needs to run on the first run.

## Code Review

Pushes into master are disabled. In order to get your code included in the repository, you must open
a pull request. Additionally, when opening a pull request, your changes must be reviewed by one of
the members of this repository. This is mandatory for all pull requests.

## Continuous Google Play Deployment

All pushes into master are automatically pushed into an alpha test track on Google Play. You can
join this track by visiting https://play.google.com/apps/internaltest/4701662988659821470, or by
scanning this QR code:

![QR Code](https://i.imgur.com/loQCoRx.png)

Github releases are automatically pushed into either a open beta or production test track, depending
on whether the release is a pre-release or not. You can download these versions at
the [Play Store](https://play.google.com/store/apps/details?id=io.github.sheasmith.discountdetective)
page (once it has passed review).

## Crash Logging

We use Firebase Crashlytics for crash logging. This means whenever the app crashes, we get a report
of this crash, along with some other information about the circumstances (such as Android version or
phone brand). If you notice a crash, please create an issue, and if possible, specify a time or
device type, so we can track down the crash more easily.

## Technical Details

This app is split into four modules, which are documented below (if you are viewing this on
the [documentation](https://sheasmith.github.io/discount-detective) page).

For most of the app, we are using Jetpack Compose (a SwiftUI analogue for Android) for the UI, along
with the Google recommended clean architecture. This means we follow a reasonably strict MVVM (
Model, View, ViewModel) pattern for the app, with all data retrieval code stores in repositories,
which map to the models, which are then processed by the ViewModel for state management and to pass
to the UI.

For the setting screen, we are using fragments within a container, loaded into Jetpack Compose.
There are three available settings for the user to change. These are 1. Dark/light mode 2. The
region, and 3. A feeedback button which is linked to our user testing Google form.

For the search, we are using Google's AndroidX AppSearch library. We pull down a list of all
products from Firebase Realtime Database (our backend), which is then indexed by AppSearch to allow
for performant searching on products, once processed. We process search results once per hour, but
only update them if there has been a change.

The scraping of the supermarket data is done by a special app build, in the `scraperapp` module.
This is run once per day on a dedicated device, and extracts the data from each supermarket, groups
matching products together, and then uploads it to the Firebase database.

## (Hopefully) Interesting Details

### App Icon

The app icon for this app is actually generated by DALL-E 2, the OpenAI image generation project. We
then slightly modified its design to suit the requirements of Android app icons, but for the most
part, it is purely what was generated by the AI.

This was a reasonably iterative process, as we needed to be specific with our prompt, and tweak the
app icon design as we felt necessary. For example, here is a sample of some of the requests we made:

![](https://github.com/SheaSmith/COSC345-Project/blob/master/docs-assets/Screen%20Shot%202022-08-25%20at%2010.42.24.png?raw=true)

![](https://raw.githubusercontent.com/SheaSmith/COSC345-Project/master/docs-assets/Screen%20Shot%202022-08-25%20at%2010.40.46.png)

![](https://raw.githubusercontent.com/SheaSmith/COSC345-Project/master/docs-assets/Screen%20Shot%202022-08-25%20at%2010.41.30.png)

### Search Library

As part of our work integrating Google's AppSearch into our app, we discovered a bug related to
nested indexing (which was necessary for our app, as we search across products, but each product has
nested information), as such
we [reported this bug](https://issuetracker.google.com/issues?q=componentid:1012737). However, as
this functionality was critical for our app, and there no immediate acknowledgement from Google
about the bug, we submitted
a [pull request](https://android-review.googlesource.com/c/platform/frameworks/support/+/2175315) to
Google to fix the issue, which was accepted and merged in.

## Data Structure

```mermaid
    erDiagram
    Product ||--|{ RetailerProductInformation : information
    Product {
        string productId
    }
    RetailerProductInformation ||--O{ Barcode : barcodes
    RetailerProductInformation ||--|{ StorePricingInformation : pricing
    RetailerProductInformation |O--|| Retailer : retailer
    RetailerProductInformation {
        string id
        string name
        string brandName
        string variant
        string saleType
        string quantity
        int weight
        string image
        boolean automated
        boolean verified
    }
    Barcode {
        string barcode
    }
    StorePricingInformation |O--|| Store : store
    StorePricingInformation {
        int price
        int discountPrice
        int multiBuyQuantity
        int multiBuyPrice
        boolean clubOnly
        boolean automated
        boolean verified
    }
    Retailer ||--|{ Store : stores
    Retailer {
        string id
        string name
        boolean automated
        int colourLight
        int onColourLight
        int colourDark
        int onColourDark
        string initialism
        boolean local
    }
    Store {
        string id
        string name
        string address
        double latitude
        double longitude
        boolean automated
    }
```

The fundamental philosophy for the data structure is that the scraper shouldn't take any
authoritative position on what data is accurate or not (e.g. by having a product with information
from just a specific retailer). Instead the decision about which retailers data to display should be
left to the app, as it can be context specific. For example, in the shopping list screen, you would
always want to show the data for the store that the user has selected, regardless of the quality of
that data relative to other shops.

For this reason, we essentially store the original data records for each supermarket, with each
product solely having a product ID for organisation purposes.
