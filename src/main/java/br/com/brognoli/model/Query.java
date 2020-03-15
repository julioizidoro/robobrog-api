package br.com.brognoli.model;

import java.io.Serializable;

public class Query implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String situacao;
	private Filtros filtros;
	
	
	
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public Filtros getFiltros() {
		return filtros;
	}
	public void setFiltros(Filtros filtros) {
		this.filtros = filtros;
	} 
	
	
	

}
