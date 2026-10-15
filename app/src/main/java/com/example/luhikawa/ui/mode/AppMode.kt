package com.example.luhikawa.ui.mode

import com.example.luhikawa.ui.HomeComponents.CategoriasTareas

enum class AppMode(val key: String) {
    TAREAS("tareas"),
    HOGAR("hogar"),
    MICROFINANZAS("microfinanzas");

    companion object {
        fun fromKey(key: String?): AppMode = entries.find { it.key == key } ?: TAREAS
    }
}

data class ModeUiConfig(
    val tituloNuevo: String,
    val etiquetaNombre: String,
    val placeholderNombre: String,
    val etiquetaClasificacion: String,
    val categorias: List<String>,
    val categoriaInicial: String,
    val categoriaConFrecuencia: String?,
    val mostrarMonto: Boolean,
    val etiquetaMonto: String,
    val textoGuardar: String,
    val toastGuardado: String,
    val toastCambioModo: String,
    val tituloLista: String,
    val textoSinItems: String,
    val descripcionIA: String
)

fun AppMode.config(): ModeUiConfig = when (this) {
    AppMode.TAREAS -> ModeUiConfig(
        tituloNuevo = "NUEVA TAREA",
        etiquetaNombre = "Nombre de la tarea",
        placeholderNombre = "Ej: Cita con el dentista",
        etiquetaClasificacion = "Clasificación",
        categorias = CategoriasTareas,
        categoriaInicial = "Trabajo",
        categoriaConFrecuencia = "Hábitos",
        mostrarMonto = false,
        etiquetaMonto = "",
        textoGuardar = "GUARDAR",
        toastGuardado = "¡Guardado con éxito!",
        toastCambioModo = "Modo tareas activado",
        tituloLista = "Tareas asignadas",
        textoSinItems = "No hay tareas para este día",
        descripcionIA = "tareas, hábitos y recordatorios personales"
    )
    AppMode.HOGAR -> ModeUiConfig(
        tituloNuevo = "NUEVA ACTIVIDAD DEL HOGAR",
        etiquetaNombre = "Nombre de la actividad",
        placeholderNombre = "Ej: Lavar la ropa",
        etiquetaClasificacion = "Área del hogar",
        categorias = listOf("Cocina", "Limpieza", "Compras", "Mantenimiento", "Mascotas", "Rutinas"),
        categoriaInicial = "Limpieza",
        categoriaConFrecuencia = "Rutinas",
        mostrarMonto = false,
        etiquetaMonto = "",
        textoGuardar = "GUARDAR",
        toastGuardado = "¡Actividad guardada!",
        toastCambioModo = "Modo hogar activado",
        tituloLista = "Actividades del hogar",
        textoSinItems = "No hay actividades para este día",
        descripcionIA = "actividades y organización del hogar"
    )
    AppMode.MICROFINANZAS -> ModeUiConfig(
        tituloNuevo = "NUEVO MOVIMIENTO",
        etiquetaNombre = "Concepto",
        placeholderNombre = "Ej: Cobro a cliente",
        etiquetaClasificacion = "Tipo de movimiento",
        categorias = listOf("Ingreso", "Gasto", "Ahorro", "Deuda", "Cobro pendiente"),
        categoriaInicial = "Ingreso",
        categoriaConFrecuencia = null,
        mostrarMonto = true,
        etiquetaMonto = "Monto",
        textoGuardar = "GUARDAR",
        toastGuardado = "¡Movimiento guardado!",
        toastCambioModo = "Modo microfinanzas activado",
        tituloLista = "Movimientos",
        textoSinItems = "No hay movimientos para este día",
        descripcionIA = "microfinanzas: ingresos, gastos, ahorros, deudas y cobros pendientes (con monto)"
    )
}