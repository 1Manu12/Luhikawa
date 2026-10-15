package com.example.luhikawa.model.neurons

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.luhikawa.model.layers.InputLayer
import com.example.luhikawa.model.layers.processingLayer
import com.example.luhikawa.model.layers.exitLayer
import com.example.luhikawa.domain.Chat
import com.example.luhikawa.data.TaskRepository

class network {

    private val BD = TaskRepository()
    private val neuronsNetwork = mutableListOf<Neuron>()
    private val inputLayer = InputLayer(BD)
    private val processingLayer = processingLayer()
    private val exitLayer = exitLayer(BD)
    private lateinit var dataInputPackage : List<Double>
    private var result : Double = 0.0

    fun createNeurons() {
        for (i in 1..8) {
            val newNeuron = Neuron(BD)
            neuronsNetwork.add(newNeuron)
        }
        for (i in neuronsNetwork){
            i.config()
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
        result = processingLayer.processing(dataInputPackage, neuronsNetwork, "normal")
        exitLayer.lector(result, ask)

        if (exitLayer.finalAnswer.isEmpty()){
            return """Lo lamentamos, al parecer a ocurrido un error en la respuesta, vuelve a intentarlo más tarde...
                
            """.trimIndent()
        }else {
            return exitLayer.finalAnswer
        }

    }

    fun networkB(ask: Int):String{
       result = processingLayer.processing(inputLayer.searchData(ask), neuronsNetwork, "search")
        exitLayer.lector(result, ask)
        return exitLayer.finalAnswer
    }

}