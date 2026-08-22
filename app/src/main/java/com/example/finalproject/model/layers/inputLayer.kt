package com.example.finalproject.model.layers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class InputLayer {

    @RequiresApi(Build.VERSION_CODES.O)

    fun validatedData(dataInputPackage : List<Any>) : Array<Double> {
        for (data in dataInputPackage){
            when (data) {
                is Int -> {

                }
                is String -> {

                }
                is Double -> {

                }
                is LocalDate -> {

                }
                else -> {
                    println("...")
                }
            }
        }
        val dataPackage = dataInputPackage.map {it as Double }.toTypedArray()
        return dataPackage
    }

}