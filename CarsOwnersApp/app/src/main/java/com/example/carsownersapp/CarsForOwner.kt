package com.example.carsownersapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.carsownersapp.Room.Car
import com.example.carsownersapp.Room.CarOwnerDatabase
import com.example.carsownersapp.Room.Owner
import com.example.carsownersapp.Room.OwnersAndCars
import com.example.carsownersapp.ViewModel.AppRepository
import com.example.carsownersapp.ViewModel.CarsViewModel
import com.example.carsownersapp.ViewModel.ViewModelFactory
import com.example.carsownersapp.ui.theme.CarsOwnersAppTheme

class CarsForOwner : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var oid = intent.getIntExtra("oid",0)
        // Room DB
        val database = CarOwnerDatabase.getInstance(applicationContext)

        // Repository
//        val repository = database?.let { AppRepository(it.carDao, it.ownerDao) }
//
//        // ViewModel Factory
//        val viewModelFactory = repository?.let { ViewModelFactory(it) }
//
//        // ViewModel
//        val myViewModel = ViewModelProvider(
//            this,
//            viewModelFactory!!
//        )[CarsViewModel::class.java]
//


        enableEdgeToEdge()
        setContent {
            CarsOwnersAppTheme {
                Scaffold(
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                   //     var carsForOwner = myViewModel.getCarsForOwners(oid)
//                    //  var cars = carsForOwner?.ownedCars
//                        LazyColumn(
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            if (cars != null) {
//                                items(cars.size) { index ->
//                                    OwnerItem(cars.get(index) )
//                                }
//                            }
//                        }
                    }
                }
            }

        }
    }

    @Composable
    fun OwnerItem(car: Car) {
        var selectedIndex by remember { mutableStateOf(-1) }
        var context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = car.careModel)
            Text(text = car.caryear.toString())
        }
    }
}