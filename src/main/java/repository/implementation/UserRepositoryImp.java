package repository.implementation;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.User;
import repository.interfaces.UserRepository;
import util.PersistenceUtil;

public class UserRepositoryImp implements UserRepository {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImp.class);
	private static final String LOGIN = "FROM User WHERE email = :email";

	private final SessionFactory sessionFactory;

	public UserRepositoryImp(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

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
}
