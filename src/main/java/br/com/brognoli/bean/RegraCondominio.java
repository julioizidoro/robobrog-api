package br.com.brognoli.bean;

import br.com.brognoli.model.Cobranca;
import br.com.brognoli.model.Modelos;

public class RegraCondominio {
	
	public Modelos retornaModelo(Modelos modelo, Cobranca cobranca) {
		if (modelo.getAdministradora().equalsIgnoreCase("Villa Real Serviços Contábeis")) {
			if (cobranca.getCondominio().equalsIgnoreCase("Áurea")) {
				modelo.setModelo("ModeloResumoRateioUmaColuna");
			} else if (cobranca.getCondominio().equalsIgnoreCase("San Marino")) {
				modelo.setModelo("ModeloResumoRateioUmaColuna");
			}
		}
		return modelo;
	}

}
