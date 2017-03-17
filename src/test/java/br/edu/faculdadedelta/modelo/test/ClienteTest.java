package br.edu.faculdadedelta.modelo.test;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.junit.AfterClass;
import org.junit.Test;

import br.edu.faculdadedelta.base.test.BaseTest;
import br.edu.faculdadedelta.modelo.Cliente;
import br.edu.faculdadedelta.util.JPAUtil;

public class ClienteTest extends BaseTest {

	private static final String CPF_PADRAO = "010.188.991-10";
	private static final Logger LOGGER = Logger.getLogger(ClienteTest.class);
	
	@Test
	public void deveSalvarCliente() {
		Cliente cliente = new Cliente()
				.setNome("Átilla Barros")
				.setCpf(CPF_PADRAO);
		
		assertTrue("não deve ter ID definido", cliente.isTransient());
		
		em.getTransaction().begin();
		em.persist(cliente);
		em.getTransaction().commit();
		
		assertFalse("deve ter ID definido", cliente.isTransient());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarCpf() {
		deveSalvarCliente();
		
		String filtro = "Barros";
		
		Query query = em.createQuery("SELECT c.cpf FROM Cliente c WHERE c.nome LIKE :nome");
		query.setParameter("nome", "%".concat(filtro).concat("%"));
		
		List<String> listaCpf = query.getResultList();
		
		assertFalse("verifica se há registros na lista", listaCpf.isEmpty());
		
		listaCpf.forEach(cpf -> LOGGER.info("\n\n=========== CPF: " + cpf + "\n\n"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarClienteComIdNome() {
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT new Cliente(c.id, c.nome) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		List<Cliente> clientes = query.getResultList();
		
		assertFalse("verifica se há registros na lista", clientes.isEmpty());
		
		clientes.forEach(cliente -> {
			assertNull("verifica que o cpf deve estar null", cliente.getCpf());
			
			cliente.setCpf(CPF_PADRAO);
		});
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarIdNome() {
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT c.id, c.nome FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		List<Object[]> resultado = query.getResultList();
		
		assertFalse("verifica se há registros na lista", resultado.isEmpty());
		
		resultado.forEach(linha -> {
			assertTrue("verifica que o primeiro item é o ID", 	linha[0] instanceof Long);
			assertTrue("verifica que o segundo item é o nome", 	linha[1] instanceof String);
			
			Cliente cliente = new Cliente((Long) linha[0], (String) linha[1]);
			assertNotNull(cliente);
		});
	}
	
	@Test
	public void deveVerificarExistenciaCliente() {
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT COUNT(c.id) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		Long qtdResultados = (Long) query.getSingleResult();

		assertTrue("verifica se há registros na lista", qtdResultados > 0L);
	}
	
	@Test(expected = NonUniqueResultException.class)
	public void naoDeveFuncionarSingleResultComMuitosRegistros() {
		deveSalvarCliente();
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		query.getSingleResult();
		
		fail("método getSingleResult deve disparar exception NonUniqueResultException");
	}
	
	@Test(expected = NoResultException.class)
	public void naoDeveFuncionarSingleResultComNenhumRegistro() {
		deveSalvarCliente();
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", "000.000.000-00");
		
		query.getSingleResult();
		
		fail("método getSingleResult deve disparar exception NoResultException");
	}
	
	@Test
	public void deveAcessarAtributoLazy() {
		deveSalvarCliente();
		
		Cliente cliente = em.find(Cliente.class, 1L);
		
		assertNotNull("verifica se encontrou um registro", cliente);
		
		assertNotNull("lista lazy não deve ser null", cliente.getCompras());
	}
	
	@Test(expected = LazyInitializationException.class)
	public void naoDeveAcessarAtributoLazyForaEscopoEntityManager() {
		deveSalvarCliente();
		
		Cliente cliente = em.find(Cliente.class, 1L);
		
		assertNotNull("verifica se encontrou um registro", cliente);

		em.detach(cliente);
		
		cliente.getCompras().size();
		
		fail("deve disparar LazyInitializationException ao acessar atributo lazy de um objeto fora de escopo do EntityManager");
	}
	
	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
		
		entityManager.getTransaction().begin();
		
		Query query = entityManager.createQuery("DELETE FROM Cliente c");
		int qtdRegistrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();

		assertTrue("certifica que a base foi limpada", qtdRegistrosExcluidos > 0);
		
		LOGGER.info("============ Base de testes limpada (Tabela Cliente) ============");
	}
}
