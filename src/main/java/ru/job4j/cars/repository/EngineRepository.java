package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class EngineRepository {

    private final CrudRepository crudRepository;

    public List<Engine> findAll() {
        return crudRepository.query("FROM Engine", Engine.class);
    }

    public Optional<Engine> findById(int id) {
        return crudRepository.optional("FROM Engine e WHERE e.id = :fId", Engine.class,
                Map.of("fId", id));
    }
}
