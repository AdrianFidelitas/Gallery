package com.gallery_m.controller;

import com.gallery_m.domain.Categoria;
import com.gallery_m.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

/*Esto dice, todos las rutas con /categoria la va a manejar esta clase*/
@RequestMapping("/categoria")
public class CategoriaController {

    /*Para enlazar categoria service con categoria controller*/
    @Autowired
    private CategoriaService categoriaService;

    /*Aca defini el metodo que va a atender la ruta categoria/listado*/
    @GetMapping("/listado")
    public String listado(Model model) {
        //Recupera en categorias en esta variable la raylist
        var categorias = categoriaService.getCategorias(false);
        //Importa o inyecta los datos a model
        model.addAttribute("categorias",categorias);
        model.addAttribute("totalCategorias",categorias.size());
        return "/categoria/listado";
    }
    @Autowired
    private MessageSource messageSource;
    @PostMapping("/guardar")
    public String guardar(@Valid /*Valida*/ Categoria categoria,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        categoriaService.save(categoria, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
        );
        return "redirect:/categoria/listado";
    }
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            categoriaService.delete(idCategoria);
        } catch (IllegalArgumentException e) {
            titulo = "error";  //Captura que no exista la categoria
            detalle = "categoria.error01";
        } catch (IllegalStateException e) {
            titulo = "error";  //Captura que no se puede eliminar por que tiene datos asociados
            detalle = "categoria.error02";
        } catch (Exception e) {
            titulo = "error";  //Ocurrio un error inesperado...
            detalle = "categoria.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null,
                Locale.getDefault()));
        return "redirect:/categoria/listado";
    }
   
    @GetMapping("/modificar/{idCategoria}")
    public String modificar (@PathVariable Integer idCategoria,
            Model model,
            RedirectAttributes redirectAttributes){
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria(idCategoria);
        if (categoriaOpt.isEmpty()){
          redirectAttributes.addFlashAttribute("error",
                  messageSource.getMessage("categoria.error01",null, Locale.getDefault()));
          return "redirect:/categoria/listado";
        }
        model.addAttribute("categoria",categoriaOpt.get());
        return "/categoria/modifica";
    }
    
}
