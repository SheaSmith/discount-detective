package com.example.cosc345project.paging

import androidx.appsearch.app.SearchResults
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.work.await
import com.example.cosc345project.models.SearchableRetailerProductInformation
import com.example.cosc345project.repository.SearchRepository

class ProductsSearchPagingSource(
    private val repository: SearchRepository,
    private val query: String
) : PagingSource<SearchResults, SearchableRetailerProductInformation>() {
    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<SearchResults, SearchableRetailerProductInformation>): SearchResults? {
        return null
    }

    override suspend fun load(params: LoadParams<SearchResults>): LoadResult<SearchResults, SearchableRetailerProductInformation> {
        val searchResults = params.key ?: repository.queryProducts(query, params.loadSize)
        val response = searchResults.nextPageAsync.await()

        return LoadResult.Page(
            data = response.map { it.getDocument(SearchableRetailerProductInformation::class.java) },
            prevKey = null,
            nextKey = searchResults
        )
    }
}