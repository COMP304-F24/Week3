package com.example.carsownersapp.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Car::class,Owner::class], version = 1)
abstract class CarOwnerDatabase : RoomDatabase() {
    abstract val carDao: CarsDAO
    abstract val ownerDao: OwnerDAO

    companion object {
        @Volatile
        private var INSTANCE: CarOwnerDatabase? = null
        fun getInstance(context: Context): CarOwnerDatabase {
            // ensuring that only one thread can execute the block
            // of code inside the synchronized block at any given time
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    // Creating the Database Object
                    instance = Room.databaseBuilder(
                        context = context,
                        CarOwnerDatabase::class.java,
                        "car_ownersDB"
                    ).build()
                }
                INSTANCE = instance
                return instance
            }
        }
    }
}