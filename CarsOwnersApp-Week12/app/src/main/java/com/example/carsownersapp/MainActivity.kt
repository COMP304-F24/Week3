package com.example.carsownersapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.carsownersapp.Room.Car
import com.example.carsownersapp.logDataWorker.LogOwnersWorkers
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.plus

class MainActivity : ComponentActivity() {

    private lateinit var workManager : WorkManager
    var namesForLog = emptyList<String>()
    var idsForLog = emptyList<Int>()
    var yearsForLog = emptyList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workManager = WorkManager.getInstance(applicationContext)

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
                                    ondelete = { id ->
                                            myViewModel.deleteOwner(id)
                                            myViewModel.deleteFromCloudDB(id)
                                    })
                            }
                        }
                    }
                    if (showDialog) {
                        AddOwnerAlertDialog(
                            onSave = { name, year ->
                                namesForLog += name
                                var id = Math.random()*1000
                                idsForLog += id.toInt()
                                yearsForLog += year.toInt()

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

    override fun onPause() {
        super.onPause()
        var request = OneTimeWorkRequestBuilder<LogOwnersWorkers>().
        setInputData(
            workDataOf(
                "names" to  namesForLog.toTypedArray(),
                "years" to yearsForLog.toTypedArray(),
                "ids" to idsForLog.toTypedArray()
        )).setConstraints(Constraints(
           requiredNetworkType = NetworkType.CONNECTED)
        ).build()
        workManager.enqueue(request)

        namesForLog = emptyList()
        yearsForLog = emptyList()
        idsForLog = emptyList()
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
    }

