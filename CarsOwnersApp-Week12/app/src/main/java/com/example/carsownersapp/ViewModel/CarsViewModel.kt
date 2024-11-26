package com.example.carsownersapp.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.example.carsownersapp.Room.Car
import com.example.carsownersapp.Room.Owner
import com.example.carsownersapp.Room.OwnersAndCars
import com.example.carsownersapp.logDataWorker.LogOwnersWorkers
import kotlinx.coroutines.launch


class CarsViewModel(private val repository: AppRepository
    ) : ViewModel() {

    var db by mutableStateOf<List<Car>>(emptyList())
        private set

    var ocOBJ by mutableStateOf<OwnersAndCars?>(null)
        private set

    var dbOwners by mutableStateOf<List<Owner>>(emptyList())
        private set


    init {
        viewModelScope.launch {
            val owners = repository.getAllOwners()
            dbOwners = owners
        }
    }



    fun deleteFromCloudDB(id: Int){
        repository.deleteFromCloudDB(id)
    }

    fun addOwnerTOCloudDB(o:Owner){
        repository.addOwnerToCloudDB(o)
    }

    fun getCarsForOwners(oID: Int) : OwnersAndCars? {
        viewModelScope.launch {
            val db = repository.getAllCarsForOwner(oID)
            ocOBJ = db
        }
        return ocOBJ
    }

    fun getAllOwners() :List<Owner>{
        viewModelScope.launch {
            val db = repository.getAllOwners()
            dbOwners = db
        }
        return dbOwners
    }

    fun addOnwer(o: Owner) {
        viewModelScope.launch {
             repository.addOwnerToDB(o)

        }

    }

    fun deleteOwner(o: Int) {
        viewModelScope.launch {
            repository.deleteOwnerAndCars(o)

        }

    }
    fun addCarToOwner(c: Car) {
        viewModelScope.launch {
            repository.addCarToDB(c)

        }

    }
}
