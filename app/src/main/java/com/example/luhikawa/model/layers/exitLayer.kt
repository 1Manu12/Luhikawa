package com.example.luhikawa.model.layers;

import com.example.luhikawa.data.TaskRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class exitLayer(
    val BD : TaskRepository
) {
    var finalAnswer : String = ""
        private set



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
        var importance="No-especifico"

        if(ask == 1) {
            when  {
                data >= 0.999 -> {
                    answer = "Perfecto tarea creada, la tines bastante complicada, "+ answers[1]?.random()
                    importance = "Alta"
                }

                data >= 0.993 -> {
                   answer = "Listo, tarea creada, tienes que meterle buen esfuerzo para que logres acabarla, "+ answers[1]?.random()
                    importance = "Media"
                }

                data >= 0.7 -> {
                    answer = "echo tarea creada, es practicamante pan comido."
                    importance = "Baja"
                }

                else -> {
                    answer ="Tarea añadida es pan comido, solo no la ignores... "
                    importance = "Baja"
                }
            }

            actions("create", importance)

        }else if(ask == 2){
            when  {
                data >= 0.999 -> {
                    answer = "listo, "+answers[2]?.random()
                    importance = "Alta"
                }

                data >= 0.993 -> {
                    answer = "creacion exitosa, "+answers[2]?.random()
                    importance = "Media"
                }

                data >= 0.7 -> {
                    answer = "perfecto, "+answers[2]?.random()
                    importance = "Baja"
                }

                else -> {
                    answer ="es pan comido, "+answers[2]?.random()
                    importance = "Baja"
                }
            }

            actions("create", importance)

        }
        return answer
    }

    fun actions(actions : String, importance : String){

        if(actions == "create") {
            val format = DateTimeFormatter
                .ofPattern("dd MMM yyyy", Locale.getDefault())

            val dateFormat = LocalDate.now().format(format)

            val hour = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))

            val newTask = mapOf<String, Any>(
                "category" to "Trabajo",
                "completed" to false,
                "date" to dateFormat.toString(),
                "dueDate" to dateFormat.toString(),
                "icon" to 0,
                "importance" to importance,
                "time" to hour.toString(),
                "title" to "no especifica"
            )

            BD.saveTask(
                newTask,
                onSuccess = {

                },

                onFailure = { error ->

                }
            )
        }
    }

}