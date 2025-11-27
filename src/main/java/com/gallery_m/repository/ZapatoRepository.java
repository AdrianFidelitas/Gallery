package com.gallery_m.repository;

import com.gallery_m.domain.Zapato;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZapatoRepository extends JpaRepository<Zapato, Integer> {
    
    // Métodos básicos que SÍ funcionan
    List<Zapato> findByActivoTrue();
    List<Zapato> findByActivoFalse();
    long countByActivoTrue();
    
    // Métodos por categoría
    List<Zapato> findByCategoriaIdCategoria(Integer idCategoria);
    List<Zapato> findByCategoriaIdCategoriaAndActivo(Integer idCategoria, boolean activo);
    
    //  Métodos por marca
    List<Zapato> findByMarcaIdMarca(Integer idMarca);
    List<Zapato> findByMarcaIdMarcaAndActivo(Integer idMarca, boolean activo);
    
   
    @Query("SELECT COUNT(z) > 0 FROM Zapato z WHERE z.nombreZapato = :nombreZapato")
    boolean existsByNombreZapato(@Param("nombreZapato") String nombreZapato);
    
    //  Búsqueda por nombre con @Query
    @Query("SELECT z FROM Zapato z WHERE LOWER(z.nombreZapato) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Zapato> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    @Query("SELECT z FROM Zapato z WHERE LOWER(z.nombreZapato) LIKE LOWER(CONCAT('%', :nombre, '%')) AND z.activo = true")
    List<Zapato> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    //  Consulta personalizada para debugging
    @Query("SELECT z FROM Zapato z LEFT JOIN FETCH z.categoria LEFT JOIN FETCH z.marca WHERE z.idZapato = :id")
    Optional<Zapato> findByIdWithRelations(@Param("id") Integer idZapato);
}