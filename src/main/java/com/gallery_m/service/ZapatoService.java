
package com.gallery_m.service;



import com.gallery_m.domain.Zapato;
import com.gallery_m.repository.ZapatoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

//Anotacion que es un servicio
@Service
public class ZapatoService {
    
   /*Metodo que ocupa el repositorio, crea el objeto repository*/
    @Autowired
    private ZapatoRepository zapatoRepository;
    
    /*Este es un metodo para ver la tabla, registros, en una lista*/
    @Transactional(readOnly = true)
    public List<Zapato> getZapatos(boolean activo) {
        /*If si el estado es activo en esa columna*/
        if (activo) {
            return zapatoRepository.findByActivoTrue();
        }
        
        /*Si no devuelve todos*/
        return zapatoRepository.findAll();
        
    }
    
    @Transactional(readOnly = true)
    public Optional<Zapato> getZapato(Integer idZapato) {
        return zapatoRepository.findById(idZapato);
    }    
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    //Actualiza o inserta un registro y sube una imagen a la nube
    @Transactional
    public void save(Zapato zapato, MultipartFile imagenFile){
        zapato = zapatoRepository.save(zapato);
        if (!imagenFile.isEmpty()){ //Hay Imagen
            try {
            String rutaImagen = firebaseStorageService.uploadImage(
                    imagenFile,
                    "zapato",
                    zapato.getIdZapato());
            zapato.setRutaImagen(rutaImagen);
            zapatoRepository.save(zapato);
            } catch (Exception e) {
                
            }
        }
        
    }
    //Elimina un registro....
    @Transactional
    public void delete(Integer idZapato){
       //Se verifica si existe la producto
       if(!zapatoRepository.existsById(idZapato)){
         //Se lanza una excepcion
         throw new IllegalArgumentException("El Zapato" + idZapato + "No Existe");
       }
        try{
          zapatoRepository.deleteById(idZapato);
        } catch (DataIntegrityViolationException e){
           throw new IllegalStateException ("No se puede eliminar el zapato, por que tiene datos asociados");
        }
    }    
}
