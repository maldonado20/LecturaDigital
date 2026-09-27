package com.florisistem.backend.controller;

import com.florisistem.backend.model.Insumo;
import com.florisistem.backend.repository.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    @Autowired
    private InsumoRepository insumoRepository;

    // Esta ruta nos devolverá todos los insumos de la base de datos
    @GetMapping
    public List<Insumo> obtenerTodosLosInsumos() {
        return insumoRepository.findAll();
    }
}
