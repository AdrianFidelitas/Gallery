
package com.gallery_m.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;


/*Esta clase solo va a tener datos dice este Data*/
@Data
/*Para decitr qu tiene entidades*/
@Entity
/*Para especificar el nombre de la tabla*/
@Table (name = "marca")
public class Marca implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /*Definir cual es la primary Key de la tabla*/
    @Id
    
    /*Para definir que el ID va a ser generado automaticamente por la BD ya que en el SQL tiene
    un auto incremental
    */
    @GeneratedValue (strategy = GenerationType.IDENTITY)   
    private Integer idMarca;
    
    
    //Caracteristicas del atributo
    @Column(unique = true,nullable = false, length =50)
    @NotNull
    @Size(max = 50)
    private String nombreMarca;

}
