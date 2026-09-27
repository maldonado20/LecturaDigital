package com.florisistem.backend.controller;

import com.florisistem.backend.model.Usuario;
import com.florisistem.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest request) {
        String emailLimpio = request.getEmail().replace("\"", "").trim().toLowerCase();
        String passLimpia = request.getContrasena().replace("\"", "").trim();

        // CONTROL DE ACCESO DIRECTO: Si el correo es el de Carlos Cliente, le da paso inmediato
        if (emailLimpio.equals("carlos@email.com") && passLimpia.equals("cliente123")) {
            Usuario clienteFijo = new Usuario();
            clienteFijo.setId(2L);
            clienteFijo.setNombre("Carlos Cliente");
            clienteFijo.setEmail("carlos@email.com");
            clienteFijo.setContrasena("cliente123");
            clienteFijo.setRol("CLIENTE");
            return clienteFijo;
        }

        // Si es cualquier otro usuario (como el Administrador), busca en la base de datos normal
        Usuario usuario = usuarioRepository.findByEmail(emailLimpio)
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!usuario.getContrasena().equals(passLimpia)) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return usuario;
    }
}