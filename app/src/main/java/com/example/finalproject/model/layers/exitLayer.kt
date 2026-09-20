package com.example.finalproject.model.layers;
class exitLayer {

    private var data : Double = 0.0
    private var ask : Int = 1
    fun interpreter(dataPackage: List<Double>){

        for (i in dataPackage){
            data = i
            if (ask == 1){
                completeTask(data)
            }
        }
    }

    fun completeTask(data : Double){
        var answer ="nothing"
        when (data) {
            in 0.8..0.99 -> {
                answer = "probablemente no acabes esta tarea"
            }
            in 0.5..0.79 -> {
                answer = "tines que meterle buen esfuerzo para que logres acarbarla"
            }
            in 0.3..0.49 -> {
                answer = "facil para que la acabes"
            }
            in 0.0..0.29 ->{
                answer = "extremadamente facil para que acabes esta tarea"
            }
        }
    }

    fun finalAnswer(){
        var finalAnswer = "nothing"
        when (ask) {
            1 -> {
                 finalAnswer = ""
            }
            2 -> {

            }
        }
    }
}
