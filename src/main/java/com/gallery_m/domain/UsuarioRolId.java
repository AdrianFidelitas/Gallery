package com.gallery_m.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class UsuarioRolId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @Column(name = "id_rol")
    private Long idRol;
}