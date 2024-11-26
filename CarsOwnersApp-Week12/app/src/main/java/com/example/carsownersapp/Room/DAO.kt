package com.example.carsownersapp.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CarsDAO {
    @Query("SELECT * FROM Car")
    suspend fun getAll(): List<Car>

    @Query("SELECT * FROM Car WHERE careModel LIKE :model")
    suspend fun findCityByName( model: String):  List<Car>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCar( car: Car)

    @Delete
    suspend fun deleteCar(car: Car)


}


@Dao
interface OwnerDAO {
    @Query("SELECT * FROM Owner")
    suspend fun getAll(): List<Owner>

    @Query("SELECT * FROM Owner WHERE ownerName LIKE :name")
    suspend fun getOneOwner( name: String):  Owner

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwner( o: Owner)

    @Query("DELETE FROM Owner where oid == :id ")
    suspend fun deleteOwner(id: Int)

    @Transaction
    @Query("SELECT * FROM Owner , Car WHERE Car.car_ownerID == :id and Owner.oid == Car.car_ownerID")
    suspend fun getAllCarsForOwner(id: Int): OwnersAndCars


    @Transaction
    @Query("DELETE FROM Car where car_ownerID == :id ")
    suspend fun deleteAllCarsForOwner(id: Int)

}

