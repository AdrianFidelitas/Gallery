
package com.gallery_m.repository;

import com.gallery_m.domain.Marca;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer>{
    
    //Como no tiene activo o asi esta clase con FindAll esta bien
    public List<Marca> findAll();
}
