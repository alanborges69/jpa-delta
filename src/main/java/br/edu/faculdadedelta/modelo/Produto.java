package br.edu.faculdadedelta.modelo;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "produto")
@Entity
public class Produto extends BaseEntity<Long> {

	private static final long serialVersionUID = -571450929835176999L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_produto", unique = true, nullable = false)
	private Long id;

	@Column(name = "nm_produto", nullable = false, length = 100)
	private String nome;
	
	@Column(name = "nm_fabricante", length = 50)
	@Basic(fetch = FetchType.LAZY)
	private String fabricante;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "dt_validade")
	@Basic(fetch = FetchType.LAZY)
	private Date validade;

	public Produto() {
	}

	public Produto(String nome) {
		super();
		this.nome = nome;
	}

	public Produto(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public Produto setNome(String nome) {
		this.nome = nome;
		return this;
	}

	public String getFabricante() {
		return fabricante;
	}

	public Produto setFabricante(String fabricante) {
		this.fabricante = fabricante;
		return this;
	}

	public Date getValidade() {
		return validade;
	}

	public Produto setValidade(Date validade) {
		this.validade = validade;
		return this;
	}
}
