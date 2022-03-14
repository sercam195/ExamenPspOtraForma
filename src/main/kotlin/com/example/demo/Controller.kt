package com.example.demo

import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.MessageDigest
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@RestController
class Controller(
    val usuariosRepository: UsuariosRepository,
    val mensajesRepository: MensajesRepository,
    val adminsRepository: AdminsRepository
) {

    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody usuario: Usuario): Any {
        val user = usuariosRepository.findByIdOrNull(usuario.nombre)
        user?.let {
            if (comprobarContrasenaUser(it,usuario)) {
                return it.claveCifrado
            }
            return Error(1, "Pass incorrecta")
        }
        usuariosRepository.save(usuario)
        return usuario.claveCifrado
    }


    @PostMapping("crearMensaje")
    fun crearMensaje(@RequestBody mensaje: Mensaje): Any {
        usuariosRepository.findByIdOrNull(mensaje.usuarioId)?.let {
            var idRepetido = mensajesRepository.findByIdOrNull(mensaje.id)
            var retorno = "Success"
            while (idRepetido != null){
                mensaje.id++
                idRepetido = mensajesRepository.findByIdOrNull(mensaje.id)
                retorno = "El id del mensaje esta repetido, se le ha asignado el id:"+mensaje.id
            }
            mensajesRepository.save(mensaje)
            return retorno
        }
        return Error(2, "Usuario inexistente")
    }

    @GetMapping("descargarMensajes")
    fun descargarMensajes(): Any {
        val objetoList = List(mutableListOf())
        mensajesRepository.findAll().forEach {
            objetoList.list.add(it)
        }
        return objetoList
    }

    @GetMapping("descargarMensajesFiltrados")
    fun descargarMensajesFiltrados(@RequestBody text: String): Any {
        val objetoList = List(mutableListOf())
        mensajesRepository.findAll().forEach {
            if (it.texto.contains(text)) {
                objetoList.list.add(it)
            }
        }
        return objetoList
    }

    @GetMapping("obtenerMensajesYLlaves")
    fun obtenerMensajesYLlaves(@RequestBody admin: Admin): Any {
        return if (iniciarSesionAdmin(admin) == true){
            listaMensajeLlave()
        } else iniciarSesionAdmin(admin)
    }

    @GetMapping("obtenerMensajesDescifrados")
    fun obtenerMensajesDescifrados(@RequestBody admin: Admin): Any {
        return if (iniciarSesionAdmin(admin) == true){
            listaTextoDescifrado()
        } else iniciarSesionAdmin(admin)
    }

    fun comprobarContrasenaUser(usuarioBaseDeDatos: Usuario, usuarioIntroducido: Usuario): Boolean {
        return usuarioBaseDeDatos.pass == usuarioIntroducido.pass
    }

    fun comprobarContrasenaAdmin(adminBaseDeDatos: Admin, adminIntroducido: Admin): Boolean {
        return adminBaseDeDatos.pass == adminIntroducido.pass
    }

    fun listaTextoDescifrado(): List {
        val objetoList = List(mutableListOf())
        mensajesRepository.findAll().forEach {
            val mensaje = it.copy()
            try {
                mensaje.texto = descifrar(it.texto, usuariosRepository.getById(it.usuarioId).claveCifrado)
            } catch (e: Exception) {
                mensaje.texto = "Texto indescifrable"
            }
            objetoList.list.add(mensaje)
        }
        return objetoList
    }

    fun listaMensajeLlave(): Lista {
        val objetoLista = Lista(mutableListOf())
        var clave: String
        mensajesRepository.findAll().forEach { mensaje ->
            clave = usuariosRepository.getById(mensaje.usuarioId).claveCifrado
            objetoLista.lista.add(MensajeClave(mensaje, clave))
        }
        return objetoLista
    }

    fun iniciarSesionAdmin(admin : Admin) : Any{
        val adminBaseDeDatos = adminsRepository.findByIdOrNull(admin.nombre)
        adminBaseDeDatos?.let {
            if (comprobarContrasenaAdmin(adminBaseDeDatos, admin)) {
                return true
            }
            return Error(3, "Pass de administrador incorrecta")
        }
        return Error(3, "Nombre de administrador incorrecto")
    }

    @Throws(BadPaddingException::class)
    private fun descifrar(textoCifradoYEncodado: String, llaveEnString: String): String {
        val type = "AES/ECB/PKCS5Padding"
        println("Voy a descifrar $textoCifradoYEncodado")
        val cipher = Cipher.getInstance(type)
        cipher.init(Cipher.DECRYPT_MODE, getKey(llaveEnString))
        val textCifradoYDencodado = Base64.getUrlDecoder().decode(textoCifradoYEncodado)
        println("Texto cifrado $textCifradoYDencodado")
        val textDescifradoYDesencodado = String(cipher.doFinal(textCifradoYDencodado))
        println("Texto cifrado y desencodado $textDescifradoYDesencodado")
        return textDescifradoYDesencodado
    }

    private fun getKey(llaveEnString: String): SecretKeySpec {
        var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        llaveUtf8 = sha.digest(llaveUtf8)
        llaveUtf8 = llaveUtf8.copyOf(16)
        return SecretKeySpec(llaveUtf8, "AES")
    }
}
