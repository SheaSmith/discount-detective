package io.github.sheasmith.discountdetective.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cosc345.shared.models.Product
import io.github.sheasmith.discountdetective.exceptions.NoInternetException
import io.github.sheasmith.discountdetective.repository.SearchRepository

/**
 * A paging source that uses Firebase to query search results, for example, if AppSearch's index is
 * empty.
 *
 * @param repository The search repository to query from.
 * @param query The query string to use.
 */
class FirebaseProductsPagingSource(
    private val repository: SearchRepository,
    private val query: String
) : PagingSource<String, Pair<String, Product>>() {
    override fun getRefreshKey(state: PagingState<String, Pair<String, Product>>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Pair<String, Product>> {
        return try {
            val result = repository.queryProductsFirebase(query, params.key, params.loadSize)

            LoadResult.Page(
                data = result.first.toList(),
                nextKey = if (result.first.size != params.loadSize) null else result.second,
                prevKey = null
            )
        } catch (e: NoInternetException) {
            LoadResult.Error(e)
        }
    }

}