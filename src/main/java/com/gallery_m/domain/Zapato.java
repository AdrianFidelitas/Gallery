
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
import lombok.Data;

/*Esta clase solo va a tener datos dice este Data*/
@Data
/*Para decitr qu tiene entidades*/
@Entity
/*Para especificar el nombre de la tabla*/
@Table (name = "zapato")

public class Zapato implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /*Definir cual es la primary Key de la tabla*/
    @Id
    
    /*Para definir que el ID va a ser generado automaticamente por la BD ya que en el SQL tiene
    un auto incremental
    */
    @GeneratedValue (strategy = GenerationType.IDENTITY)    
    private Integer idZapato;
    
    
    //Relaciones con las demas tablas
    //Con la tabla categoria
    @ManyToOne
    //Indica con que columna se une, esto permite la union (Llave foranea)
    @JoinColumn(name="id_categoria")
    private Categoria categoria;
    
    //Con la tabla marca
    @ManyToOne
    //Indica con que columna se une, esto permite la union (Llave foranea)
    @JoinColumn (name="id_marca")
    private Marca marca;
    
    /*Agregar las caracteristicas que tiene la tabla, como que es unica la descripcion, not null y length del
    atributo descripcion, debe de ir antes del atributo*/
    @Column(unique = true, nullable = false, length =50)
    @NotNull
    @Size(max = 50)
    private String NombreZapato;
    
    //Atributo tipo Texto
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column (precision = 12, scale=2) //Esto esque de 12 es el la cantidad de digitos que puede tener, y 2 decimales
    @NotNull (message = "El precio no puede esta vacio")//Que no sea null
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio debe ser mayor o igual a 0") //Mayor o igual que cero segun la tabla en mysql
    private BigDecimal precio;
    
    /*Agregar las caracteristicas que tiene la tabla, como que es unica la rutaImagen, not null y length del
    atributo descripcion, debe de ir antes del atributo*/
    @Column(length =1024)
    @Size(max = 1024)    
    private String rutaImagen;
    
    
    //Atributo Activo
    private boolean activo;

}
