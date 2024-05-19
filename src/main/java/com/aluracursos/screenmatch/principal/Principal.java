package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=c79e28ed";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \n\n1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar serie por Categoria
                    7 - Filtrar series por temporadas y Evaluacion
                    8 - Buscar episodios por titulo
                    9 - Top 5 mejores episodios por serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    filtrarSeriePorTemporadaEvalucion();
                    break;
                case 8:
                    buscarEpisodioPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
//        DatosSerie datosSerie = getDatosSerie();
//        List<DatosTemporadas> temporadas = new ArrayList<>();

//        for (int i = 1; i <= datosSerie.totalTemporadas(); i++) {
//            var json = consumoApi.obtenerDatos(URL_BASE + datosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
//            DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
//            temporadas.add(datosTemporada);
//        }
//        temporadas.forEach(System.out::println);

        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie que deseas ver los episodios: ");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo()
                        .replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(),e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
//        datosSeries.add(datos);
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {

        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);


//        List <Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//                .map(d->new Serie(d))
//                .collect(Collectors.toList());
//
//        series.stream()
//                .sorted(Comparator.comparing(Serie::getGenero))
//                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas ver: ");
        var nombreSerie = teclado.nextLine();

        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()) {
            System.out.println("La serie que desea buscar es: " + serieBuscada.get());
        }else {
            System.out.println("No se encontro el serie que desea buscar");
        }
    }

    private void buscarTop5Series(){
        List<Serie> topseries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topseries.forEach(s ->
                System.out.println("Titulo: " + s.getTitulo() + " Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Ingrese el genero/categoria de la serie que desea buscar: ");
        var genero = teclado.nextLine();

        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las Series de la Categoria " + genero);
        seriePorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriePorTemporadaEvalucion(){
        System.out.println("Filtrar series con cuantas temporadas: ");
        var temporada = teclado.nextInt();
        teclado.nextLine();
        System.out.println("con evaluacion apartir de cual valor: ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();

//        List<Serie> filtroSeries = repositorio
//                .findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(temporada,evaluacion);
//        filtroSeries.forEach(t -> System.out.println("Titulo: " +
//                t.getTitulo() + " Evaluacion: " + t.getEvaluacion()));

        List<Serie> filtroSeries = repositorio
                .seriesPorTemporadaYEvaluacion(temporada,evaluacion);
       filtroSeries.forEach(f -> System.out.println("Titulo: " +
               f.getTitulo() + " Evaluacion: " + f.getEvaluacion()));
    }

    private void buscarEpisodioPorTitulo(){
        System.out.println("Ingrese nombre del episodio a buscar: ");
        var nombreEpisodio = teclado.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodioPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s Episodio %s Evaliación %s",
                e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));

    }

    private void buscarTop5Episodios(){
        buscarSeriePorTitulo();

        if(serieBuscada.isPresent()) {
            var serie = serieBuscada.get();
            List<Episodio>topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s Temporada %s Episodio %s Evaliación %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));
        }
    }


}

