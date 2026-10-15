package com.example.luhikawa.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.luhikawa.model.neurons.network

data class Errors(
    val dataListEmpty: String = "Si elegiste esta opcion debes de digitar los datos pedidos, vuelve a intentarlo de nuevo.",
    val categoria: String = "",
    val respuesta: String = ""
)

class Chat {

    var fail = false
    var ask : Int = 0
    private val network = network()
    var waitingData : Boolean = false
        get() {
            return field
        }
        set(value) {
            field = value
        }

    init {
        network.createNeurons()
    }

    fun greeting (): String = """
           Soy el chat bot de luhi, me puedes utilizar para tareas basicas cada vez que no tengas más tokens
           ¡Mira mis opciones!
           
           1) Te ayudo crear una tarea con las especificaciones que me des.
           2) Crear una tarea adaptada a ti, segun mi criterio.
           3) Recomendarte que tareas debes de hacer primero.
           4) Resetea toda tu informacion.
           
           ¡estare listo para cuando tu lo estes, digita el numero correspondiente a la opcion y empezamos!
           
           -Nota- este bot contiene un sistema de prediccion que apunta a tu rendimiento, por lo que lo hace propenso a ciertas brechas
        """.trimIndent()

    fun especificts (): String = """
        Listo, te ayudare a crear una tarea para ti, necesito que me des las siguientes especificaciones de acuerdo
        a la siguiente regla, digitame cada especificaion en un rango del 1 al 10, siendo 10 el numero de mas complejidad para ti, ademas de ser el rango más alto,
        separandolas por una coma.
        ejemplo: 7,6,8,2,7 
        digita 0 si no sabes que responder...
        
        1, ¿concideras que la tarea es muy extensa?
        2, ¿segun tu, cuanta complejidad contiene la tarea?
        3, ¿la materia de la cual se te fue asignida la tarea, concideras que es una materia que se te da muy duro entender?
        4, ¿cuanto concideras que entiendes del tema?
        5, ¿Qué nivel de precisión o detalle requiere en la entrega final?
        6, ¿Cuanto de comodo te sientes con el formato de entrega solicitado (código, redacción, presentación, etc.)?
        7, ¿Cauanto concideras que tienes de conocimeinto previo del tema?
        8, ¿tines poca o mucha disponibilidad al dia para realizar la tarea?
       
    """.trimIndent()

    @RequiresApi(Build.VERSION_CODES.O)
    fun message(selection : Int): String{
        ask = selection
        when (selection) {
            1 -> {
                waitingData = true
                return especificts()
            }
            2 -> {
                return especificts()
            }
            3 -> {

            }
            4 -> {
                return """
                  ¡ALERTA! 
                    ¿Estas seguro de que deseas resetear el bot?
                    si deseas realizar esta accion ten en cuenta que todos tus datos usados por este bot se eliminaran,
                    aumentando la probabilidad de posbiles brechas en la informacion del bot, solo hazlo si estas completamente seguro
                    que necesites borrar tu informacion.
                    
                    Para continuar digita 1.
                    Para Cancelar digita 2.
                    
                """.trimIndent()
            }
            else -> {
                return especificts()
            }
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dataCollector(data : String): String{

        if (data.isEmpty()){
            fail = true
            return "añadir error"
        } else {
            val dataList: List<Any> = data
                .split(",")
                .map { it.trim().toInt() }

            if (dataList.size == 8){
                return activateModel(dataList, ask)
            }else{
                fail = true
                return "añadir error"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun activateModel(dataList : List<Any>, ask : Int): String{
        if (dataList.isEmpty()){
            return "opcion no valida por el momento"
        } else {
            return network.networkA(dataList, ask)
        }
    }

    fun resetModel(){
        network.neuronsDefaultValue()

    }

}