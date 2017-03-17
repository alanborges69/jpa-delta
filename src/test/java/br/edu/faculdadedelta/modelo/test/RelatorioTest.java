package br.edu.faculdadedelta.modelo.test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import br.edu.faculdadedelta.base.test.BaseTest;
import br.edu.faculdadedelta.modelo.Cliente;
import br.edu.faculdadedelta.modelo.Produto;
import br.edu.faculdadedelta.modelo.Venda;

public class RelatorioTest extends BaseTest {

	private static final String CPF_PADRAO = "010.188.991-10";
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarTodosClientes() {
		salvarClientes(3);
		
		Criteria criteria = createCriteria(Cliente.class, "c");
		
		List<Cliente> clientes = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de clientes é pelo menos 3", clientes.size() >= 3);
		
		clientes.forEach(cliente -> assertFalse(cliente.isTransient()));
	}
	
	@Test
	public void deveConsultarQuantidadeVendasPorCliente() {
		salvarVendas(3);
		
		Criteria criteria = createCriteria(Venda.class, "v");
		// JOIN
		criteria.createAlias("v.cliente", "c");
		// WHERE
		criteria.add(Restrictions.eq("c.cpf", CPF_PADRAO));
		// COUNT(*)
		criteria.setProjection(Projections.rowCount());
		
		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 3", qtdRegistros >= 3);
	}

	private void salvarClientes(int quantidade) {
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Cliente cliente = new Cliente()
					.setNome("Átilla Barros")
					.setCpf(CPF_PADRAO);
			
			em.persist(cliente);
		}

		em.getTransaction().commit();	
	}
	
	private void salvarProdutos(int quantidade) {
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Produto produto = new Produto("Notebook")
					.setFabricante("Dell");
			
			em.persist(produto);
		}

		em.getTransaction().commit();	
	}
	
	private void salvarVendas(int quantidade) {
		em.getTransaction().begin();
		
		for (int i = 0; i < quantidade; i++) {
			Venda venda = criarVenda();
			
			venda.getProdutos().add(criarProduto("Notebook", "Dell"));
			venda.getProdutos().add(criarProduto("Mouse", "Razer"));
			
			em.persist(venda);
		}

		em.getTransaction().commit();	
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

		return new Venda()
				.setDataHora(new Date())
				.setCliente(cliente);
	}
}
