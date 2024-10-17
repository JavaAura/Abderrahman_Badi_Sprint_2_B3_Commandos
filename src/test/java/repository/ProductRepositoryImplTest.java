package repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Product;
import repository.implementation.ProductRepositoryImpl;
import repository.interfaces.ProductRepository;
import util.PersistenceUtil;

public class ProductRepositoryImplTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImplTest.class);

    private EntityManager entityManager;

    private ProductRepository productRepository;

    private Product product;

    @BeforeClass
    public static void setUpBeforeClass() {
        System.setProperty("persistence.unit.name", "test_COMMANDOS_PU");
    }

    @Before
    public void setUp() {
        productRepository = new ProductRepositoryImpl();

        product = new Product();
        product.setName("PlayStation 5");
        product.setDescription("The best console for gaming");
        product.setStock(15);
        product.setPrice(6000);

        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(product);
        transaction.commit();
        entityManager.close();
    }

    @After
    public void tearDown() {
        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.createQuery("DELETE FROM Product").executeUpdate();

        transaction.commit();
        entityManager.close();
    }

    @Test
    public void testGetProductById() {
        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        Product retrievedProduct = productRepository.get(product.getId()).orElse(null);
        assertNotNull(retrievedProduct);
        assertEquals("PlayStation 5", retrievedProduct.getName());
        entityManager.close();
    }

    @Test
    public void testAddProduct() {
        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        Product newProduct = new Product();
        newProduct.setName("Nintendo Switch");
        newProduct.setDescription("Portable gaming console");
        newProduct.setStock(50);
        newProduct.setPrice(3000);

        productRepository.add(newProduct);

        Product retrievedProduct = entityManager.find(Product.class, newProduct.getId());
        assertNotNull(retrievedProduct);
        assertEquals("Nintendo Switch", retrievedProduct.getName());
        entityManager.close();
    }

    @Test
    public void testUpdateProduct() {
        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        product.setPrice(7000);
        productRepository.update(product);

        Product updatedProduct = entityManager.find(Product.class, product.getId());
        assertNotNull(updatedProduct);
        assertEquals(7000, updatedProduct.getPrice(), 0.001); // Proper floating-point comparison
        entityManager.close();
    }

    @Test
    public void testDeleteProduct() {
        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        productRepository.delete(product.getId());

        Product deletedProduct = entityManager.find(Product.class, product.getId());
        assertNull(deletedProduct);
        entityManager.close();
    }

    @Test
    public void testSearchProductByName() {
        entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();

        // Add another product to test search functionality
        Product anotherProduct = new Product();
        anotherProduct.setName("PlayStation 5 Digital Edition");
        anotherProduct.setDescription("A digital-only version of PlayStation 5");
        anotherProduct.setStock(10);
        anotherProduct.setPrice(5000);

        productRepository.add(anotherProduct);

        // Search for "PlayStation"
        List<Product> products = productRepository.search("PlayStation");
        assertNotNull(products);
        assertEquals(2, products.size()); // Expecting 2 products with "PlayStation" in the name

        entityManager.close();
    }
}
