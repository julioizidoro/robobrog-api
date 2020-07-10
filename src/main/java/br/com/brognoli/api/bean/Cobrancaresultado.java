package br.com.brognoli.api.bean;

import br.com.brognoli.api.model.Cobranca;

public class Cobrancaresultado {
	
	private Cobranca cobranca;
	private boolean capturou;
	public Cobranca getCobranca() {
		return cobranca;
	}
	public void setCobranca(Cobranca cobranca) {
		this.cobranca = cobranca;
	}
	public boolean isCapturou() {
		return capturou;
	}
	public void setCapturou(boolean capturou) {
		this.capturou = capturou;
	}
	
	

}
