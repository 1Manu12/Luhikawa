package com.example.luhikawa.model.neurons

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.luhikawa.model.layers.InputLayer
import com.example.luhikawa.model.layers.processingLayer
import com.example.luhikawa.model.layers.exitLayer
import com.example.luhikawa.domain.Chat

class network {
    private val neuronsNetwork = mutableListOf<Neuron>()
    private val inputLayer = InputLayer()
    private val processingLayer = processingLayer()
    private val exitLayer = exitLayer()
    private lateinit var dataInputPackage : List<Double>
    private var result : Double = 0.0

    fun createNeurons() {
        for (i in 1..8) {
            val newNeuron = Neuron()
            neuronsNetwork.add(newNeuron)
        }
    }

    fun neuronsDefaultValue() {
        for(neuron in neuronsNetwork){
            neuron.setDefault()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun networkA (data: List<Any>, ask : Int):String {
        dataInputPackage = inputLayer.validatedData(data).toList()
        result = processingLayer.processing(dataInputPackage, neuronsNetwork)
        exitLayer.lector(result, ask)

        if (exitLayer.finalAnswer.isEmpty()){
            return """Lo lamentamos, al parecer a ocurrido un error en la respuesta, vuelve a intentarlo más tarde...
                
            """.trimIndent()
        }else {
            return exitLayer.finalAnswer
        }

    }

}