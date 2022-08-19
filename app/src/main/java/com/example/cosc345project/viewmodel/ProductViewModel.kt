package com.example.cosc345project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.repository.RetailersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    retailersRepository: RetailersRepository
) : ViewModel() {
    val product = MutableStateFlow(Pair("", Product(information = mutableListOf())))

    init {
        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }
    }

    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())

    val localRetailerInfo = MutableStateFlow(listOf<RetailerProductInformation>())
    val nonLocalRetailerInfo = MutableStateFlow(listOf<RetailerProductInformation>())
}