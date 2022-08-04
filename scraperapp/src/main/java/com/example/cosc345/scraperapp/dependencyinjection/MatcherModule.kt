package com.example.cosc345.scraperapp.dependencyinjection

import com.example.cosc345.scraper.Matcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatcherModule {
    @Provides
    @Singleton
    fun provideMatcher(): Matcher {
        return Matcher()
    }
}