package com.instituto.PROYECTOCLIMA.service;

import com.instituto.PROYECTOCLIMA.model.Lectura;
import com.instituto.PROYECTOCLIMA.repository.LecturaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LecturaService {

    private final LecturaRepository lecturaRepository ;

    public LecturaService(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }


    public Lectura createLectura(Lectura lectura) {
        try {
            return lecturaRepository.save(lectura);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear lectura: " + e.getMessage());
        }
    }

    public List<Lectura> readAllLectura() {
        try {
            return lecturaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener lecturas: " + e.getMessage());
        }
    }

    public Page<Lectura> readAllLecturaPaginado(int pagina, int tamaño) {
        try {
            Pageable pageable = PageRequest.of(pagina, tamaño);
            return lecturaRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener lecturas paginadas: " + e.getMessage());
        }
    }

    // NUEVO: Método con paginación y ordenamiento
    public Page<Lectura> readAllLecturaPaginadoYOrdenado(int pagina, int tamaño, String campoOrden, String direccion) {
        try {
            Sort sort = direccion.equalsIgnoreCase("desc") ?
                    Sort.by(campoOrden).descending() :
                    Sort.by(campoOrden).ascending();

            Pageable pageable = PageRequest.of(pagina, tamaño, sort);
            return lecturaRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener lecturas paginadas y ordenadas: " + e.getMessage());
        }
    }

}
