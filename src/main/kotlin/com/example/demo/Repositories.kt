package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository

interface AdminsRepository : JpaRepository<Admin, String>
interface UsuariosRepository : JpaRepository<Usuario, String>
interface MensajesRepository : JpaRepository<Mensaje, Int>