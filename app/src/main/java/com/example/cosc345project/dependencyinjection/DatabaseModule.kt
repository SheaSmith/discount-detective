package com.example.cosc345project.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.database.ShoppingListDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
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

    @Provides
    fun provideProductDao(db: ShoppingListDatabase): ProductDao {
        return db.productDao()
    }
}