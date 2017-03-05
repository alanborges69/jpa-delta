package br.edu.faculdadedelta.util.test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class JPAUtilTest {

	@Test
	public void deveInstanciarEntityManager() {
		EntityManager em = JPAUtil.INSTANCE.getEntityManager();
		
		assertNotNull("instância do EntityManager não deve estar nula", em);
		
		em.close();
	}
	
	@Test
	public void deveAbrirUmaTransacao() {
		EntityManager em = JPAUtil.INSTANCE.getEntityManager();
		
		assertFalse("transação deve estar fechada", em.getTransaction().isActive());
		
		em.getTransaction().begin();
		
		assertTrue("transação deve estar aberta", em.getTransaction().isActive());
		
		em.close();
	}
	
	@AfterClass
	public static void fecharFactory() {
		System.out.println("Fechando factory...");
		
		JPAUtil.INSTANCE.close();
	}
}
