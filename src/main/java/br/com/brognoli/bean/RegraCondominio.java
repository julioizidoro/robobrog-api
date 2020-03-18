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
		if (modelo.getAdministradora().equalsIgnoreCase("Grupo Embracon")) {
			if (cobranca.getCondominio().equalsIgnoreCase("CONJUNTO MARIA ZITA")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("ALBATROZ")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("MONT REAL")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("ILHA DE ITAPARICA")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("NAIR HEIDERSCHEIDT")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			}  else if (cobranca.getCondominio().equalsIgnoreCase("ÁGUAS DO BALNEÁRIO")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("BOULEVARD DU NORD")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("HARMONY")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("KALAMARI RESIDENCE")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("RECANTO DO HORIZONTE")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} /*else if (cobranca.getCondominio().equalsIgnoreCase("MALBEC")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("PORTO REAL")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("STELLA MARIS")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("VILLA MILANO")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			}  else if (cobranca.getCondominio().equalsIgnoreCase("VILLENEUVE RESIDENCE")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("MARINA PARK")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("PARQUE ILHA DO ARVOREDO")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("Miguel Daux")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			} else if (cobranca.getCondominio().equalsIgnoreCase("MORADAS DO GARAPUVU")) {
				modelo.setModelo("ModeloGrupoEmbraconMariaZita");
			}     */ 
		}
		return modelo;
	}

}
