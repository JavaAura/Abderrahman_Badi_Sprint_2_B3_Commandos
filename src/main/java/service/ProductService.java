package service;

import java.util.List;
import java.util.Optional;

import model.Product;
import repository.interfaces.ProductRepository;

public class ProductService {

	private ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getAllProducts(int page , int pageSize) {

		return productRepository.getAll(page,pageSize);

	}

	public Optional<Product> getProduct(Long id) {

		return productRepository.get(id);
	}

	public List<Product> searchProduct(String name) {

		return productRepository.search(name);

	}

	public void addProduct(Product product) {
		productRepository.add(product);
	}

	public void updateProduct(Product product) {
		productRepository.update(product);
	}

	public void deleteProduct(Long id) {
		productRepository.delete(id);
	}

	public long getTotalProductCount() {
		return productRepository.getTotalProductCount();
	}
	

	public List<Product> getAllProducts(){
		return  productRepository.getAllProducts();
	}

}
