package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PostRepository {

    private static final String FROM = "SELECT DISTINCT p FROM Post p JOIN FETCH p.priceHistories JOIN FETCH p.subscribers ";

    private final CrudRepository crudRepository;

    public Optional<Post> create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return Optional.of(post);
    }

    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    public void delete(int id) {
        crudRepository.run("DELETE Post p WHERE p.id = :fId",
                Map.of("fId", id));
    }

    public List<Post> findAll() {
        return crudRepository.query(FROM, Post.class);
    }

    public Optional<Post> findById(int id) {
        return crudRepository.optional(FROM + "WHERE p.id = :fId", Post.class,
                Map.of("fId", id));
    }
}
