package com.gallery_m.service;

import com.gallery_m.domain.Ruta;
import com.gallery_m.repository.RutaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    public List<Ruta> getRutas() {
        return rutaRepository.findAllByOrderByRequiereRolAsc();
    }

}
