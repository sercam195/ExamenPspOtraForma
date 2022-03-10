package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository

interface AdminsRepository : JpaRepository<Admin, Int>