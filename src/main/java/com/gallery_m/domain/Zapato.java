package com.gallery_m.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "zapato")
public class Zapato implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idZapato;
    
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "id_marca")
    private Marca marca;
    
    @Column(name = "nombre_zapato", unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String nombreZapato; 
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(precision = 12, scale = 2)
    @NotNull(message = "El precio no puede estar vacío")
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precio;
    
    @Column(name = "ruta_imagen", length = 1024)
    @Size(max = 1024)
    private String rutaImagen;
    
    private boolean activo;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}