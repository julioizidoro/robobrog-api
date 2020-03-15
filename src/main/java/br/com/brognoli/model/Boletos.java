package br.com.brognoli.model;

import java.util.List;

public class Boletos {
	
	private Cobranca cobranca;
	private String Endereco;
	private String numero;
	private String linhaDigitavel;
		
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
	
	
	
	
	
	
	
	
	

}
