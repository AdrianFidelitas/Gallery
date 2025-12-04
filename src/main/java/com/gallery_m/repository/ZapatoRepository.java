package com.gallery_m.repository;

import com.gallery_m.domain.Zapato;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZapatoRepository extends JpaRepository<Zapato, Integer> {
    
    
    // Métodos básicos
    //Este para buscar por activo 
    List<Zapato> findByActivoTrue();
    //Metodo para buscar los que no estan activos
    List<Zapato> findByActivoFalse();
    //Metodo para contar los activos
    long countByActivoTrue();
    
    // Métodos por categoría
    //Metodo para buscar por categoria ID
    List<Zapato> findByCategoriaIdCategoria(Integer idCategoria);
    //Metodo para buscar por ID categoria y que este activo
    List<Zapato> findByCategoriaIdCategoriaAndActivoTrue(Integer idCategoria); 
    
    //Metodo para buscar por ID Categoria y que este inactivo
    List<Zapato> findByCategoriaIdCategoriaAndActivoFalse(Integer idCategoria); 
    
    
    // Métodos por marca
    //Metodo para buscar por ID marca
    List<Zapato> findByMarcaIdMarca(Integer idMarca);
    //Metodo para gucar por ID marca y que este activo
    List<Zapato> findByMarcaIdMarcaAndActivoTrue(Integer idMarca); 
    
    //Metodo para buscar por ID Marca y que este inactivo
    List<Zapato> findByMarcaIdMarcaAndActivoFalse(Integer idMarca); 
    
    // Método para verificar existencia por nombre, por query
    @Query("SELECT COUNT(z) > 0 FROM Zapato z WHERE z.nombreZapato = :nombreZapato")
    boolean existsByNombreZapato(@Param("nombreZapato") String nombreZapato);
    
    // Búsqueda por nombre, por query
    @Query("SELECT z FROM Zapato z WHERE LOWER(z.nombreZapato) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Zapato> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    //Busqueda por nombre, que ignorar si esta mayuscula, minuscula o asi
    @Query("SELECT z FROM Zapato z WHERE LOWER(z.nombreZapato) LIKE LOWER(CONCAT('%', :nombre, '%')) AND z.activo = true")
    List<Zapato> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    // Consulta con relaciones
    @Query("SELECT z FROM Zapato z LEFT JOIN FETCH z.categoria LEFT JOIN FETCH z.marca WHERE z.idZapato = :id")
    Optional<Zapato> findByIdWithRelations(@Param("id") Integer idZapato);
    
    
    
    // Métodos por talla
    
    //Metodo para buscar por talla
    List<Zapato> findByTalla(String talla);
    //Metodo para guscar por talla activa
    List<Zapato> findByTallaAndActivoTrue(String talla);
    
    // Métodos por existencias
    //Meotod para buscas por existencias mas que digamos
    List<Zapato> findByExistenciasGreaterThan(Integer existencias);
    //Para buscar zapato menos que 
    List<Zapato> findByExistenciasLessThan(Integer existencias);
    
    // Verificar unicidad nombre + talla
    @Query("SELECT COUNT(z) > 0 FROM Zapato z WHERE z.nombreZapato = :nombre AND z.talla = :talla")
    boolean existsByNombreZapatoAndTalla(@Param("nombre") String nombreZapato, 
                                        @Param("talla") String talla);
    
    // Para excluir el ID actual al actualizar
    @Query("SELECT COUNT(z) > 0 FROM Zapato z WHERE z.nombreZapato = :nombre AND z.talla = :talla AND z.idZapato <> :idExcluir")
    boolean existsByNombreAndTallaExcluyendoId(@Param("nombre") String nombre,
                                              @Param("talla") String talla,
                                              @Param("idExcluir") Integer idExcluir);
    
    // Buscar Zapatos agotados
    @Query("SELECT z FROM Zapato z WHERE z.existencias <= 0 AND z.activo = true")
    List<Zapato> findAgotados();
    
    // Buscar apatos con bajo inventario
    @Query("SELECT z FROM Zapato z WHERE z.existencias > 0 AND z.existencias < 10 AND z.activo = true")
    List<Zapato> findConBajoInventario();
}