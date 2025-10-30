package com.gallery_m.service;

import com.gallery_m.domain.Marca;
import com.gallery_m.repository.MarcaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*Anotacion que es un servicio*/
@Service
public class MarcaService {

    /*Metodo que ocupa el repositorio, crea el objeto repository*/
    @Autowired
    private final MarcaRepository marcaRepository;

    //Contructor
    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    //Metodo getMarcas, para ver las marcas en modo lista
    @Transactional(readOnly = true)
    public List<Marca> getMarcas() {
        return marcaRepository.findAll();
    }

    //Metodo para buscar por idMarca
    @Transactional(readOnly = true)
    public Optional<Marca> getMarca(Integer idMarca) {
        return marcaRepository.findById(idMarca);
    }

    //Metodo para guardar
    @Transactional
    public void save(Marca marca) {
        marcaRepository.save(marca);
    }
    
    
    //Metodo para borrar
    @Transactional
    public void delete(Integer idMarca) {
        marcaRepository.deleteById(idMarca);
    }
}
