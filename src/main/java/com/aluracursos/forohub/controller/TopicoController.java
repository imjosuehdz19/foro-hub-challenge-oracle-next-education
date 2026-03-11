package com.aluracursos.forohub.controller;

import com.aluracursos.forohub.domain.topico.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository repository;

    @Transactional
    @PostMapping
    public ResponseEntity registrarTopico(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriComponentsBuilder){
        var yaExiste = repository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje());

        if (yaExiste){
            return ResponseEntity.badRequest().body("Ya existe un tópico con el mismo título y mensaje.");
        }

        var topico = new Topico(datos);
        repository.save(topico);
        var uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleTopico(topico));
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listar(@PageableDefault(size=10, sort={"titulo"}) Pageable paginacion){
        var page = repository.findAllByActivoTrue(paginacion).map(DatosListadoTopico::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity deatllar(@PathVariable Long id){
        if(!repository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El tópico con el ID suministrado no existe.");
        }

        var topico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

//    @Transactional
//    @PutMapping("/{id}")
//    public ResponseEntity actualizar(@PathVariable Long id, @RequestBody @Valid DatosActualizarTopico datos){
//        var optionalTopico = repository.findById(id);
//        if (optionalTopico.isEmpty()){ // Usamos isEmpty() que es más moderno que !isPresent()
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El tópico no existe");
//        }
//
//        var topico = optionalTopico.get();
//
//        var tituloABuscar = datos.titulo() != null ? datos.titulo() : topico.getTitulo();
//        var mensajeABuscar = datos.mensaje() != null ? datos.mensaje() : topico.getMensaje();
//
//        var yaExiste = repository.existsByTituloAndMensaje(tituloABuscar, mensajeABuscar);
//
//        if (yaExiste) {
//            return ResponseEntity.badRequest().body("Ya existe un tópico con el mismo título y mensaje.");
//        }
//
//        topico.actualizarDatos(datos);
//        return ResponseEntity.ok(new DatosDetalleTopico(topico));
//    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity actualizar(@PathVariable Long id, @RequestBody @Valid DatosActualizarTopico datos){
        var topico = repository.getReferenceById(id); // Más eficiente que findById si solo vas a actualizar

        topico.actualizarDatos(datos);
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }


    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminar(@PathVariable Long id){
        Topico topico = repository.getReferenceById(id);
        topico.desactivarTopico();

        return ResponseEntity.noContent().build();
    }
}
