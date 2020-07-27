package br.com.brognoli.api.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity(name= "FORNECEDOR")
public class Fornecedor implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	private int codigo;
	private String nome;
	private String cgc;
	
	
	public Fornecedor() {
	
	}


	public int getCodigo() {
		return codigo;
	}


	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCgc() {
		return cgc;
	}


	public void setCgc(String cgc) {
		this.cgc = cgc;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigo;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fornecedor other = (Fornecedor) obj;
		if (codigo != other.codigo)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Fornecedor [codigo=" + codigo + ", nome=" + nome + "]";
	}
	
	
	
	
	

}
