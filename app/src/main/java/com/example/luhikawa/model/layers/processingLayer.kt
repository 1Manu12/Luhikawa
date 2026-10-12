package com.example.luhikawa.model.layers

import com.example.luhikawa.model.neurons.Neuron
class processingLayer {

    fun processing (dataInputLayer : List<Double>, neuronsNetwork: List<Neuron>):  Double {
        var counter = 0
        var result = 0.0

        for (neuron in neuronsNetwork) {
            val data = dataInputLayer[counter]

            result += neuron.predict(data)

            counter++
        }

        result /= neuronsNetwork.size

        return result
    }

}