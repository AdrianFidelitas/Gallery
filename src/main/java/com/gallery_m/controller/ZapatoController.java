package com.gallery_m.controller;

import com.gallery_m.domain.Zapato;
import com.gallery_m.service.CategoriaService;
import com.gallery_m.service.MarcaService;
import com.gallery_m.service.ZapatoService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/zapato")
public class ZapatoController {

    @Autowired
    private ZapatoService zapatoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private MessageSource messageSource;

   
    @GetMapping("/categoria/{idCategoria}")
    public String listarPorCategoria(@PathVariable Integer idCategoria, Model model) {
        try {

            var zapatos = zapatoService.getZapatosPorCategoria(idCategoria, false);


            model.addAttribute("zapatos", zapatos);
            model.addAttribute("totalZapatos", zapatos.size());
            model.addAttribute("categoriaSeleccionada", idCategoria);
            model.addAttribute("categorias", categoriaService.getCategorias(false));
            model.addAttribute("marcas", marcaService.getMarcas());

            return "zapato/listado";

        } catch (Exception e) {
            System.err.println("ERROR en /zapato/categoria/" + idCategoria);
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar zapatos: " + e.getMessage());
            return "redirect:/zapato/listado";
        }
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var zapatos = zapatoService.getZapatosActivos();

        zapatos.forEach(z -> System.out.println(
                "ID: " + z.getIdZapato()
                + ", Nombre: " + z.getNombreZapato()
                + ", Categoría: " + (z.getCategoria() != null ? z.getCategoria().getNombreCategoria() : "null")
        ));

        model.addAttribute("zapatos", zapatos);
        model.addAttribute("totalZapatos", zapatos.size());
        model.addAttribute("categorias", categoriaService.getCategorias(false));
        model.addAttribute("marcas", marcaService.getMarcas());

        return "zapato/listado";
    }

    @GetMapping("/detalle/{idZapato}")
    public String detalleZapato(@PathVariable Integer idZapato, Model model) {
        try {
            Optional<Zapato> zapatoOpt = zapatoService.getZapato(idZapato);

            if (zapatoOpt.isEmpty()) {
                return "redirect:/zapato/listado";
            }
            Zapato zapato = zapatoOpt.get();

            model.addAttribute("zapato", zapato);

            return "zapato/detalle";

        } catch (Exception e) {
            System.err.println("💥 ERROR en detalle: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/zapato/listado";
        }
    }

    @GetMapping("/test-detalle/{id}")
    @ResponseBody
    public String testDetalle(@PathVariable Integer id) {
        try {

            Optional<Zapato> zapatoOpt = zapatoService.getZapato(id);

            if (zapatoOpt.isPresent()) {
                Zapato z = zapatoOpt.get();
                return "✅ ZAPATO ENCONTRADO - Nombre: " + z.getNombreZapato();
            } else {
                return "❌ ZAPATO NO ENCONTRADO - ID: " + id;
            }
        } catch (Exception e) {
            return "❌ ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Zapato zapato,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        zapatoService.save(zapato, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/zapato/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idZapato, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            zapatoService.delete(idZapato);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "zapato.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "zapato.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "zapato.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/zapato/listado";
    }

    @GetMapping("/modificar/{idZapato}")
    public String modificar(@PathVariable Integer idZapato,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Zapato> zapatoOpt = zapatoService.getZapato(idZapato);
        if (zapatoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("zapato.error01", null, Locale.getDefault()));
            return "redirect:/zapato/listado";
        }
        model.addAttribute("zapato", zapatoOpt.get());

        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);

        var marcas = marcaService.getMarcas();
        model.addAttribute("marcas", marcas);

        return "/zapato/modifica";
    }

}
