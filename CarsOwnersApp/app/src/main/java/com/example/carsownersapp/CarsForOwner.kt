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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CarsForOwner : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        var oid = intent.getIntExtra("oid",0)
        // Room DB
        val database = CarOwnerDatabase.getInstance(applicationContext)
        // Repository
        val repository = database?.let { AppRepository(it.carDao, it.ownerDao,db ) }
//        // ViewModel Factory
        val viewModelFactory = repository?.let { ViewModelFactory(it) }
//        // ViewModel
        val myViewModel = ViewModelProvider(
            this,
            viewModelFactory!!
        )[CarsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            var showDialog by remember { mutableStateOf(false) }

            CarsOwnersAppTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            showDialog = true

                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Car")
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        var carsForOwner = myViewModel.getCarsForOwners(oid)
                        //  var cars = carsForOwner?.ownedCars
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (carsForOwner != null) {
                                items(carsForOwner.ownedCars.size) { index ->
                                    CarItem(carsForOwner.ownedCars.get(index), ondelete = {id ->
                                        // delete one car
                                    } )
                                }
                            }
                        }
                        if (showDialog) {
                            AddCarAlertDialog(
                                onSave = { model, year ->
                                    myViewModel.addCarToOwner(Car((Math.random()*1000).toInt(),
                                        oid,model,
                                        year.toInt()))
                                    showDialog = false
                                },
                                onCancel = { showDialog = false }
                            )
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun CarItem(car: Car, ondelete: (Int)->Unit) {
        var selectedIndex by remember {mutableStateOf(-1)}
        var context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp).selectable(
                    selected = car.cid == selectedIndex,
                    onClick = {
                        if (selectedIndex != car.cid) {
                            selectedIndex = car.cid
                        }
                        else selectedIndex = -1
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = car.careModel)
            Text(text = car.caryear.toString())
            IconButton(onClick = {
                ondelete(car.cid)
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }





    @Composable
    fun AddCarAlertDialog(
        onSave: (String, String) -> Unit,
        onCancel: () -> Unit
    ) {
        var model by remember { mutableStateOf(TextFieldValue("")) }
        var year by remember { mutableStateOf(TextFieldValue("")) }

        AlertDialog(
            onDismissRequest = onCancel,
            title = {
                Text(text = "Add New Owner")
            },
            text = {
                Column {
                    TextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Car Model") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    TextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSave(model.text, year.text)
                }) {
                    Text("Add To Owner")
                }
            },

            dismissButton = {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        )
    }
}
