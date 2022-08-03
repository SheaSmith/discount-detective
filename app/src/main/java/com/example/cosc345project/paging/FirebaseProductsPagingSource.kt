package com.example.cosc345project.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cosc345project.models.SearchableRetailerProductInformation
import com.example.cosc345project.repository.SearchRepository

class FirebaseProductsPagingSource(
    private val repository: SearchRepository,
    private val query: String
) : PagingSource<String, SearchableRetailerProductInformation>() {
    override fun getRefreshKey(state: PagingState<String, SearchableRetailerProductInformation>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SearchableRetailerProductInformation> {
        val result = repository.queryProductsFirebase(query, params.key, params.loadSize)

        return LoadResult.Page(
            data = result.first.flatMap { it.information },
            nextKey = result.second,
            prevKey = null
        )
    }

}