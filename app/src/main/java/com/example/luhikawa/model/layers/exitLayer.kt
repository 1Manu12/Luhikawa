package com.example.luhikawa.model.layers;

import com.example.luhikawa.domain.chat
class exitLayer() {
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