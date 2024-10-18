package repository.interfaces;

import java.util.List;
import java.util.Optional;

import model.User;

public interface UserRepository {

	Optional<User> get(long id);

	List<User> getAll(int pageNumber, int accessLevel);

	int getTotalPageNumber(int accessLevel);

	void save(User user);

	void update(User user);

	void delete(long id);

	Optional<User> login(String email, String password);
}
