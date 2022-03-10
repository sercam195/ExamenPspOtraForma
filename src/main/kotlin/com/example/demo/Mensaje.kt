package com.example.demo

import com.google.gson.Gson
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
@Entity
data class Mensaje (var texto:String, var usuarioId: String, var id : Int) {
    @Id
    @GeneratedValue
    var idd = 0
    var time = System.currentTimeMillis()
    override fun toString(): String {
        val gson= Gson()
        return gson.toJson(this)
    }
}