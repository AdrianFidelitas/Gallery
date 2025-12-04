package com.gallery_m.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
    @Column(name = "id_zapato") 
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
    
    //Existencias que va guardar cuantos habra 
    //Caracteristicas del los datos que va a tener la columna
    @NotNull (message = "Las existencias no pueden estar vacias")//Que no sea null
    @Min(value = 0, message = "Las existencias debe ser mayor o igual a 0") //Mayor o igual que cero segun la tabla en mysql
    private Integer existencias;
    
    
    //Talla
    @Column(name = "talla", unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String talla; 
    
    @Column(name = "ruta_imagen", length = 1024)
    @Size(max = 1024)
    private String rutaImagen;
    
    private boolean activo;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    //Metodos extras
    
    //Metodo si esta agotado
    public boolean isAgotado() {
        return existencias != null && existencias <= 0;
    }
    
    //Metodo si tiene bajo inventario o bueno pocos items
    public boolean tieneBajoInventario() {
        return existencias != null && existencias > 0 && existencias < 10;
    }
    
    //Metodo para obtener el nombre completo
    public String getNombreCompleto() {
        return nombreZapato + " (Talla: " + talla + ")";
    }
}