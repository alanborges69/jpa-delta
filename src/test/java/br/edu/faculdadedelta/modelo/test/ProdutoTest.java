package br.edu.faculdadedelta.modelo.test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.modelo.Produto;
import br.edu.faculdadedelta.util.JPAUtil;

public class ProdutoTest {

	private static final Logger LOGGER = Logger.getLogger(ProdutoTest.class);
	
	private EntityManager em;
	
	@Before
	public void instanciarEntityManager() {
		em = JPAUtil.INSTANCE.getEntityManager();
	}
	
	@After
	public void fecharEntityManager() {
		if (em.isOpen()) {
			em.close();
		}
	}
	
	@Test
	public void deveAlterarProduto() {
		deveSalvarProduto();
		
		TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1);
		
		Produto produto = query.getSingleResult();
		
		assertNotNull("deve ter encontrado um produto", produto);
		
		Integer versao = produto.getVersion();
		
		em.getTransaction().begin();
		
		produto.setFabricante("Sony");
		
		produto = em.merge(produto);
		
		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.intValue(), produto.getVersion().intValue());
	}
	
	@Test
	public void devePesquisarProdutos() {
		for (int i = 0; i < 10; i++) {
			deveSalvarProduto();
		}
		
		TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class);
		List<Produto> produtos = query.getResultList();
		
		assertFalse("deve ter encontrado um produto", produtos.isEmpty());
		assertTrue("deve ter encontrado vários produtos", produtos.size() >= 10);
	}
	
	@Test
	public void deveExcluirProduto() {
		deveSalvarProduto();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(p.id) FROM Produto p", Long.class).setMaxResults(1);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Produto produto = em.find(Produto.class, id);
		em.remove(produto);
		
		em.getTransaction().commit();
		
		Produto produtoExcluido = em.find(Produto.class, id);
		
		assertNull("não deve ter encontrado o produto", produtoExcluido);
	}
	
	@Test
	public void deveSalvarProduto() {
		Produto produto = new Produto("Notebook")
				.setFabricante("Dell");
		
		assertTrue("não deve ter ID definido", produto.isTransient());
		
		em.getTransaction().begin();
		
		produto = em.merge(produto);
		
		em.getTransaction().commit();
		
		assertNotNull("deve ter ID definido", produto.getId());
	}
	
	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
		
		entityManager.getTransaction().begin();
		
		Query query = entityManager.createQuery("DELETE FROM Produto p");
		int qtdRegistrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();

		assertTrue("certifica que a base foi limpada", qtdRegistrosExcluidos > 0);
		
		LOGGER.info("============ Base de testes limpada (Tabela Produto) ============");
	}
}
