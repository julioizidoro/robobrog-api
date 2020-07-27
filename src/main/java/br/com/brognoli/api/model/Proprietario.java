package br.com.brognoli.api.model;

import java.util.Date;

public class Proprietario {
	
	private String cpf;
	private Date datanascimento;
	
	public Proprietario() {
		super();
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDatanascimento() {
		return datanascimento;
	}

	public void setDatanascimento(Date datanascimento) {
		this.datanascimento = datanascimento;
	}
	
	
	
	
	
	

}
