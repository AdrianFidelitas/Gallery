package com.gallery_m.service;

import com.gallery_m.domain.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    // Método para obtener todos los usuarios
    List<Usuario> getUsuarios();
    
    // Método para obtener un usuario por ID
    Optional<Usuario> getUsuario(Long id);
    
    // Método para obtener un usuario por USERNAME (necesario para el carrito)
    Optional<Usuario> getUsuarioPorUsername(String username);
    
    // Método para guardar un usuario
    Usuario save(Usuario usuario);
    
    // Método para eliminar un usuario
    void delete(Long id);
    
    // Método para verificar si un username ya existe
    boolean existeUsername(String username);
    
    // Método para verificar si un correo ya existe
    boolean existeCorreo(String correo);
}