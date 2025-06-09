package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CarRepository {

    private static final String FROM = "SELECT DISTINCT c FROM Car c JOIN FETCH c.owners ";

    private final CrudRepository crudRepository;

    public List<Car> findAll() {
        return crudRepository.query(FROM, Car.class);
    }

    public Optional<Car> findById(int id) {
        return crudRepository.optional(FROM + "WHERE c.id = :fId", Car.class,
                Map.of("fId", id));
    }
}
