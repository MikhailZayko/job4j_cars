package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PostRepository {

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
        return crudRepository.tx(session -> {
            List<Post> posts = session.createQuery("""
                            SELECT DISTINCT p
                            FROM Post p""", Post.class)
                    .list();
            return loadCollections(session, posts);
        });
    }

    public List<Post> findAllWithUsers() {
        return crudRepository.tx(session -> {
            List<Post> posts = session.createQuery("""
                            SELECT DISTINCT p
                            FROM Post p
                            JOIN FETCH p.user""", Post.class)
                    .list();
            return loadCollections(session, posts);
        });
    }

    public Optional<Post> findById(int id) {
        return crudRepository.tx(session -> {
            Optional<Post> postOpt = session.createQuery("""
                            SELECT p
                            FROM Post p
                            LEFT JOIN FETCH p.user
                            LEFT JOIN FETCH p.car
                            WHERE p.id = :postId""", Post.class)
                    .setParameter("postId", id)
                    .uniqueResultOptional();
            if (postOpt.isEmpty()) {
                return Optional.empty();
            }
            Post post = postOpt.get();
            initializeCollection(session, post, "priceHistories");
            initializeCollection(session, post, "subscribers");
            initializeCollection(session, post, "photos");
            return Optional.of(post);
        });
    }

    public List<Post> findPostsFromLastDay() {
        return crudRepository.tx(session -> {
            List<Post> posts = session.createQuery("""
                            SELECT DISTINCT p
                            FROM Post p
                            WHERE p.created >= :yesterday""", Post.class)
                    .setParameter("yesterday", LocalDateTime.now().minusDays(1))
                    .list();
            return loadCollections(session, posts);
        });
    }

    public List<Post> findPostsWithPhotos() {
        return crudRepository.tx(session -> {
            List<Post> posts = session.createQuery("""
                            SELECT DISTINCT p
                            FROM Post p
                            WHERE SIZE(p.photos) > 0""", Post.class)
                    .list();
            return loadCollections(session, posts);
        });
    }

    public List<Post> findPostsByCarBrand(String brand) {
        return crudRepository.tx(session -> {
            List<Post> posts = session.createQuery("""
                            SELECT DISTINCT p
                            FROM Post p
                            JOIN p.car c
                            WHERE c.name = :brand""", Post.class)
                    .setParameter("brand", brand)
                    .list();
            return loadCollections(session, posts);
        });
    }

    private List<Post> loadCollections(Session session, List<Post> posts) {
        if (posts.isEmpty()) {
            return posts;
        }
        posts = session.createQuery("""
                        SELECT DISTINCT p
                        FROM Post p
                        LEFT JOIN FETCH p.priceHistories
                        WHERE p IN :posts""", Post.class)
                .setParameter("posts", posts)
                .list();
        posts = session.createQuery("""
                        SELECT DISTINCT p
                        FROM Post p
                        LEFT JOIN FETCH p.subscribers
                        WHERE p IN :posts""", Post.class)
                .setParameter("posts", posts)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .list();
        return session.createQuery("""
                        SELECT DISTINCT p
                        FROM Post p
                        LEFT JOIN FETCH p.photos
                        WHERE p IN :posts""", Post.class)
                .setParameter("posts", posts)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .list();
    }

    private void initializeCollection(Session session,
                                      Post post,
                                      String collectionName) {
        session.createQuery("""
                        SELECT DISTINCT p
                        FROM Post p
                        LEFT JOIN FETCH p.%s
                        WHERE p = :post
                        """.formatted(collectionName), Post.class)
                .setParameter("post", post)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .uniqueResult();
    }
}
