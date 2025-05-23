package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
@Repository
public class UserRepository {

    private final SessionFactory sf;

    /**
     * Сохранить в базе.
     *
     * @param user пользователь.
     * @return пользователь с id.
     */
    public User create(User user) {
        transactionVoid(session -> session.save(user));
        return user;
    }

    /**
     * Обновить в базе пользователя.
     *
     * @param user пользователь.
     */
    public void update(User user) {
        transactionVoid(session -> session.createQuery(
                        "UPDATE User SET login = :fLogin, password = :fPassword WHERE id = :fId")
                .setParameter("fLogin", user.getLogin())
                .setParameter("fPassword", user.getPassword())
                .setParameter("fId", user.getId())
                .executeUpdate());
    }

    /**
     * Удалить пользователя по id.
     *
     * @param userId ID
     */
    public void delete(int userId) {
        transactionVoid(session -> session.createQuery(
                        "DELETE User WHERE id = :fId")
                .setParameter("fId", userId)
                .executeUpdate());
    }

    /**
     * Список пользователь отсортированных по id.
     *
     * @return список пользователей.
     */
    public List<User> findAllOrderById() {
        return transaction(session -> session.createQuery("FROM User", User.class).list());
    }

    /**
     * Найти пользователя по ID
     *
     * @return пользователь.
     */
    public Optional<User> findById(int userId) {
        return transaction(session -> session.createQuery("FROM User u WHERE u.id = :fId", User.class)
                .setParameter("fId", userId)
                .uniqueResultOptional());
    }

    /**
     * Список пользователей по login LIKE %key%
     *
     * @param key key
     * @return список пользователей.
     */
    public List<User> findByLikeLogin(String key) {
        return transaction(session -> session.createQuery("FROM User u WHERE u.login LIKE :fLogin", User.class)
                .setParameter("fLogin", "%" + key + "%")
                .list());
    }

    /**
     * Найти пользователя по login.
     *
     * @param login login.
     * @return Optional or user.
     */
    public Optional<User> findByLogin(String login) {
        return transaction(session -> session.createQuery("FROM User u WHERE u.login = :fLogin", User.class)
                .setParameter("fLogin", login)
                .uniqueResultOptional());
    }

    private <T> T transaction(Function<Session, T> command) {
        T result = null;
        Transaction transaction = null;
        try (Session session = sf.openSession()) {
            transaction = session.beginTransaction();
            result = command.apply(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return result;
    }

    private void transactionVoid(Consumer<Session> command) {
        Transaction transaction = null;
        try (Session session = sf.openSession()) {
            transaction = session.beginTransaction();
            command.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
