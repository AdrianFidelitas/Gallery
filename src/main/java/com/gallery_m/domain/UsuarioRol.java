package com.gallery_m.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
@Entity
@Table(name = "usuario_rol")
public class UsuarioRol implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private UsuarioRolId id;
    
    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @ManyToOne
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;
}