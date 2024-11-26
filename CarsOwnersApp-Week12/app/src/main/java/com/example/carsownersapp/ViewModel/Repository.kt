package com.example.carsownersapp.ViewModel

import android.util.Log
import com.example.carsownersapp.Room.Car
import com.example.carsownersapp.Room.CarsDAO
import com.example.carsownersapp.Room.Owner
import com.example.carsownersapp.Room.OwnerDAO
import com.example.carsownersapp.Room.OwnersAndCars
import com.google.firebase.firestore.FirebaseFirestore

class AppRepository(private val carsDAO: CarsDAO,
                    private val ownerDAO: OwnerDAO,
                    private val firebaseDB: FirebaseFirestore
    ) {

    suspend fun getAllOwners(): List<Owner>{
        return ownerDAO.getAll()!!
    }
    suspend fun addOwnerToDB(o: Owner){
        ownerDAO.insertOwner(o)
    }

    suspend fun deleteOwnerAndCars(id: Int){
       ownerDAO.deleteAllCarsForOwner(id)
        ownerDAO.deleteOwner(id)
    }

    suspend fun getAllCarsForOwner(o: Int): OwnersAndCars{
        return ownerDAO.getAllCarsForOwner(o)
    }
    suspend fun addCarToDB(c: Car){
        carsDAO.insertCar(c)
    }

    suspend fun deleteCar(c: Car){
        carsDAO.deleteCar(c)
    }

fun deleteFromCloudDB(id: Int){
    firebaseDB.collection("OwnersCollection").
    whereEqualTo("oID", id).get().
    addOnSuccessListener{ documents ->
        for (document in documents) {
            Log.d("firebase", "${document.id} => ${document.data}")
            firebaseDB.collection("OwnersCollection").document(document.id).delete()
        }
    }.addOnFailureListener{exception ->
        Log.w("firebase", "Error getting documents: ", exception)
    }
}

    fun addOwnerToCloudDB(o:Owner){
        val owner = hashMapOf(
            "OwnerName" to  o.ownerName,
            "YOB" to o.yearOfBirth,
            "oID" to o.oid,
        )

// Add a new document with a generated ID
        firebaseDB.collection("OwnersCollection")
            .add(owner)
            .addOnSuccessListener { documentReference ->
                Log.d("MyAPP", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("MYAPP", "Error adding document", e)
            }
    }

}