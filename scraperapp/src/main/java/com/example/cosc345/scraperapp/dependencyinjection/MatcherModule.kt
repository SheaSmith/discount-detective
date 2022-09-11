package com.example.cosc345.scraperapp.dependencyinjection

import com.example.cosc345.scraper.Matcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the matcher for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object MatcherModule {
    /**
     * The specific method which provides the matcher.
     *
     * @return An instance of the matcher for running the matching program.
     */
    @Provides
    @Singleton
    fun provideMatcher(): Matcher {
        return Matcher()
    }
}