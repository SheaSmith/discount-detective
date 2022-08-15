package com.example.cosc345project.paging

import androidx.appsearch.app.SearchResults
import androidx.concurrent.futures.await
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.repository.SearchRepository

class ProductsSearchPagingSource(
    private val repository: SearchRepository,
    private val query: String
) : PagingSource<SearchResults, SearchableProduct>() {
    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<SearchResults, SearchableProduct>): SearchResults? {
        return null
    }

    override suspend fun load(params: LoadParams<SearchResults>): LoadResult<SearchResults, SearchableProduct> {
        val searchResults = params.key ?: repository.queryProductsAppSearch(query, params.loadSize)
        val response = searchResults.nextPageAsync.await()
        val res = response.map { it.getDocument(SearchableProduct::class.java) }

        if (res.any { it.information == null }) {
            print("Test")
        }

        return LoadResult.Page(
            data = res,
            prevKey = null,
            nextKey = if (res.size != params.loadSize) null else searchResults
        )
    }
}