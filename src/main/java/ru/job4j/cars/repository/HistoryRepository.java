package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.History;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HistoryRepository {

    private final CrudRepository crudRepository;

    public List<History> findAll() {
        return crudRepository.query("FROM History", History.class);
    }

    public Optional<History> findById(int id) {
        return crudRepository.optional("FROM History h WHERE h.id = :fId", History.class,
                Map.of("fId", id));
    }
}
