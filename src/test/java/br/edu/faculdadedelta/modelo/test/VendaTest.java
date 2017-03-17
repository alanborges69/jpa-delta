package br.edu.faculdadedelta.modelo.test;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Test;

import br.edu.faculdadedelta.base.test.BaseTest;
import br.edu.faculdadedelta.modelo.Cliente;
import br.edu.faculdadedelta.modelo.Produto;
import br.edu.faculdadedelta.modelo.Venda;
import br.edu.faculdadedelta.util.JPAUtil;

public class VendaTest extends BaseTest {

	private static final String CPF_PADRAO = "010.188.991-10";
	private static final Logger LOGGER = Logger.getLogger(ClienteTest.class);
	
	@Test
	public void deveSalvarVendaComRelacionamentosEmCascadta() {
		Venda venda = criarVenda();
		
		venda.getProdutos().add(criarProduto("Notebook", "Dell"));
		venda.getProdutos().add(criarProduto("Mouse", "Razer"));
		
		assertTrue("não deve ter ID definido", venda.isTransient());
		
		em.getTransaction().begin();
		em.persist(venda);
		em.getTransaction().commit();

		assertFalse("deve ter ID definido", venda.isTransient());
		assertFalse("deve ter ID definido", venda.getCliente().isTransient());
		
		venda.getProdutos().forEach(produto -> assertFalse("deve ter ID definido", produto.isTransient()));
	}
	
	@Test(expected = IllegalStateException.class)
	public void naoDeveFazerMergeEmObjetosTransient() {
		Venda venda = criarVenda();
		
		venda.getProdutos().add(criarProduto("Notebook", "Dell"));
		venda.getProdutos().add(criarProduto("Mouse", "Razer"));
		
		assertTrue("não deve ter ID definido", venda.isTransient());
		
		em.getTransaction().begin();
		venda = em.merge(venda);
		em.getTransaction().commit();
		
		fail("não deveria ter salvo (merge) uma venda nova com relacionamentos transient");
	}

	@Test
	public void deveConsultarQuantidadeProdutosVendidos() {
		Venda venda = criarVenda("001.001.001-01");
		
		for (int i = 0; i < 10; i++) {
			venda.getProdutos().add(criarProduto("Produto " + i, "Marca " + i));
		}
		
		em.getTransaction().begin();
		em.persist(venda);
		em.getTransaction().commit();
		
		assertFalse("deve ter persistido a venda", venda.isTransient());
		
		int qtdProdutosAdicionados = venda.getProdutos().size();
		
		assertTrue("lista de produtos deve ter itens", qtdProdutosAdicionados > 0);
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append("   FROM Venda v ");
		jpql.append("  INNER JOIN v.produtos p ");
		jpql.append("  INNER JOIN v.cliente c ");
		jpql.append("  WHERE c.cpf = :cpf ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("cpf", "001.001.001-01");

		Long qtdProdutosDaVenda = (Long) query.getSingleResult();
		
		assertEquals("quantidade de produtos deve ser igual a quantidade da lista de produtos", 
				qtdProdutosDaVenda.intValue(), qtdProdutosAdicionados);
	}

	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
		
		entityManager.getTransaction().begin();
		
		Query query = entityManager.createQuery("DELETE FROM Venda v");
		int qtdRegistrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();

		assertTrue("certifica que a base foi limpada", qtdRegistrosExcluidos > 0);
		
		LOGGER.info("============ Base de testes limpada (Tabela Venda) ============");
	}

	private Produto criarProduto(String nome, String marca) {
		return new Produto()
				.setNome(nome)
				.setFabricante(marca);
	}
	
	private Venda criarVenda() {
		return criarVenda(null);
	}
	
	private Venda criarVenda(String cpf) {
		Cliente cliente = new Cliente()
				.setNome("Átilla Barros")
				.setCpf(cpf == null ? CPF_PADRAO : cpf);

		assertTrue("não deve ter ID definido", cliente.isTransient());
		
		return new Venda()
				.setDataHora(new Date())
				.setCliente(cliente);
	}
}
