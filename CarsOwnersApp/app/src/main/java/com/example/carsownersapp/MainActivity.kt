package com.example.carsownersapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton

import androidx.work.WorkManager
import androidx.work.workDataOf
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.carsownersapp.Room.CarOwnerDatabase
import com.example.carsownersapp.Room.CarsDAO
import com.example.carsownersapp.Room.Owner
import com.example.carsownersapp.ViewModel.AppRepository
import com.example.carsownersapp.ViewModel.CarsViewModel
import com.example.carsownersapp.ViewModel.ViewModelFactory
import com.example.carsownersapp.ui.theme.CarsOwnersAppTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import com.example.carsownersapp.BackGroundTasks.LogAllOwners
import com.example.carsownersapp.Room.Car
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    private lateinit var workManager : WorkManager

    var names = emptyList<String>()
    var ids = emptyList<Int>()
    var years = emptyList<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workManager =  WorkManager.getInstance(applicationContext)


       // val firebase =  FirebaseApp.initializeApp(this)
        val db = Firebase.firestore


        // Room DB
        val database = CarOwnerDatabase.getInstance(applicationContext)

        // Repository
        val repository = database?.let { AppRepository(it.carDao, it.ownerDao,db) }

        // ViewModel Factory
        val viewModelFactory = repository?.let { ViewModelFactory(it) }

        // ViewModel
        val myViewModel = ViewModelProvider(
            this,
            viewModelFactory!!
        )[CarsViewModel::class.java]

        enableEdgeToEdge()
        setContent {

        CarsOwnersAppTheme {
                var showDialog by remember { mutableStateOf(false) }
                var ownerslist by remember {  mutableStateOf(emptyList<Owner>()) }

                Scaffold(

                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            showDialog = true

                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Owner")
                        }
                    }
                ) { innerPadding ->
                    ownerslist = myViewModel.getAllOwners()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(ownerslist.size) { index ->
                                OwnerItem(owner = ownerslist[index],
                                    ondelete = {id ->
                                    myViewModel.deleteOwner(id)
                                       // ownerslist = myViewModel.getAllOwners()
                                })
                            }
                        }
                    }
                    if (showDialog) {
                        AddOwnerAlertDialog(
                            onSave = { name, year ->

                                names += name
                                years += year.toInt()
                                var id = Math.random() * 1000
                                ids += id.toInt()

                                myViewModel.addOnwer(Owner(id.toInt(),name,year.toInt()))
                                showDialog = false
                            },
                            onCancel = { showDialog = false }
                        )
                    }
                }
            }

            }
        }
    @Composable
    fun OwnerItem(owner: Owner, ondelete: (Int)->Unit) {
        var selectedIndex by remember {mutableStateOf(-1)}
        var context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp).selectable(
                    selected = owner.oid == selectedIndex,
                    onClick = {
                        if (selectedIndex != owner.oid) {
                            selectedIndex = owner.oid
                            var i = Intent(context,CarsForOwner::class.java)
                            i.putExtra("oid",owner.oid)
                            startActivity(i);
                        }
                        else selectedIndex = -1
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = owner.ownerName)
            Text(text = owner.yearOfBirth.toString())
            IconButton(onClick = {
                ondelete(owner.oid)
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }


    @Composable
    fun AddOwnerAlertDialog(
        onSave: (String, String) -> Unit,
        onCancel: () -> Unit
    ) {
        var name by remember { mutableStateOf(TextFieldValue("")) }
        var yearOfBirth by remember { mutableStateOf(TextFieldValue("")) }

        AlertDialog(
            onDismissRequest = onCancel,
            title = {
                Text(text = "Add New Owner")
            },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Owner Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    TextField(
                        value = yearOfBirth,
                        onValueChange = { yearOfBirth = it },
                        label = { Text("Year of Birth") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSave(name.text, yearOfBirth.text)
                }) {
                    Text("Save")
                }
            },

            dismissButton = {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        )
    }

    override fun onPause() {
        super.onPause()

        var reques = OneTimeWorkRequestBuilder<LogAllOwners>().setInputData(workDataOf(
            "names" to names.toTypedArray(),
            "ids" to ids.toTypedArray(),
            "years" to years.toTypedArray()
        )).setConstraints(
            Constraints(requiredNetworkType = NetworkType.CONNECTED,
                requiresBatteryNotLow = true)).build()

        WorkManager.getInstance(this).enqueue(reques)
    }
    }

