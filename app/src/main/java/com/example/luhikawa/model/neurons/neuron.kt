package com.example.luhikawa.model.neurons
import com.example.luhikawa.MyApp
import com.example.luhikawa.model.activation.Activation
import com.example.luhikawa.data.TaskRepository
import com.example.luhikawa.data.storage

class Neuron(
    val BD : TaskRepository
) {

    private val storage = storage(MyApp.getInstance())
    private var weight = 0.5
    private var result: Double = 0.0
    private var bias: Double = 2.0
    private val activation = Activation()

    fun setDefault (){
        storage.save(0.5)
    }

    fun predict(data: Double):Double{
        result = (data * weight) + bias
        regression()
        return activation.sigmoid(result)
    }



    fun regression() {

        val listCompleted = mutableListOf<Boolean>()

        TaskRepository().getTasksQuery("Trabajo")
            .get()
            .addOnSuccessListener { result ->

                for (document in result.documents) {
                    val completed = document.getBoolean("completed") ?: false

                    if(!completed) {
                        listCompleted.add(completed)
                    }
                }
            }

        TaskRepository().getTasksQuery("Estudio")
            .get()
            .addOnSuccessListener { result ->

                for (document in result.documents) {
                    val completed = document.getBoolean("completed") ?: false

                    if(!completed) {
                        listCompleted.add(completed)
                    }
                }
            }

        if(listCompleted.size > 4){
            bias += 0.2
        }

        if(listCompleted.size < 4){
            if (this.bias != 0.0) {
                bias -= 1
            }
        }

        storage.save(bias)
    }

    fun config(){
        bias = storage.read()
    }
}