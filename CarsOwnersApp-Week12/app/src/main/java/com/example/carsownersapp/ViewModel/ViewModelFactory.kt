package com.example.carsownersapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore


class ViewModelFactory(private val repository: AppRepository)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CarsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}
