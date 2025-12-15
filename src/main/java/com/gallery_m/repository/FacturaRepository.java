package com.gallery_m.repository;

import com.gallery_m.domain.Factura;
import com.gallery_m.domain.EstadoFactura;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    
    /**
     * Consulta optimizada para cargar la Factura con todas sus relaciones
     * (Usuario, Ventas y Zapatos) en una sola query usando FETCH JOIN
     */
    @Query("SELECT f FROM Factura f " +
           "LEFT JOIN FETCH f.usuario u " +       // Carga inmediata del Usuario
           "LEFT JOIN FETCH f.ventas v " +        // Carga inmediata de las Ventas
           "LEFT JOIN FETCH v.zapato z " +        // Carga inmediata de los Zapatos
           "WHERE f.idFactura = :idFactura")
    Optional<Factura> findByIdFacturaConDetalle(@Param("idFactura") Integer idFactura);
    
    /**
     * Obtiene todas las facturas de un usuario específico
     */
    List<Factura> findByUsuarioIdUsuario(Integer idUsuario);
    
    /**
     * Obtiene facturas por estado
     */
    List<Factura> findByEstado(EstadoFactura estado);
    
    /**
     * Obtiene facturas de un usuario ordenadas por fecha descendente
     */
    @Query("SELECT f FROM Factura f " +
           "WHERE f.usuario.idUsuario = :idUsuario " +
           "ORDER BY f.fecha DESC")
    List<Factura> findByUsuarioIdUsuarioOrderByFechaDesc(@Param("idUsuario") Integer idUsuario);
    
    /**
     * Cuenta el número de facturas por estado
     */
    long countByEstado(EstadoFactura estado);
}