package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import model.Product;
import repository.interfaces.ProductRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private Product product;
    private List<Product> productList = new ArrayList<>();

    @Before
    public void setUp() {
        product = new Product();
        product.setName("PS Vita");
        product.setDescription("Mobile console for playing");
        product.setIsDeleted(false);
        product.setStock(120);
        product.setPrice(150);

        Product product1 = new Product();
        product1.setName("PS Vita 2");
        product1.setDescription("Mobile console for playing");
        product1.setIsDeleted(false);
        product1.setStock(100);
        product1.setPrice(180);

        productList.add(product);
        productList.add(product1);

        productService = new ProductService(productRepository);
    }

    @Test
    public void getProductByIdTest() {
        when(productRepository.get(1L)).thenReturn(Optional.of(product));
        Optional<Product> result = productService.getProduct(1L);
        assertTrue(result.isPresent());
        assertEquals("PS Vita", result.get().getName());
        verify(productRepository).get(1L);
    }

    @Test
    public void getAllProductsTest() {
        when(productRepository.getAll(1)).thenReturn(productList);
        List<Product> result = productService.getAllProducts(1);
        assertEquals(2, result.size());
        assertEquals("PS Vita", result.get(0).getName());
        assertEquals("PS Vita 2", result.get(1).getName());
        verify(productRepository).getAll(1);
    }

    @Test
    public void addProductTest() {
        doNothing().when(productRepository).add(product);
        productService.addProduct(product);
        verify(productRepository).add(product);
    }

    @Test
    public void updateProductTest() {
        doNothing().when(productRepository).update(product);
        productService.updateProduct(product);
        verify(productRepository).update(product);
    }

    @Test
    public void deleteProductTest() {
        doNothing().when(productRepository).delete(1L);
        productService.deleteProduct(1L);
        verify(productRepository).delete(1L);
    }

    @Test
    public void searchProductTest() {
        when(productRepository.search("Vita")).thenReturn(productList);
        List<Product> result = productService.searchProduct("Vita");
        assertEquals(2, result.size());
        assertEquals("PS Vita", result.get(0).getName());
        verify(productRepository).search("Vita");
    }
}
