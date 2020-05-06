package br.com.brognoli.bean;

import br.com.brognoli.model.Modelos;

public class RegraCondominio {
	
	public Modelos retornaModelo(Modelos modelo, String condominio) {
		if (modelo.getAdministradora().equalsIgnoreCase("Villa Real Serviços Contábeis")) {
			if (condominio.equalsIgnoreCase("Áurea")) {
				modelo.setModelo("ModeloResumoRateioUmaColuna");
			} else if (condominio.equalsIgnoreCase("San Marino")) {
				modelo.setModelo("ModeloResumoRateioUmaColuna");
			}
		}
		
		if (modelo.getAdministradora().equalsIgnoreCase("Liderança Administradora de Condomínios")) {
			if (condominio.equalsIgnoreCase("ALPHACENTAURI")) {
				modelo.setModelo("ModeloLideranca");
			} else if (condominio.equalsIgnoreCase("ANA MATILDE")) {
				modelo.setModelo("ModeloLideranca");
			}else if (condominio.equalsIgnoreCase("")) {
				modelo.setModelo("ModeloLideranca");
			}else if (condominio.equalsIgnoreCase("FLAMBOYANT")) {
				modelo.setModelo("ModeloLideranca");
			}else if (condominio.equalsIgnoreCase("DI BERNARDI TOWER")) {
				modelo.setModelo("ModeloLideranca");
			}else if (condominio.equalsIgnoreCase("DONA MARTA")) {
				modelo.setModelo("ModeloLideranca");
			}else if (condominio.equalsIgnoreCase("CARRERA")) {
				modelo.setModelo("ModeloLideranca");
			}else if (condominio.equalsIgnoreCase("CARRERA")) {
				modelo.setModelo("ModeloLideranca");
			} else {
				if ((!condominio.equalsIgnoreCase("PATIO DAS FLORES"))
					&& (!condominio.equalsIgnoreCase("D/ART HOME DESIGN"))
						){
					modelo.setModelo("ModeloLideranca");
				}
				
			}
		}
		if (modelo.getAdministradora().equalsIgnoreCase("Grupo Embracon")) {
			if (condominio.equalsIgnoreCase("CONJUNTO MARIA ZITA")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("ALBATROZ")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("MONT REAL")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("ILHA DE ITAPARICA")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("NAIR HEIDERSCHEIDT")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			}  else if (condominio.equalsIgnoreCase("ÁGUAS DO BALNEÁRIO")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("BOULEVARD DU NORD")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("HARMONY")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("KALAMARI RESIDENCE")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("RECANTO DO HORIZONTE")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("MALBEC")) {
				modelo.setModelo("ModeloGrupoEmbraconMalbec");
			} else if (condominio.equalsIgnoreCase("PORTO REAL")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("STELLA MARIS")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("VILLA MILANO")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			}  else if (condominio.equalsIgnoreCase("VILLENEUVE RESIDENCE")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("MARINA PARK")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("PARQUE ILHA DO ARVOREDO")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("Miguel Daux")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			} else if (condominio.equalsIgnoreCase("MORADAS DO GARAPUVU")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("AÇORES")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("AMÉRICA OFFICENTER")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("CHARDONNAY")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (condominio.equalsIgnoreCase("CITY OFFICE SQUARE")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (condominio.equalsIgnoreCase("COMERCIAL MADISON CENTER")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("EPIROS PARK")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("FLAMBOYANT")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (condominio.equalsIgnoreCase("GAIVOTA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("ILHA DE ITAPARICA")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("ILHA DO CORAL HOTEL RESIDENCE")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (condominio.equalsIgnoreCase("ILHA DOURADA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("ISABELLA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("KALAMARI RESIDENCE")) {
				modelo.setModelo("Resumo Grupo Embracon");
			} else if (condominio.equalsIgnoreCase("MAISON MARIA OLIVIA")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}else if (condominio.equalsIgnoreCase("REAL TRINDADE")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("DONA DÉLCIA")) {
				modelo.setModelo("ModeloGrupoEmbraconCodigoBarras");
			} else if (condominio.equalsIgnoreCase("PRIVILEGE")) {
				modelo.setModelo("ModeloGrupoEmbraconAposVencimento");
			}else if (condominio.equalsIgnoreCase("RECANTO DOS IMIGRANTES")) {
				modelo.setModelo("Resumo Grupo Embracon");
			}
			
		}
		if (modelo.getAdministradora().equalsIgnoreCase("DUPLIQUE FLORIANOPOLIS")) {
			if (condominio.equalsIgnoreCase("BAIRRO DE FÁTIMA")) {
				modelo.setModelo("Resumo Duplic2");
			}
		}
		return modelo;
	}

}
