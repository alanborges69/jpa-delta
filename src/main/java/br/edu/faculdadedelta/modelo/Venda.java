package br.edu.faculdadedelta.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Venda extends BaseEntity<Long> {

	private static final long serialVersionUID = -5895550324835026704L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_venda", unique = true, nullable = false)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dh_venda")
	private Date dataHora;
	
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", insertable = true, updatable = false, nullable = false)
	private Cliente cliente;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "venda_produto",
			joinColumns = @JoinColumn(name = "id_venda"),
			inverseJoinColumns = @JoinColumn(name = "id_produto"))
	private List<Produto> produtos;

	public Venda() {
	}

	public Long getId() {
		return id;
	}

	public Venda setId(Long id) {
		this.id = id;
		return this;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public Venda setDataHora(Date dataHora) {
		this.dataHora = dataHora;
		return this;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Venda setCliente(Cliente cliente) {
		this.cliente = cliente;
		return this;
	}

	public List<Produto> getProdutos() {
		if (produtos == null) {
			produtos = new ArrayList<>();
		}
		return produtos;
	}
}
