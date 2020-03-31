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
		
		if (modelo.getAdministradora().equalsIgnoreCase("Liderança Administradora de Condomínios")) {
			if (cobranca.getCondominio().equalsIgnoreCase("ALPHACENTAURI")) {
				modelo.setModelo("ModeloLideranca");
			} else if (cobranca.getCondominio().equalsIgnoreCase("ANA MATILDE")) {
				modelo.setModelo("ModeloLideranca");
			}else if (cobranca.getCondominio().equalsIgnoreCase("")) {
				modelo.setModelo("ModeloLideranca");
			}else if (cobranca.getCondominio().equalsIgnoreCase("FLAMBOYANT")) {
				modelo.setModelo("ModeloLideranca");
			}else if (cobranca.getCondominio().equalsIgnoreCase("DI BERNARDI TOWER")) {
				modelo.setModelo("ModeloLideranca");
			}else if (cobranca.getCondominio().equalsIgnoreCase("DONA MARTA")) {
				modelo.setModelo("ModeloLideranca");
			}else if (cobranca.getCondominio().equalsIgnoreCase("CARRERA")) {
				modelo.setModelo("ModeloLideranca");
			}else if (cobranca.getCondominio().equalsIgnoreCase("CARRERA")) {
				modelo.setModelo("ModeloLideranca");
			} else {
				if ((!cobranca.getCondominio().equalsIgnoreCase("PATIO DAS FLORES"))
					&& (!cobranca.getCondominio().equalsIgnoreCase("D/ART HOME DESIGN"))
						){
					modelo.setModelo("ModeloLideranca");
				}
				
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
			} else if (cobranca.getCondominio().equalsIgnoreCase("MALBEC")) {
				modelo.setModelo("ModeloGrupoEmbraconMalbec");
			} else if (cobranca.getCondominio().equalsIgnoreCase("PORTO REAL")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("STELLA MARIS")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("VILLA MILANO")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			}  else if (cobranca.getCondominio().equalsIgnoreCase("VILLENEUVE RESIDENCE")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("MARINA PARK")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("PARQUE ILHA DO ARVOREDO")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("Miguel Daux")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (cobranca.getCondominio().equalsIgnoreCase("MORADAS DO GARAPUVU")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("AÇORES")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("AMÉRICA OFFICENTER")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("CHARDONNAY")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (cobranca.getCondominio().equalsIgnoreCase("CITY OFFICE SQUARE")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (cobranca.getCondominio().equalsIgnoreCase("COMERCIAL MADISON CENTER")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("EPIROS PARK")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("FLAMBOYANT")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (cobranca.getCondominio().equalsIgnoreCase("GAIVOTA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("ILHA DE ITAPARICA")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("ILHA DO CORAL HOTEL RESIDENCE")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (cobranca.getCondominio().equalsIgnoreCase("ILHA DOURADA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("ISABELLA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("KALAMARI RESIDENCE")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (cobranca.getCondominio().equalsIgnoreCase("MAISON MARIA OLIVIA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (cobranca.getCondominio().equalsIgnoreCase("REAL TRINDADE")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("DONA DÉLCIA")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (cobranca.getCondominio().equalsIgnoreCase("PRIVILEGE")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			}else if (cobranca.getCondominio().equalsIgnoreCase("RECANTO DOS IMIGRANTES")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}
			
		}
		return modelo;
	}

}
