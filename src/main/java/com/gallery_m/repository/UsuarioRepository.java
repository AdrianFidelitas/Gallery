package com.gallery_m.repository;

import com.gallery_m.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Método para buscar usuario por username (usado en la autenticación)
    Usuario findByUsername(String username);
    
    // Método para buscar usuario por correo (útil para validaciones)
    Usuario findByCorreo(String correo);
    
    // Verificar si existe un usuario con ese username
    boolean existsByUsername(String username);
    
    // Verificar si existe un usuario con ese correo
    boolean existsByCorreo(String correo);
}