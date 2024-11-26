package com.example.carsownersapp.BackGroundTasks

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogAllOwners(private val appContext: Context,
                   private val param: WorkerParameters) :
    CoroutineWorker(appContext,param) {

    override suspend fun doWork(): Result {
// multiple owners and insert all of them to the firebase
        var ownersIds = param.inputData.getIntArray("ids")
        var ownersNames = param.inputData.getStringArray("names")
        var years = param.inputData.getIntArray("years")
        val db = Firebase.firestore
        var done = 0
        // get three arrays
        // array of ownersIDs
        // array of ownersNames
        // array of years

        var logsize = 0
        if (ownersIds != null) {
            while (logsize < ownersIds.size) {
                // insert all owners to firebase
                val owner = hashMapOf(
                    "OwnerName" to (ownersNames?.get(logsize) ?: ""),
                    "YOB" to (years?.get(logsize) ?: 1990),
                    "oID" to ownersIds[logsize]
                )
                db.collection("OwnersCollection")
                    .add(owner)
                    .addOnSuccessListener { documentReference ->
                        Log.d("MyAPP", "DocumentSnapshot added with ID: ${documentReference.id}")
                        done = 1
                    }
                    .addOnFailureListener { e ->
                        Log.w("MYAPP", "Error adding document", e)
                        done = 0
                    }
                logsize++

            }
            Log.d("Background Tasks", "In Background Taks")
        }
        if (done == 1)
            return Result.success()
        else
            return Result.failure()

    }
}