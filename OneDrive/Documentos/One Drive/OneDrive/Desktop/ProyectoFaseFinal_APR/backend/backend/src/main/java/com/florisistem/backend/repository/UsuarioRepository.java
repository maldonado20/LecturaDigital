package com.florisistem.backend.repository;

import com.florisistem.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto nos servirá para el inicio de sesión más adelante
    Optional<Usuario> findByEmail(String email);
}
