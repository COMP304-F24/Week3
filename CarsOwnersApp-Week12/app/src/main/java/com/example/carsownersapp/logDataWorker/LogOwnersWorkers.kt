package com.example.carsownersapp.logDataWorker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LogOwnersWorkers(private val appContext: Context,
                       private val param: WorkerParameters) :
    CoroutineWorker(appContext,param){

    override suspend fun doWork(): Result {
            val db = Firebase.firestore
        var done = 0;
            var ownerNames = param.inputData.getStringArray("names");
            var yobs = param.inputData.getIntArray("years");
            var ids = param.inputData.getIntArray("ids");

        var counter = 0
        if (ownerNames != null) {
            while (counter < ownerNames.size){
                val owner = hashMapOf(
                    "OwnerName" to ownerNames[counter],
                    "YOB" to (yobs?.get(counter) ?: 1990),
                    "oID" to (ids?.get(counter) ?: 0)
                )

                db.collection("OwnersCollection")
                    .add(owner)
                    .addOnSuccessListener { documentReference ->
                        Log.d("MyAPP", "DocumentSnapshot added with ID In Background Task: ${documentReference.id}")
                        done = 0
                    }
                    .addOnFailureListener { e ->
                        Log.w("MYAPP", "Error adding document", e)
                        done = 1
                    }
                counter++
            }
            }
        if (done == 1) {
            return Result.success()
        }else
            return  Result.failure()
    }
    }