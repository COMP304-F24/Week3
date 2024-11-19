package com.example.carsownersapp.Room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "Car")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val cid: Int,
    val car_ownerID: Int,
    val careModel: String,
    val caryear: Int
)

@Entity(tableName = "Owner")
data class Owner(
    @PrimaryKey(autoGenerate = true)
    val oid: Int,

    val ownerName: String,
    val yearOfBirth: Int
)

data class OwnersAndCars(
    @Embedded val owner: Owner,
    @Relation(
        parentColumn = "oid",
        entityColumn = "car_ownerID"
    )
    val ownedCars: List<Car>
)