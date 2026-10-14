package com.example.luhikawa.model.neurons
import com.example.luhikawa.model.activation.Activation

class Neuron() {
    private var weight = 0.5
    private var result: Double = 0.0
    private var bias: Double = 2.0
    private val activation = Activation()

    fun setDefault (){
        weight = 0.5
        result = 0.0
        bias = 2.0
    }

    fun predict(data: Double):Double{
        result = (data * weight) + bias
        return activation.sigmoid(result)
    }

    fun regression(error:Double) {

    }
}