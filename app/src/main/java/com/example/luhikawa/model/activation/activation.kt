package com.example.luhikawa.model.activation

import kotlin.math.exp
class Activation {

    fun sigmoid (data:Double):Double{
        return 1.0 / (1.0 + exp(-data))
    }

}