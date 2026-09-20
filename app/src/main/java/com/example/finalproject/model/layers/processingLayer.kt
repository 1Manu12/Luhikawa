package com.example.finalproject.model.layers

import com.example.finalproject.model.neurons.Neuron
class processingLayer {

    private var dataPackage = mutableListOf<Double>()

    fun processing (dataInputLayer : List<Double>, neuronsNetwork: List<Neuron>):  List<Double>{
        var counter = 0
        for (neuron in neuronsNetwork) {

            val data =  dataInputLayer.get(counter)
            dataPackage.add(neuron.predict(data))
            counter++
        }
        return dataPackage.toList()
    }

}