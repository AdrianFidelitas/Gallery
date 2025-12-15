package com.gallery_m.repository;

import com.gallery_m.domain.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    /**
     * Obtiene todas las ventas de una factura específica
     */
    List<Venta> findByFacturaIdFactura(Integer idFactura);
    
    /**
     * Obtiene todas las ventas de un zapato específico
     */
    List<Venta> findByZapatoIdZapato(Integer idZapato);
    
    /**
     * Obtiene las ventas con información completa (Factura + Zapato)
     */
    @Query("SELECT v FROM Venta v " +
           "LEFT JOIN FETCH v.factura f " +
           "LEFT JOIN FETCH v.zapato z " +
           "WHERE v.idVenta = :idVenta")
    Venta findByIdConRelaciones(@Param("idVenta") Integer idVenta);
}