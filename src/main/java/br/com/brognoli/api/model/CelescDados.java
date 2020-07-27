package br.com.brognoli.api.model;

import java.util.List;

public class CelescDados {
	
	private String nome;
	private String cpf;
	private String codigo;
	private String endereco;
	private String cidade;
	private String telefone;
	private String celular;
	private String fax;
	private String email;
	private String emailfatura;
	private String situacao;
	private String dataCorte;
	private List<CelescFatura> listaFatura;
	private List<CelescHistorico> listaHistorico;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmailfatura() {
		return emailfatura;
	}
	public void setEmailfatura(String emailfatura) {
		this.emailfatura = emailfatura;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getDataCorte() {
		return dataCorte;
	}
	public void setDataCorte(String dataCorte) {
		this.dataCorte = dataCorte;
	}
	public List<CelescFatura> getListaFatura() {
		return listaFatura;
	}
	public void setListaFatura(List<CelescFatura> listaFatura) {
		this.listaFatura = listaFatura;
	}
	
	
	public List<CelescHistorico> getListaHistorico() {
		return listaHistorico;
	}
	public void setListaHistorico(List<CelescHistorico> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		CelescDados other = (CelescDados) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CelescDados [nome=" + nome + ", cpf=" + cpf + "]";
	}
	
	
	
}
