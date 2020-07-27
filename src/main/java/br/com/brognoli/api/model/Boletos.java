package br.com.brognoli.api.model;

import java.util.List;

public class Boletos {
	
	private Cobranca cobranca;
	private String codigoImovel;
	private String Endereco;
	private String numero;
	private String linhaDigitavel;
	private Imoveladm imovelAdm;
	private String competencia;
	private String datavencimento;
	private String cnpj;
	private String nomearquivo;
	private String valor;
	private String referencia;
	private String tipo;
	
	
		
	private List<Resumo> listaResumo;
	
	public Cobranca getCobranca() {
		return cobranca;
	}
	public void setCobranca(Cobranca cobranca) {
		this.cobranca = cobranca;
	}
	public List<Resumo> getListaResumo() {
		return listaResumo;
	}
	public void setListaResumo(List<Resumo> listaResumo) {
		this.listaResumo = listaResumo;
	}
	public String getEndereco() {
		return Endereco;
	}
	public void setEndereco(String endereco) {
		Endereco = endereco;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getLinhaDigitavel() {
		return linhaDigitavel;
	}
	public void setLinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel;
	}
	public Imoveladm getImovelAdm() {
		return imovelAdm;
	}
	public void setImovelAdm(Imoveladm imovelAdm) {
		this.imovelAdm = imovelAdm;
	}
	public String getCompetencia() {
		return competencia;
	}
	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}
	public String getDatavencimento() {
		return datavencimento;
	}
	public void setDatavencimento(String datavencimento) {
		this.datavencimento = datavencimento;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getNomearquivo() {
		return nomearquivo;
	}
	public void setNomearquivo(String nomearquivo) {
		this.nomearquivo = nomearquivo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCodigoImovel() {
		return codigoImovel;
	}
	public void setCodigoImovel(String codigoImovel) {
		this.codigoImovel = codigoImovel;
	}
	
	
	
	
	
	
	
	
	

}
