package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository

interface MensajesRepository : JpaRepository<Mensaje, Int>