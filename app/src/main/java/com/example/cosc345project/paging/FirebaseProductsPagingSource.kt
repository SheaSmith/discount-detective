package com.example.cosc345project.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cosc345.shared.models.Product
import com.example.cosc345project.repository.SearchRepository

class FirebaseProductsPagingSource(
    private val repository: SearchRepository,
    private val query: String
) : PagingSource<String, Pair<String, Product>>() {
    override fun getRefreshKey(state: PagingState<String, Pair<String, Product>>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Pair<String, Product>> {
        val result = repository.queryProductsFirebase(query, params.key, params.loadSize)

        return LoadResult.Page(
            data = result.first.toList(),
            nextKey = if (result.first.size != params.loadSize) null else result.second,
            prevKey = null
        )
    }

}