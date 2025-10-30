
package com.gallery_m.repository;

import com.gallery_m.domain.Zapato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ZapatoRepository extends JpaRepository<Zapato,Integer> {

    /*Esta instruccion trae todos los registros de la tabla zapato que
    esten activos / Consulta derivada*/
    public List<Zapato> findByActivoTrue();
}
