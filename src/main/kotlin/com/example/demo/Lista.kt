package com.example.demo

import com.google.gson.Gson

data class Lista(var lista: MutableList<Any>) {
    override fun toString(): String {
        val gson= Gson()
        return gson.toJson(this)
    }
}