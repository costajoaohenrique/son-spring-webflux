package com.son.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private Scheduler jdbScheduler;

    @Autowired
    private TodoRepository repository;


    @PostMapping
    public Mono<Todo> save(@RequestBody Todo todo) {
        return Mono.fromCallable(() -> 
            transactionTemplate.execute(action -> {
                Todo saveEntityTodo = repository.save(todo);
                return saveEntityTodo;
            })
        );
    }

    @GetMapping
    public Flux<Todo> findAll(@RequestBody Todo todo) {
        return Flux.defer(() -> Flux.fromIterable(this.repository.findAll()).subscribeOn(jdbScheduler));
    }

    @GetMapping("/{id}")
    public Mono<Todo> findById(@PathVariable Long id) {
        return Mono.justOrEmpty(this.repository.findById(id));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return Mono.fromCallable(() -> this.transactionTemplate.execute(action -> {
            this.repository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        })
        ).subscribeOn(jdbScheduler);
    }
}
