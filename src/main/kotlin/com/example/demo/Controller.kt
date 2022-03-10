package com.example.demo

import net.minidev.json.JSONObject
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import java.security.MessageDigest
import javax.crypto.BadPaddingException

@RestController
class Controller(
    val usuariosRepository: UsuariosRepository,
    val mensajesRepository: MensajesRepository,
    val adminsRepository: AdminsRepository
) {

    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody usuario: Usuario): Any {
        vaciarLista()
        usuariosRepository.findAll().forEach {
            if (it.nombre == usuario.nombre) {
                if (it.pass == usuario.pass) {
                    return it.claveCifrado
                } else {
                    return Error(1, "Pass invalida")
                }
            }
        }
        usuariosRepository.save(usuario)
        return usuario.claveCifrado
    }


    @PostMapping("crearMensaje")
    fun crearMensaje(@RequestBody mensaje: Mensaje): Any {
        vaciarLista()
        usuariosRepository.findAll().forEach {
            if (it.nombre == mensaje.usuarioId) {
                mensajesRepository.save(mensaje)
                return "Success"
            }
        }
        return Error(2, "Usuario inexistente")
    }

    @GetMapping("descargarMensajes")
    fun descargarMensajes(): List.Companion {
        vaciarLista()
        mensajesRepository.findAll().forEach {
            List.list.add(it)
        }
        return List
    }

    @GetMapping("descargarMensajesFiltrados")
    fun descargarMensajesFiltrados(@RequestBody text: String): Any {
        vaciarLista()
        mensajesRepository.findAll().forEach {
            if (it.texto.contains(text, ignoreCase = true)) {
                List.list.add(it)
            }
        }
        return listOf(List)
    }

    @GetMapping("obtenerMensajesYLlaves")
    fun obtenerMensajesYLlaves(@RequestBody admin: Admin): Any {
        vaciarLista()
        var clave : String
        adminsRepository.findAll().forEach { admin1 ->
            if (admin1.nombre == admin.nombre) {
                if (admin1.pass == admin.pass) {
                    mensajesRepository.findAll().forEach { mensaje ->
                        clave = usuariosRepository.getById(saberUsuario(mensaje.usuarioId)).claveCifrado
                        Lista.lista.add(MensajeClave(mensaje, clave))
                    }
                    return listOf(Lista)
                }
            }
        }
        return listOf(Error(3, "Pass de administrador incorrecta"))
    }

    @GetMapping("obtenerMensajesDescifrados")
    fun obtenerMensajesDescifrados(@RequestBody admin: Admin): Any {
        vaciarLista()
        adminsRepository.findAll().forEach { admin1 ->
            if (admin1.nombre == admin.nombre) {
                if (admin1.pass == admin.pass) {
                    mensajesRepository.findAll().forEach {
                        val mensaje = it
                        var textoDescifrado: String = try {
                            descifrar(it.texto, usuariosRepository.getById(saberUsuario(it.usuarioId)).claveCifrado)
                        } catch (e: Exception) {
                            "Texto indescifrable"
                        }
                        mensaje.texto = textoDescifrado
                        List.list.add(mensaje)
                    }
                    return listOf(List)

                }
            }
        }
        return listOf(Error(3, "Pass de administrador incorrecta"))
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


    fun vaciarLista() {
        Lista.lista.clear()
        List.list.clear()
    }


    fun saberUsuario(nombre: String): Int {
        usuariosRepository.findAll().forEach {
            if (it.nombre == nombre)
                return it.id
        }
        return 0
    }
}
