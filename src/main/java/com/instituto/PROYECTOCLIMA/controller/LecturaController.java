package com.instituto.PROYECTOCLIMA.controller;

import com.instituto.PROYECTOCLIMA.model.Lectura;
import com.instituto.PROYECTOCLIMA.service.LecturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;


@RestController
@RequestMapping("/api/lecturas")
public class LecturaController {

    private final LecturaService lecturaService;

    public LecturaController(LecturaService lecturaService) {
        this.lecturaService = lecturaService;
    }

    @PostMapping
    public ResponseEntity<Lectura> crearLectura(@RequestBody Lectura lectura){

        try {
            return ResponseEntity.ok(lecturaService.createLectura(lectura));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Lectura>>listarLecturas(){

        try {
            return ResponseEntity.ok(lecturaService.readAllLectura());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/paginado")
    public ResponseEntity<Page<Lectura>> listarLecturasPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño) {

        try {
            Page<Lectura> lecturas = lecturaService.readAllLecturaPaginado(pagina, tamaño);
            return ResponseEntity.ok(lecturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // NUEVO: Endpoint paginado con ordenamiento
    @GetMapping("/paginado-ordenado")
    public ResponseEntity<Page<Lectura>> listarLecturasPaginadoYOrdenado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion) {

        try {
            Page<Lectura> lecturas = lecturaService.readAllLecturaPaginadoYOrdenado(
                    pagina, tamaño, ordenarPor, direccion);
            return ResponseEntity.ok(lecturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
