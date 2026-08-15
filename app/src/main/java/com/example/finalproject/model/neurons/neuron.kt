package com.example.finalproject.model.neurons
import com.example.finalproject.model.activation.Activation

class Neuron() {
    private var weight = 0.5
    private var data = 0
        get() = field
        set(value)  {field = value}
    private var result: Double = 0.0
    private val activation : Activation = TODO()

    fun predict():Double{
        result = data * weight
        return activation.digmoid(result)
    }

    fun regression(error:Double) {

    }
}