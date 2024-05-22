package com.aluracursos.screenmatch.controller;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")//url base
public class SerieController {

    @Autowired
    private SerieService servicio;

    @GetMapping()
    public List<SerieDTO> obtenerTodasSeries(){
        return servicio.obtenerTodasSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obtenerTop5() {
        return servicio.obtenerTop5();
    }

    @GetMapping("/lanzamientos")
    public List<SerieDTO> obtenerLanzamientos() {
        return servicio.obtenerLanzamientos();
    }

    @GetMapping("/{id}")
    public SerieDTO obtenerPorId(@PathVariable Long id) {
        return servicio.obtenerPorID(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO>obtenerTodasTemporadas(@PathVariable Long id){
        return servicio.obtenerTodasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{temporada}")
    public List<EpisodioDTO>obtenerTemporada(@PathVariable Long id, @PathVariable Long temporada){
        return servicio.obtenertemporada(id,temporada);
    }

    @GetMapping("/categoria/{genero}")
    public List<SerieDTO>obtenerSerieCategoria(@PathVariable String genero){
        return servicio.obtenerSerieCategoria(genero);
    }

    
}
