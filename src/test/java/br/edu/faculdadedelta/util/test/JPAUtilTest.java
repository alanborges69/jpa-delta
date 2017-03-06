package br.edu.faculdadedelta.util.test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class JPAUtilTest {

	private static final Logger LOGGER = Logger.getLogger(JPAUtilTest.class);
	
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
	public void deveTerInstanciaDoEntityManagerDefinida() {
		assertNotNull("instância do EntityManager não deve estar nula", em);
	}
	
	@Test
	public void deveFecharEntityManager() {
		em.close();
		
		assertFalse("instância do EntityManager não deve estar nula", em.isOpen());
	}
	
	@Test
	public void deveAbrirUmaTransacao() {
		assertFalse("transação deve estar fechada", em.getTransaction().isActive());
		
		em.getTransaction().begin();
		
		assertTrue("transação deve estar aberta", em.getTransaction().isActive());
	}
	
	@AfterClass
	public static void fecharFactory() {
		LOGGER.debug("fechando entity manager factory...");
		
		JPAUtil.INSTANCE.close();
	}
}
