package com.example.week56test.ViewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.week56test.Models.Product
import com.example.week56test.Models.ProductRepository

class ProductViewModel() : ViewModel() {
        var repo = ProductRepository()

    val stateList : SnapshotStateList<Product> = mutableStateListOf<Product>().apply {
        addAll( repo.getInitProducts())
    }

    fun getProducts() : ArrayList<Product>{
        return repo.getInitProducts()
    }

    fun addNewProduct(p: Product) {
        repo.addNewProduct(p)
        stateList.add(p)
    }
}