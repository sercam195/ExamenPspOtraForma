package com.example.demo

import com.google.gson.Gson
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

data class Error(var codigo: Int, var motivo: String) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}