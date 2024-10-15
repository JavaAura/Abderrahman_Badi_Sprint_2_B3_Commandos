package repository.interfaces;

import java.util.Optional;

import model.User;

public interface UserRepository {
	Optional<User> login(String email, String password);
}
