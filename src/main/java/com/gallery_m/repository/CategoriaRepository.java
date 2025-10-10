package com.gallery_m.repository;

import com.gallery_m.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Integer>{
    public List<Categoria> findByActivoTrue();
}
