package com.gallery_m.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

//Esta clase solo va a tener datos dice este Data
@Data

//Para decir que tiene entidades
@Entity

//Para especificar el nombre de la tabla
@Table(name = "categoria")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    //*Definir cual es la primary Key de la tabla
    @Id

    /*Para definir que el ID va a ser generado automaticamente por la BD ya que en el SQL tiene
    un auto incremental
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    /*Agregar las caracteristicas que tiene la tabla, como que es el nombre, not null y length del
    atributo descripcion, debe de ir antes del atributo*/
    @Column(name = "nombre_categoria", unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String nombreCategoria;

    /*Agregar las caracteristicas que tiene la tabla, como que es unica la rutaImagen, not null y length del
    atributo descripcion, debe de ir antes del atributo*/
    @Column(name = "ruta_imagen", length = 1024)
    @Size(max = 1024)
    private String rutaImagen;

    //Atributo Activo
    @Column(name = "activo")
    private boolean activo;

    //Anotacion que es una relacion con la tabla zapato, de uno a muchos
    @OneToMany(mappedBy = "categoria")

    //Listado para la lista de zapatos, atributo que guarda tipo zapato
    private List<Zapato> zapatos;

}
