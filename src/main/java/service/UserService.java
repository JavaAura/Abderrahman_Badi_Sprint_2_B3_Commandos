package service;

import java.util.Optional;

import model.User;
import repository.interfaces.UserRepository;

public class UserService {
	
	private UserRepository userRepository ;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public Optional<User> login(String email , String Password){
		return userRepository.login(email, Password);
	}
	
	

}
