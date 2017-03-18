package br.edu.faculdadedelta.modelo.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
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
		// WHERE =
		criteria.add(Restrictions.eq("c.cpf", CPF_PADRAO));
		// COUNT(*)
		criteria.setProjection(Projections.rowCount());
		
		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 3", qtdRegistros >= 3);
	}
	
	@Test
	public void deveConsultarVendasDaUltimaSemana() {
		salvarVendas(3);
		
		Calendar ultimaSemana = Calendar.getInstance();
		ultimaSemana.add(Calendar.WEEK_OF_YEAR, -1);
		
		Criteria criteria = createCriteria(Venda.class, "v");
		// WHERE BETWEEN
		criteria.add(Restrictions.between("v.dataHora", ultimaSemana.getTime(), new Date()));
		// COUNT(*)
		criteria.setProjection(Projections.rowCount());
		
		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 3", qtdRegistros >= 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarNotebooks() {
		salvarProdutos(3);
		
		Criteria criteria = createCriteria(Produto.class, "p");
		// WHERE IN
		criteria.add(Restrictions.in("p.nome", "Notebook", "Netbook", "Macbook"));
		// ORDER BY
		criteria.addOrder(Order.asc("p.fabricante"));
		
		List<Produto> notebooks = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de notebooks é pelo menos 3", notebooks.size() >= 3);
		
		notebooks.forEach(notebook -> assertFalse(notebook.isTransient()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarNotebooksDellOuSamsung() {
		salvarProdutos(3);
		
		Criteria criteria = createCriteria(Produto.class, "p");
		// WHERE OR
		criteria.add(Restrictions.or(
				Restrictions.eq("p.fabricante", "Dell"),
				Restrictions.eq("p.fabricante", "Samsung")
		));
		
		List<Produto> notebooks = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de notebooks é pelo menos 3", notebooks.size() >= 3);
		
		notebooks.forEach(notebook -> assertFalse(notebook.isTransient()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarProdutosContendoParteDoNome() {
		salvarProdutos(3);
		
		Criteria criteria = createCriteria(Produto.class, "p");
		// WHERE ILIKE %%
		criteria.add(Restrictions.ilike("p.nome", "book", MatchMode.ANYWHERE));
		
		List<Produto> produtos = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de produtos é pelo menos 3", produtos.size() >= 3);
		
		produtos.forEach(produto -> assertFalse(produto.isTransient()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarIdENomeProduto() {
		salvarProdutos(1);
		
		ProjectionList projection = Projections.projectionList();
		
		projection.add(Projections.property("p.id").as("id"));
		projection.add(Projections.property("p.nome").as("id"));
		
		Criteria criteria = createCriteria(Produto.class, "p");
		// SELECT field_a, field_b, field_c
		criteria.setProjection(projection);
		
		List<Object[]> produtos = criteria
				.setResultTransformer(Criteria.PROJECTION)
				.list();
		
		assertTrue("verifica se a quantidade de produtos é pelo menos 1", produtos.size() >= 1);
		
		produtos.forEach(produto -> {
			assertTrue("primeiro item deve ser o ID", 	produto[0] instanceof Long);
			assertTrue("primeiro item deve ser o nome", produto[1] instanceof String);
		});
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarVendasPorNomeClienteUsandoSubquery() {
		salvarVendas(1);
		
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Cliente.class, "c")
				// WHERE IN
				.add(Restrictions.in("c.id", 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L))
				// SELECT field_a FROM
				.setProjection(Projections.property("c.nome"));
		
		Criteria criteria = createCriteria(Venda.class, "v")
				// INNER JOIN
				.createAlias("v.cliente", "cli")
				// WHERE IN ( SELECT )
				.add(Subqueries.propertyIn("cli.nome", detachedCriteria));
		
		List<Venda> vendas = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 1", vendas.size() >= 1);
		
		vendas.forEach(venda -> assertFalse("trouxe os itens corretamente", venda.getCliente().isTransient()));
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
