package com.example.luhikawa.model.layers

import com.example.luhikawa.model.neurons.Neuron
class processingLayer {

    fun processing(dataInputLayer: List<Any>, neuronsNetwork: List<Neuron>, type: String): Double {

        if (type == "normal") {
            var counter = 0
            var result = 0.0

            for (neuron in neuronsNetwork) {
                val data = dataInputLayer.get(counter)

                if(data is Double) {
                        result += neuron.predict(data)
                }
                counter++
            }
            result /= neuronsNetwork.size

            return result
        }

        if (type == "search") {
            var sumData = 0.0

            for (i in dataInputLayer) {
                if (i == true) {
                    sumData += 0
                } else {
                    sumData += 1
                }

            }
            val sumResult = neuronsNetwork.get(1).predict(sumData)
            return sumResult

        }else {
            return 0.0
        }


    }
}