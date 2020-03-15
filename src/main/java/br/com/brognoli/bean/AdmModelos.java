package br.com.brognoli.bean;

import java.util.ArrayList;
import java.util.List;

import br.com.brognoli.model.Modelos;

public class AdmModelos {
	
	private List<Modelos> listaModelos;
	
	

	public AdmModelos() {
		listaModelos = new ArrayList<Modelos>();
		Modelos modelo = new Modelos();
		modelo.setAdministradora("Embraoffice Contabilidade");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		modelo = new Modelos();
		modelo.setAdministradora("Construtábil Contabilidade");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Pereira Jorge Contabilidade");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Plano Consult");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Villa Real Serviços Contábeis");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Plac Condomínios");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("ZC Contabilidade");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Dômina");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Construtábil Contabilidade");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Municipalis Assessoria Contábil");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Atico Contabilidade");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Plano Consult");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("CVR Condomínios");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("INOVA");
		modelo.setModelo("Resumo de rateio");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Sorrento");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("SUPORTE - CONDOMÍNIOS");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Sensato Continente");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Sensato Centro");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("REGGIO DI CALABRIA");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Liderança Administradora de Condomínios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Cetecol Adm. e Cont. de Condomínios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Gerencial Contabilidade");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Ilha Floripa Condominios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Adcon SC");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("OLICON ");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Dela Bruna & Sarda Contabilidade (DBS)");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Creditus Contabilidade e Condomínios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Zimmermann Condomínios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Cattoni Condomínios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Contato Contabilidade");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
		modelo = new Modelos();
		modelo.setAdministradora("Parceria Condomínios");
		modelo.setModelo("Resumo de rateio ponto");
		listaModelos.add(modelo);
		
	}

	public List<Modelos> getListaModelos() {
		return listaModelos;
	}

	public void setListaModelos(List<Modelos> listaModelos) {
		this.listaModelos = listaModelos;
	}
	
	public Modelos validarModelo(String administrador) {
		for (int i=0;i<listaModelos.size();i++) {
			if (listaModelos.get(i).getAdministradora().equalsIgnoreCase(administrador)) {
				return listaModelos.get(i);
			}
		}
		return null;
	}
	
	

}
