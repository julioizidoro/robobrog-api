package br.com.brognoli.model;

import java.util.List;

public class Winkerexcel {
	
	private String edificio;
	private String endereco;
	private String numero;
	private String bloco;
	private String unidade;
	private String competencia;
	private String vencimento;
	private String linhadigitavel;
	private String codigobarras;
	private Float total;
	private String desconto;
	private List<Winkerexcelvalor> listawinkerexcelvalor;
	
	
	public String getEdificio() {
		return edificio;
	}
	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getBloco() {
		return bloco;
	}
	public void setBloco(String bloco) {
		this.bloco = bloco;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getCompetencia() {
		return competencia;
	}
	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}
	public String getVencimento() {
		return vencimento;
	}
	public void setVencimento(String vencimento) {
		this.vencimento = vencimento;
	}
	public String getLinhadigitavel() {
		return linhadigitavel;
	}
	public void setLinhadigitavel(String linhadigitavel) {
		this.linhadigitavel = linhadigitavel;
	}
	public String getCodigobarras() {
		return codigobarras;
	}
	public void setCodigobarras(String codigobarras) {
		this.codigobarras = codigobarras;
	}
	public Float getTotal() {
		return total;
	}
	public void setTotal(Float total) {
		this.total = total;
	}
	public String getDesconto() {
		return desconto;
	}
	public void setDesconto(String desconto) {
		this.desconto = desconto;
	}
	public List<Winkerexcelvalor> getListawinkerexcelvalor() {
		return listawinkerexcelvalor;
	}
	public void setListawinkerexcelvalor(List<Winkerexcelvalor> listawinkerexcelvalor) {
		this.listawinkerexcelvalor = listawinkerexcelvalor;
	}
	
	

}
