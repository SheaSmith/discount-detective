package com.example.cosc345project

import android.content.Context
import androidx.room.Room
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.database.ShoppingListDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideProductDao(db: ShoppingListDatabase): ProductDao{
        return db.productDao()
    }

    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in application
    @Provides
    fun provideShoppingListDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ShoppingListDatabase::class.java,
        "shoppingListDatabase"
    ).build()

}