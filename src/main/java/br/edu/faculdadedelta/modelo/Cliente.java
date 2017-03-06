package br.edu.faculdadedelta.modelo;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cliente extends BaseEntity<Long> {

	private static final long serialVersionUID = -4459227866046584397L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_cliente", unique = true, nullable = false)
	private Long id;

	@Column(name = "nm_cliente", nullable = false, length = 100)
	private String nome;
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "cpf_cliente", length = 20)
	private String cpf;
	
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	private List<Venda> compras;

	public Cliente() {
	}

	public Cliente(String nome) {
		super();
		this.nome = nome;
	}

	public Long getId() {
		return id;
	}

	public Cliente setId(Long id) {
		this.id = id;
		return this;
	}

	public String getNome() {
		return nome;
	}

	public Cliente setNome(String nome) {
		this.nome = nome;
		return this;
	}

	public String getCpf() {
		return cpf;
	}

	public Cliente setCpf(String cpf) {
		this.cpf = cpf;
		return this;
	}

	public List<Venda> getCompras() {
		return compras;
	}
}
