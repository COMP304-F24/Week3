package com.example.week4project.Views

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.week4project.Model.Address
import com.example.week4project.AddressActivity
import com.example.week4project.R
import com.example.week9_navigation.Navigation.NavItem

@Composable
fun MyBottomBar(navController: NavController) {
    val navItems = listOf(NavItem.Search, NavItem.Email, NavItem.Favorite, NavItem.Call)
    var selectedItem by rememberSaveable {
        mutableStateOf(0)
    }
    NavigationBar {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.path){
                        navController.graph.startDestinationRoute?.let {
                                route -> popUpTo(route){
                                    saveState = true
                                }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {Text(item.title)})
        }
    }



//    var cnx = LocalContext.current
//    BottomAppBar {
//        Row(modifier = Modifier.fillMaxWidth(1f) ,
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceEvenly
//            ){
//            Text("Search")
//            IconButton(onClick = {
//                val intent = Intent(Intent.ACTION_DIAL).apply {
//                    data = Uri.parse("tel:4167771111")
//                }
//                cnx.startActivity(intent)
//
//            }) {
//               Icon(painterResource(R.drawable.baseline_local_phone_24), contentDescription = null)
//            }
//            IconButton(onClick = {
//                    var intent = Intent(cnx, AddressActivity::class.java)
//                    intent.putExtra("name","Rania")
//                    intent.putExtra("number", 33)
//                    var myaddress = Address(22,"Yong","Toronto","m3a1ql")
//                    intent.putExtra("myaddress",myaddress)
//                    Log.d("In Main Activity", myaddress.street)
//                    cnx.startActivity(intent)
//
//            }) {
//                Icon(Icons.Default.Favorite, contentDescription = null)
//            }
//            IconButton(onClick = {
//                val intent = Intent(Intent.ACTION_SEND).apply {
//                    type = "*/*"
//                    putExtra(Intent.EXTRA_EMAIL, "john@hotmail.com")
//                    putExtra(Intent.EXTRA_BCC, "rania@hotmail.com")
//                    putExtra(Intent.EXTRA_CC, "rania@hotmail.com")
//                    putExtra(Intent.EXTRA_SUBJECT, "From My App")
//                }
//                cnx.startActivity(intent)
//
//            }) {
//                Icon(Icons.Default.Email, contentDescription = null)
//            }
//
//
//        }
//    }

}