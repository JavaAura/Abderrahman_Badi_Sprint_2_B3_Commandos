package repository.implementation;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.User;
import repository.interfaces.UserRepository;
import util.PersistenceUtil;

public class UserRepositoryImpl implements UserRepository {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	private static final int ITEMS_PER_PAGE = 5;

	private static final String LOGIN = "FROM User WHERE email = :email";

	@Override
	public Optional<User> login(String email, String password) {

		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		User user = null;

		try {
			transaction.begin();

			user = entityManager.createQuery(LOGIN, User.class).setParameter("email", email).getSingleResult();

			transaction.commit();
		} catch (Exception e) {

			if (transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Login failed for email: " + email, e);
		} finally {
			entityManager.close();
		}

		if (user != null && verifyPassword(password, user.getPassword())) {
			return Optional.of(user); // Successful login
		}
		return Optional.empty(); // Login failed
	}

	// Method to verify the password
	private boolean verifyPassword(String password, String hashedPassword) {
		return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
	}

	// Method to hash the password (for registration or password update)
	public String hashPassword(String password) {
		return BCrypt.withDefaults().hashToString(12, password.toCharArray());
	}

	@Override
	public Optional<User> get(long id) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		try {
			return Optional.ofNullable(entityManager.find(User.class, id));
		} finally {
			entityManager.close();
		}
	}

	@Override
	public List<User> getAll(int pageNumber, int accessLevel) {
		int startIndex = (pageNumber - 1) * ITEMS_PER_PAGE;

		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		List<User> users = null;

		try {
			TypedQuery<User> typedQuery;

			if (accessLevel == 1) {
                typedQuery = entityManager
                        .createQuery(
                                "SELECT u FROM User u WHERE u.isDeleted = false AND TYPE(u) != Admin OR (TYPE(u) = Admin AND u.levelAccess != 1) ORDER BY u.id ASC",
                                User.class);
            } else {
                typedQuery = entityManager
                        .createQuery(
                                "SELECT c FROM Client c WHERE c.isDeleted = false ORDER BY c.id",
                                User.class);
            }

			typedQuery.setFirstResult(startIndex);
			typedQuery.setMaxResults(ITEMS_PER_PAGE);

			users = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error("Error listing users: ", e);
		} finally {
			entityManager.close();
		}

		return users;
	}

	@Override
	public int getTotalPageNumber(int accessLevel){
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> countQuery;
            if (accessLevel == 1) {
                countQuery = entityManager
                        .createQuery(
                                "SELECT COUNT(u) FROM User u WHERE u.isDeleted = false AND TYPE(u) != Admin OR (TYPE(u) = Admin AND u.levelAccess != 1) ",
                                Long.class);
            } else {
                countQuery = entityManager
                        .createQuery(
                                "SELECT COUNT(c) FROM Client c WHERE c.isDeleted = false ",
                                Long.class);
            }

            long totalUsers = countQuery.getSingleResult();
            int totalPages = (int) Math.ceil((double) totalUsers / ITEMS_PER_PAGE);

            return totalPages == 0 ? 1 : totalPages;

        } finally {
            entityManager.close();
        }
	}

	@Override
	public void save(User user) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();
			entityManager.persist(user);
			transaction.commit();
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Error creating user: ", e);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public void update(User user) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();
			entityManager.merge(user);
			transaction.commit();
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Error updating user: ", e);
		} finally {
			entityManager.close();
		}
	}

	@Override
	public void delete(long id) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();
			User user = entityManager.find(User.class, id);
			if (user != null) {
				user.setIsDeleted(true);
				entityManager.merge(user);
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Error deleting user: ", e);
		} finally {
			entityManager.close();
		}
	}
}
