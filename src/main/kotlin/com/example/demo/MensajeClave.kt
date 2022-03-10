package com.example.demo

import com.google.gson.Gson
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
data class MensajeClave (var mensaje: Mensaje, var clave: String) {
    override fun toString(): String {
        val gson= Gson()
        return gson.toJson(this)
    }
}