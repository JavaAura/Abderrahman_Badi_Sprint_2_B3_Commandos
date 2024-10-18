package repository.interfaces;

import java.util.List;
import java.util.Optional;

import model.Product;

public interface ProductRepository {

	List<Product> getAll(int page , int pageSize);

	Optional<Product> get(Long id);
	
	List<Product> search(String name);
	
	long getTotalProductCount();

	void add(Product product);

	void update(Product product);

	void delete(Long id);

}
