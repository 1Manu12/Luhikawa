package com.example.luhikawa.model.layers;

import android.widget.Toast
import com.example.luhikawa.data.TaskRepository
import com.example.luhikawa.domain.Chat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class exitLayer() {
    var finalAnswer : String = ""
        private set

    val BD = TaskRepository()

    private val answers = mapOf(
        1 to listOf(
            "pero, desde el fondo de mi codigo... siento que podras terminarla con exito (:",
            "asi que mejor ponte a trabajar en ella antes de que sea demasiado tarde",
            "Talvez, no te podre ayudar de mucho, pero tengo fe en esas habilidades tan geniales que tienes para que logres culminar tu deber"
        ),

        2 to listOf(
            "He creado la tarea de acuerdo a lo que conozco de ti.",
            "Tarea creada la pudes revisar cuando desees.",
            "Creacion exitosa."
        ),
    )
    fun lector(result : Double, ask : Int){

        finalAnswer = interpreter(result, ask)

    }

    fun interpreter(data : Double, ask : Int):String{
        var answer ="nothing"

        if(ask == 1) {
            when  {
                data >= 0.8 -> {
                    answer = "Perfecto tarea creada, la tines bastante complicada, "+ answers[1]?.random()
                }

                data >= 0.5 -> {
                   answer = "Listo, tarea creada, tienes que meterle buen esfuerzo para que logres acabarla, "+ answers[1]?.random()
                }

                data >= 0.3 -> {
                    answer = "echo tarea creada, es practicamante pan comido."
                }

                else -> {
                    answer ="Tarea añadida es pan comido, solo no la ignores... "
                }
            }

            val format = DateTimeFormatter
                .ofPattern("dd MMM yyyy", Locale.getDefault())

            val dateFormat = LocalDate.now().format(format)

            val hour= LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))

            val newTask = mapOf<String, Any>(
                "category" to "Trabajo",
                "completed" to false,
                "date" to dateFormat.toString(),
                "dueDate" to dateFormat.toString(),
                "icon" to 0,
                "importance" to "Alta",
                "time" to hour.toString(),
                "title" to "no especifica"
            )

            BD.saveTask(newTask,
                onSuccess = {
                    println("chechechevere")
                },

                onFailure = { error ->
                    println("error")
                }
            )

        }else if(ask == 2){
            when  {
                data >= 0.8 -> {
                    answer = "listo, "+answers[2]?.random()
                }

                data >= 0.5 -> {
                    answer = "creacion exitosa, "+answers[2]?.random()
                }

                data >= 0.3 -> {
                    answer = "perfecto, "+answers[2]?.random()
                }

                else -> {
                    answer ="es pan comido, "+answers[2]?.random()
                }
            }

        }

        return answer
    }

}