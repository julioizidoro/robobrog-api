package br.com.brognoli.model;

import java.util.List;

public class Resumo {
	
	private String descicao;
	private float valor;
	private List<Despesas> listaDespesas;
	
	
	public String getDescicao() {
		return descicao;
	}
	public void setDescicao(String descicao) {
		this.descicao = descicao;
	}
	public float getValor() {
		return valor;
	}
	public void setValor(float valor) {
		this.valor = valor;
	}
	
	public List<Despesas> getListaDespesas() {
		return listaDespesas;
	}
	public void setListaDespesas(List<Despesas> listaDespesas) {
		this.listaDespesas = listaDespesas;
	}
	
	

}
