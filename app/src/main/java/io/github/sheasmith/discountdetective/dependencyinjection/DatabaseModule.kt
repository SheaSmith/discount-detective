package io.github.sheasmith.discountdetective.dependencyinjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.sheasmith.discountdetective.dao.ShoppingListDao
import io.github.sheasmith.discountdetective.database.ShoppingListDatabase
import javax.inject.Singleton

/**
 * The module which handles dependency injection for the Room database.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**
     * Provide the shopping list database for injection.
     *
     * @param appContext The app context to use for getting the database.
     * @return An instance of the database.
     */
    @Provides
    @Singleton
    fun provideShoppingListDatabase(@ApplicationContext appContext: Context):
            ShoppingListDatabase {
        return Room.databaseBuilder(
            appContext,
            ShoppingListDatabase::class.java,
            "shoppingList_database"
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Get the product dao from the Room database for injection.
     *
     * @param db The database to use to get the dao.
     * @return The dao.
     */
    @Provides
    fun provideProductDao(db: ShoppingListDatabase): ShoppingListDao {
        return db.productDao()
    }
}