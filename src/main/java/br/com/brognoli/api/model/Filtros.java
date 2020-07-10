package br.com.brognoli.api.model;

import java.io.Serializable;

public class Filtros implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Periodo periodo;
	private Integer administradora;
	private Integer condominio;
	private int page;
	private Integer unidade;
	
	
	public Periodo getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}
	public Integer getAdministradora() {
		return administradora;
	}
	public void setAdministradora(Integer administradora) {
		this.administradora = administradora;
	}
	public Integer getCondominio() {
		return condominio;
	}
	public void setCondominio(Integer condominio) {
		this.condominio = condominio;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public Integer getUnidade() {
		return unidade;
	}
	public void setUnidade(Integer unidade) {
		this.unidade = unidade;
	}
	
	
	
	
	

}
