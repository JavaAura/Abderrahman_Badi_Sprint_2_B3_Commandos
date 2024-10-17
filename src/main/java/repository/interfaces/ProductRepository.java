package repository.interfaces;

import java.util.List;
import java.util.Optional;

import model.Product;

public interface ProductRepository {

	List<Product> getAll(int pageNumber);

	Optional<Product> get(Long id);
	
	List<Product> search(String name);

	void add(Product product);

	void update(Product product);

	void delete(Long id);

}
