package com.gallery_m.service;

import com.gallery_m.repository.UsuarioRepository;
import com.gallery_m.domain.Usuario;
import com.gallery_m.domain.UsuarioRol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("\n==========================================");
        System.out.println("🔍 LOAD USER BY USERNAME LLAMADO");
        System.out.println("   Username recibido: '" + username + "'");
        System.out.println("==========================================");

        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            System.out.println("❌ ERROR: Usuario NO encontrado en BD");
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        System.out.println("✅ Usuario encontrado en BD:");
        System.out.println("   - ID: " + usuario.getIdUsuario());
        System.out.println("   - Username: " + usuario.getUsername());
        System.out.println("   - Nombre: " + usuario.getNombre());
        System.out.println("   - Activo: " + usuario.isActivo());
        System.out.println("   - Password (primeros 30 chars): " + usuario.getPassword().substring(0, Math.min(30, usuario.getPassword().length())));

        // Verificar formato del hash
        BCryptPasswordEncoder testEncoder = new BCryptPasswordEncoder();
        String passwordHash = usuario.getPassword();
        boolean formatoValido = passwordHash.startsWith("$2a$")
                || passwordHash.startsWith("$2b$")
                || passwordHash.startsWith("$2y$");
        System.out.println("   - Formato BCrypt válido: " + formatoValido);
        System.out.println("   - Longitud del hash: " + passwordHash.length());

        if (!usuario.isActivo()) {
            System.out.println("❌ ERROR: Usuario existe pero está INACTIVO");
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        System.out.println("   - Cargando roles:");
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            for (UsuarioRol usuarioRol : usuario.getRoles()) {
                String rolNombre = usuarioRol.getRol().getRol();
                System.out.println("     * " + rolNombre);
                authorities.add(new SimpleGrantedAuthority(rolNombre));
            }
        } else {
            System.out.println("     ⚠️ ADVERTENCIA: NO hay roles asignados");
        }

        System.out.println("\n✅ Creando UserDetails...");
        UserDetails userDetails = new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );

        System.out.println("✅ UserDetails creado exitosamente");
        System.out.println("   - Username en UserDetails: " + userDetails.getUsername());
        System.out.println("   - Password en UserDetails: " + userDetails.getPassword().substring(0, 30) + "...");
        System.out.println("   - Authorities: " + userDetails.getAuthorities());
        System.out.println("   - Enabled: " + userDetails.isEnabled());
        System.out.println("==========================================\n");

        return userDetails;
    }
}
