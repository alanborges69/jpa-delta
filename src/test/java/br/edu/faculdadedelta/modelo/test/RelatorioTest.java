package br.edu.faculdadedelta.modelo.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
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
		
		Criteria criteria = createCriteria(Venda.class, "v")
				// JOIN
				.createAlias("v.cliente", "c")
				// WHERE =
				.add(Restrictions.eq("c.cpf", CPF_PADRAO))
				// COUNT(*)
				.setProjection(Projections.rowCount());
		
		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 3", qtdRegistros >= 3);
	}
	
	@Test
	public void deveConsultarMaiorIdCliente() {
		salvarClientes(3);
		
		Criteria criteria = createCriteria(Cliente.class, "c")
				.setProjection(Projections.max("c.id"));
		
		Long maiorId = (Long) criteria
				.setResultTransformer(Criteria.PROJECTION)
				.uniqueResult();
		
		assertTrue("verifica se o ID é maior que 2 (salvou 3 clientes)", maiorId > 3);
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
	public void deveConsultarDezPrimeirosProdutos() {
		salvarProdutos(20);
		
		Criteria criteria = createCriteria(Produto.class, "p")
				// OFFSET
				.setFirstResult(1)
				// LIMIT
				.setMaxResults(10);
		
		List<Produto> notebooks = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de notebooks é 10", notebooks.size() == 10);
		
		notebooks.forEach(notebook -> assertFalse(notebook.isTransient()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarClientesChaveValor() {
		salvarClientes(5);
		
		ProjectionList projection = Projections.projectionList()
				// SELECT field_a, field_b, field_c
				.add(Projections.property("c.id").as("id"))
				.add(Projections.property("c.nome").as("nome"));
		
		Criteria criteria = createCriteria(Cliente.class, "c")
				.setProjection(projection);
		
		List<Map<String, Object>> clientes = criteria
				.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
				.list();
		
		assertTrue("verifica se a quantidade de cliente é pelo menos 5", clientes.size() >= 5);
		
		clientes.forEach(clienteMap -> {
			clienteMap.forEach((chave, valor) -> {
				assertTrue("chave deve ser String", chave instanceof String);
				assertTrue("valor deve ser String ou Long", valor instanceof String || valor instanceof Long);
			});
		});
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
		
		Criteria criteria = createCriteria(Produto.class, "p")
				// WHERE ILIKE '%string%'
				.add(Restrictions.ilike("p.nome", "book", MatchMode.ANYWHERE));
		
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
		
		ProjectionList projection = Projections.projectionList()
				// SELECT field_a, field_b, field_c
				.add(Projections.property("p.id").as("id"))
				.add(Projections.property("p.nome").as("nome"));
		
		Criteria criteria = createCriteria(Produto.class, "p")
					.setProjection(projection);
		
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
	public void deveConsultarIdENomeConverterCliente() {
		salvarClientes(3);
		
		ProjectionList projection = Projections.projectionList()
				// SELECT field_a, field_b, field_c
				.add(Projections.property("c.id").as("id"))
				.add(Projections.property("c.nome").as("nome"));
		
		Criteria criteria = createCriteria(Cliente.class, "c")
				.setProjection(projection);
		
		List<Cliente> clientes = criteria
				.setResultTransformer(Transformers.aliasToBean(Cliente.class))
				.list();
		
		assertTrue("verifica se a quantidade de clientes é pelo menos 3", clientes.size() >= 3);
		
		clientes.forEach(cliente -> {
			assertTrue("ID deve estar preenchido", cliente.getId() != null);
			assertTrue("Nome deve estar prenchido", cliente.getNome() != null);
			assertTrue("CPF não deve estar preenchido", cliente.getCpf() == null);
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
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarVendasENomeClienteCasoExista() {
		salvarVendas(1);
		
		Criteria criteria = createCriteria(Venda.class, "v")
				// LEFT JOIN
				.createAlias("v.cliente", "c", JoinType.LEFT_OUTER_JOIN)
				// WHERE ILIKE 'string%'
				.add(Restrictions.ilike("c.nome", "Átilla", MatchMode.START));
		
		List<Venda> vendas = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 1", vendas.size() >= 1);
		
		vendas.forEach(venda -> assertFalse("trouxe os itens corretamente", venda.isTransient()));
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
