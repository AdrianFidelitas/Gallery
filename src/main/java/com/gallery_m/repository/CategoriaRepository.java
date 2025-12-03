package com.gallery_m.repository;

import com.gallery_m.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Integer>{
    public List<Categoria> findByActivoTrue();
    
     // Método para buscar categorías por nombre (búsqueda parcial, case-insensitive)
    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Categoria> buscarPorNombre(@Param("texto") String texto);
    
    // Método alternativo combinando búsqueda con filtro de activos
    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :texto, '%')) AND (:activo = false OR c.activo = true)")
    List<Categoria> buscarPorNombreYActivo(@Param("texto") String texto, @Param("activo") boolean activo);
    
    
}
