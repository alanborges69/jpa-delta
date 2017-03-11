package br.edu.faculdadedelta.util.test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class JPAUtilTest {

	private EntityManager em;
	
	@Test
	public void deveTerInstanciaDoEntityManagerDefinida() {
		assertNotNull("instância do EntityManager não deve estar nula", em);
	}
	
	@Test
	public void deveFecharEntityManager() {
		em.close();
		
		assertFalse("instância do EntityManager deve estar fechada", em.isOpen());
	}
	
	@Test
	public void deveAbrirUmaTransacao() {
		assertFalse("transação deve estar fechada", em.getTransaction().isActive());
		
		em.getTransaction().begin();
		
		assertTrue("transação deve estar aberta", em.getTransaction().isActive());
	}

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
}
