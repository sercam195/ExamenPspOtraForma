package com.example.demo

import com.google.gson.Gson
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Usuario(@Id var nombre: String, var pass: String) {
    var claveCifrado = generarClave()

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    fun generarClave(): String {
        var palabra = ""
        repeat(20) {
            val abcd = 0..9
            val random1 = abcd.random()
            palabra += random1
        }
        return palabra
    }
}
@Entity
data class Admin(@Id var nombre: String, var pass: String) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}