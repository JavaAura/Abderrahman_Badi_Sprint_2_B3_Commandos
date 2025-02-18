package repository.implementation;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
import model.Product;
import repository.interfaces.ProductRepository;
import util.PersistenceUtil;

public class ProductRepositoryImpl implements ProductRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImpl.class);

	private static final String LIST = "SELECT p FROM Product p ORDER BY p.id ASC";
	private static final String GET = "SELECT p FROM Product p WHERE p.id = :id";
	private static final String SEARCH = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name) ORDER BY p.id ASC";
	private static final String COUNT = "SELECT COUNT(p) FROM Product p";

	@Override
	public List<Product> getAll(int page, int pageSize) {

	    EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();

	    List<Product> products = null;  

	    try {
	        
	        TypedQuery<Product> query = entityManager.createQuery(LIST, Product.class);
	        query.setFirstResult((page - 1) * pageSize);  
	        query.setMaxResults(pageSize);                

	        products = query.getResultList();            

	    } catch (Exception e) {
	        logger.error("Error fetching products for page " + page + " with pageSize " + pageSize, e);
	    } finally {
	        entityManager.close();   
	    }

	    return products;   
	}



	@Override
	public Optional<Product> get(Long id) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		
		try {
			TypedQuery<Product> query = entityManager.createQuery(GET,Product.class);
			query.setParameter("id", id);
			Product product = query.getSingleResult();
			return Optional.ofNullable(product);
		} catch (NoResultException e) {
            return Optional.empty();   
        
		}finally {
			entityManager.close();
		}
	}

	@Override
	public List<Product> search(String name) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		try {
			TypedQuery<Product> query = entityManager.createQuery(SEARCH,Product.class);
			query.setParameter("name","%" + name + "%");
			return query.getResultList();
		}finally {
			entityManager.close();
		}
	}
	
	@Override
	public void add(Product product) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = null ;
		
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();
			entityManager.persist(product);
			transaction.commit();
		}catch(Exception e) {
			if(transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Error Adding This Product", e);
			
		}finally {
			entityManager.close();
		}

	}

	@Override
	public void update(Product product) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = null;
		
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();
			entityManager.merge(product);
			transaction.commit();
		}catch(Exception e) {
			if(transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Error Updating This Product" , e);
		}finally {
			entityManager.close();
		}

	}

	@Override
	public void delete(Long id) {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction transaction = null;
		
		try {
			transaction = entityManager.getTransaction();
			transaction.begin();
			Product product = entityManager.find(Product.class , id);
					if(product != null) {
						
						entityManager.remove(product);
					}
			transaction.commit();
		}catch(Exception e) {
			if(transaction.isActive()) {
				transaction.rollback();
			}
			logger.error("Error Deleting This Product" ,e );
		}finally {
			entityManager.close();
		}
	}
	
	@Override
	public long getTotalProductCount() {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();

		try {
			TypedQuery<Long> query = entityManager.createQuery(COUNT, Long.class);
			return query.getSingleResult();
		} finally {
			entityManager.close();
		}
	}



 


	@Override
	public List<Product> getAllProducts() {
		EntityManager entityManager = PersistenceUtil.getEntityManagerFactory().createEntityManager();
		List<Product> products = null;

		try {
			TypedQuery<Product> query = entityManager.createQuery(LIST, Product.class);
			products = query.getResultList();
		} catch (Exception e) {
			logger.error("Error fetching all products", e);
		} finally {
			entityManager.close();
		}

		return products;
	}



}
