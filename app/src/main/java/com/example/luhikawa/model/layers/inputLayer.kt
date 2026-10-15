package com.example.luhikawa.model.layers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import com.example.luhikawa.data.TaskRepository

class InputLayer(
    val BD : TaskRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)

    fun validatedData(dataInputPackage: List<Any>): Array<Double> {

        val dataPackage = dataInputPackage.map { data ->

            when (data) {

                is Int -> {
                    data.toDouble()
                }
                is Double -> {
                    data
                }
                is LocalDate -> {
                    val actual = LocalDate.now()

                    val dias = abs(
                        ChronoUnit.DAYS.between(actual, data)
                    )

                    (10 - dias).coerceIn(0, 10).toDouble()
                }
                else -> {
                    0.0
                }
            }
        }

        return dataPackage.toTypedArray()
    }

    fun searchData(ask: Int): List<Boolean> {

        val jobsCompleted = mutableListOf<Boolean>()

        TaskRepository().getTasksQuery("Trabajo")
            .get()
            .addOnSuccessListener { result ->

                for (document in result.documents) {
                    val completed = document.getBoolean("completed") ?: false

                    if(completed){
                        jobsCompleted.add(completed)
                    }else{
                        jobsCompleted.add(completed)
                    }
                }
            }

        TaskRepository().getTasksQuery("Estudio")
            .get()
            .addOnSuccessListener { result ->

                for (document in result.documents) {
                    val completed = document.getBoolean("completed") ?: false

                    if(completed){
                        jobsCompleted.add(completed)
                    }else{
                        jobsCompleted.add(completed)
                    }
                }
            }

        return jobsCompleted.toList()
    }

}