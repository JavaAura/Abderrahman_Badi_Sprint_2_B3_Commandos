package service;

import java.util.List;
import java.util.Optional;

import model.User;
import repository.interfaces.UserRepository;

public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Optional<User> getUser(long id) {
		return userRepository.get(id);
	}

	public List<User> getAllUsers(int pageNumber, int accessLevel) {
		return userRepository.getAll(pageNumber, accessLevel);
	}


	public int getTotalPageNumber(int accessLevel) {
		return userRepository.getTotalPageNumber(accessLevel);
	}

	public void addUser(User user) {
		userRepository.save(user);
	}

	public void updateUser(User user) {
		userRepository.update(user);
	}

	public void deleteUser(long id) {
		userRepository.delete(id);
	}

	public Optional<User> login(String email, String Password) {
		return userRepository.login(email, Password);
	}

}
