package br.com.brognoli.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.brognoli.bean.AdmModelos;
import br.com.brognoli.bean.Diretorios;
import br.com.brognoli.bean.ExportarExcel;
import br.com.brognoli.bean.ModeloAdelante;
import br.com.brognoli.bean.ModeloBRCONDOS;
import br.com.brognoli.bean.ModeloCNeto;
import br.com.brognoli.bean.ModeloControll;
import br.com.brognoli.bean.ModeloDuo;
import br.com.brognoli.bean.ModeloDuplic2;
import br.com.brognoli.bean.ModeloDuplique;
import br.com.brognoli.bean.ModeloExato;
import br.com.brognoli.bean.ModeloExato2;
import br.com.brognoli.bean.ModeloFama;
import br.com.brognoli.bean.ModeloFeltrin;
import br.com.brognoli.bean.ModeloGi;
import br.com.brognoli.bean.ModeloGrupoEmbracon;
import br.com.brognoli.bean.ModeloGrupoEmbraconAposVencimento;
import br.com.brognoli.bean.ModeloGrupoEmbraconCodigoBarras;
import br.com.brognoli.bean.ModeloGrupoEmbraconMalbec;
import br.com.brognoli.bean.ModeloLideranca;
import br.com.brognoli.bean.ModeloMaxima;
import br.com.brognoli.bean.ModeloNovara;
import br.com.brognoli.bean.ModeloPontual;
import br.com.brognoli.bean.ModeloResumoRateio;
import br.com.brognoli.bean.ModeloResumoRateioPonto;
import br.com.brognoli.bean.ModeloResumoRateioUmaColuna;
import br.com.brognoli.bean.RegraCondominio;
import br.com.brognoli.model.Boletos;
import br.com.brognoli.model.Imoveladm;
import br.com.brognoli.model.Modelos;
import br.com.brognoli.repository.ImovelAdmRepository;
import br.com.brognoli.service.S3Service;

@CrossOrigin("*")
@RestController
@RequestMapping("/gtboletos")
public class BoletosGarantiaTotalController {

	@Autowired
	private ImovelAdmRepository imovelAdmRepository;
	@Autowired
	private S3Service s3Service;

	@GetMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String teste() {
		return "ok";
	}

	@PostMapping("/teste")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> teste2() {
		return ResponseEntity.ok("ok");
	}
	
	@PostMapping("/gerarexcel")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> exportarExcel(@Valid @RequestBody Diretorios diretorios) {
		//String caminhoDir = diretorios.getCaminho();
		//caminhoDir = caminhoDir.replace("@", "\\");
		//caminhoDir = caminhoDir + "\\";
		///File fileDir = new File(caminhoDir);
		///int encontrado = 0;
	
		/*
		 * String arquivos[] = fileDir.list(); List<String> listaAdm = new
		 * ArrayList<String>(); if (arquivos != null) { List<Boletos> listaBoletos = new
		 * ArrayList<Boletos>(); String codigoImovel = null; for (int
		 * i=0;i<arquivos.length;i++) { boolean gPDF = false; String fileName = ""; File
		 * file = null; fileName = caminhoDir + arquivos[i]; file = new File(fileName);
		 * codigoImovel = getCodgioImovel(file.getName()); gPDF =true; if
		 * (codigoImovel==null) { gPDF =false; } if (gPDF) { Imoveladm imovelAdm = new
		 * Imoveladm(); imovelAdm =
		 * imovelAdmRepository.getImovel(Integer.parseInt(codigoImovel)); AdmModelos
		 * admModelos = new AdmModelos(); Modelos modelo; if (imovelAdm==null) { modelo
		 * = null; }else { modelo = admModelos.validarModelo(imovelAdm.getAdmwinker());
		 * 
		 * if (modelo!=null) { //file.delete(); //modelo = null; encontrado++; if
		 * (!modelo.getAdministradora().equalsIgnoreCase("GI-GESTÃO")) { modelo = null;
		 * } }
		 * 
		 * } if (modelo !=null) { encontrado++;
		 * System.out.println(modelo.getAdministradora() + " - " + file.getName());
		 * RegraCondominio regraCondominio = new RegraCondominio(); if
		 * (imovelAdm.getEdificio()!= null) { modelo =
		 * regraCondominio.retornaModelo(modelo, imovelAdm.getEdificio()); } Boletos
		 * boleto = new Boletos(); boleto.setCobranca(null);
		 * boleto.setImovelAdm(imovelAdm); if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio")) {
		 * ModeloResumoRateio modeloResumoRateio = new ModeloResumoRateio(); try {
		 * boleto.setListaResumo(modeloResumoRateio.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloResumoRateio.getLinhaDigitavel());
		 * boleto.setEndereco(modeloResumoRateio.getEndereco());
		 * boleto.setNumero(modeloResumoRateio.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio ponto")) {
		 * ModeloResumoRateioPonto modeloResumoRateioPonto = new
		 * ModeloResumoRateioPonto(); try {
		 * boleto.setListaResumo(modeloResumoRateioPonto.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloResumoRateioPonto.getLinhaDigitavel());
		 * boleto.setEndereco(modeloResumoRateioPonto.getEndereco());
		 * boleto.setNumero(modeloResumoRateioPonto.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("ModeloResumoRateioUmaColuna")) {
		 * ModeloResumoRateioUmaColuna modeloResumoRateioPontoUmaColuna = new
		 * ModeloResumoRateioUmaColuna(); try {
		 * boleto.setListaResumo(modeloResumoRateioPontoUmaColuna.gerarTXT(fileName)); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloResumoRateioPontoUmaColuna.getLinhaDigitavel()
		 * ); boleto.setEndereco(modeloResumoRateioPontoUmaColuna.getEndereco());
		 * boleto.setNumero(modeloResumoRateioPontoUmaColuna.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo Adelante")) { ModeloAdelante
		 * modeloAdelante = new ModeloAdelante(); try {
		 * boleto.setListaResumo(modeloAdelante.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloAdelante.getLinhaDigitavel());
		 * boleto.setEndereco(modeloAdelante.getEndereco());
		 * boleto.setNumero(modeloAdelante.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo Correta")) { ModeloAdelante
		 * modeloAdelante = new ModeloAdelante(); try {
		 * boleto.setListaResumo(modeloAdelante.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloAdelante.getLinhaDigitavel());
		 * boleto.setEndereco(modeloAdelante.getEndereco());
		 * boleto.setNumero(modeloAdelante.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo Novara")) { ModeloNovara
		 * modeloNovara = new ModeloNovara(); try {
		 * boleto.setListaResumo(modeloNovara.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloNovara.getLinhaDigitavel());
		 * boleto.setEndereco(modeloNovara.getEndereco());
		 * boleto.setNumero(modeloNovara.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo Grupo Embracon")) {
		 * ModeloGrupoEmbracon modeloGrupoEmbracon = new ModeloGrupoEmbracon(); try {
		 * boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
		 * boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
		 * boleto.setNumero(modeloGrupoEmbracon.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconAposVencimento")) {
		 * ModeloGrupoEmbraconAposVencimento modeloGrupoEmbracon = new
		 * ModeloGrupoEmbraconAposVencimento(); try {
		 * boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
		 * boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
		 * boleto.setNumero(modeloGrupoEmbracon.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconCodigoBarras")) {
		 * ModeloGrupoEmbraconCodigoBarras modeloGrupoEmbracon = new
		 * ModeloGrupoEmbraconCodigoBarras(); try {
		 * boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
		 * boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
		 * boleto.setNumero(modeloGrupoEmbracon.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconMalbec")) {
		 * ModeloGrupoEmbraconMalbec modeloGrupoEmbracon = new
		 * ModeloGrupoEmbraconMalbec(); try {
		 * boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
		 * boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
		 * boleto.setNumero(modeloGrupoEmbracon.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("ModeloLideranca")) { ModeloLideranca
		 * modeloLideranca = new ModeloLideranca(); try {
		 * boleto.setListaResumo(modeloLideranca.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloLideranca.getLinhaDigitavel());
		 * boleto.setEndereco(modeloLideranca.getEndereco());
		 * boleto.setNumero(modeloLideranca.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Duplique")) {
		 * ModeloDuplique modeloDuplique = new ModeloDuplique(); try {
		 * boleto.setListaResumo(modeloDuplique.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloDuplique.getLinhaDigitavel());
		 * boleto.setEndereco(modeloDuplique.getEndereco());
		 * boleto.setNumero(modeloDuplique.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Exato")) { ModeloExato
		 * modeloExato = new ModeloExato(); try {
		 * boleto.setListaResumo(modeloExato.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloExato.getLinhaDigitavel());
		 * boleto.setEndereco(modeloExato.getEndereco());
		 * boleto.setNumero(modeloExato.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Pontual")) {
		 * ModeloPontual modeloPontual = new ModeloPontual(); try {
		 * boleto.setListaResumo(modeloPontual.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloPontual.getLinhaDigitavel());
		 * boleto.setEndereco(modeloPontual.getEndereco());
		 * boleto.setNumero(modeloPontual.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Constâncio Neto")) {
		 * ModeloCNeto modeloCNeto = new ModeloCNeto(); try {
		 * boleto.setListaResumo(modeloCNeto.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloCNeto.getLinhaDigitavel());
		 * boleto.setEndereco(modeloCNeto.getEndereco());
		 * boleto.setNumero(modeloCNeto.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Exato2")) {
		 * ModeloExato2 modeloExato = new ModeloExato2(); try {
		 * boleto.setListaResumo(modeloExato.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloExato.getLinhaDigitavel());
		 * boleto.setEndereco(modeloExato.getEndereco());
		 * boleto.setNumero(modeloExato.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo Duplic2")) { ModeloDuplic2
		 * modeloDuplic2 = new ModeloDuplic2(); try {
		 * boleto.setListaResumo(modeloDuplic2.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloDuplic2.getLinhaDigitavel());
		 * boleto.setEndereco(modeloDuplic2.getEndereco());
		 * boleto.setNumero(modeloDuplic2.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Maxima")) {
		 * ModeloMaxima modeloMaxima = new ModeloMaxima(); try {
		 * boleto.setListaResumo(modeloMaxima.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloMaxima.getLinhaDigitavel());
		 * boleto.setEndereco(modeloMaxima.getEndereco());
		 * boleto.setNumero(modeloMaxima.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Feltrin")) {
		 * ModeloFeltrin modeloFeltrin = new ModeloFeltrin(); try {
		 * boleto.setListaResumo(modeloFeltrin.gerarTXT(fileName)); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloFeltrin.getLinhaDigitavel());
		 * boleto.setEndereco(modeloFeltrin.getEndereco());
		 * boleto.setNumero(modeloFeltrin.getNumero()); } else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio BRCONDOS")) {
		 * ModeloBRCONDOS modeloBRCONDOS = new ModeloBRCONDOS(); try {
		 * boleto.setListaResumo(modeloBRCONDOS.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloBRCONDOS.getLinhaDigitavel());
		 * boleto.setEndereco(modeloBRCONDOS.getEndereco());
		 * boleto.setNumero(modeloBRCONDOS.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio CONTROLL")) {
		 * ModeloControll modeloControll = new ModeloControll(); try {
		 * boleto.setListaResumo(modeloControll.gerarTXT(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloControll.getLinhaDigitavel());
		 * boleto.setEndereco(modeloControll.getEndereco());
		 * boleto.setNumero(modeloControll.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio DUO SERVICOS1")) {
		 * ModeloDuo modeloDuo = new ModeloDuo(); try {
		 * boleto.setListaResumo(modeloDuo.gerarTXT(fileName)); } catch (IOException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloDuo.getLinhaDigitavel());
		 * boleto.setEndereco(modeloDuo.getEndereco());
		 * boleto.setNumero(modeloDuo.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Fama")) { ModeloFama
		 * modeloFama = new ModeloFama(); try {
		 * boleto.setListaResumo(modeloFama.gerarTXT(fileName)); } catch (IOException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloFama.getLinhaDigitavel());
		 * boleto.setEndereco(modeloFama.getEndereco());
		 * boleto.setNumero(modeloFama.getNumero()); }else if
		 * (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Gi")) { ModeloGi
		 * modeloGi = new ModeloGi(); try {
		 * boleto.setListaResumo(modeloGi.gerarTXT(fileName)); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 * boleto.setLinhaDigitavel(modeloGi.getLinhaDigitavel());
		 * boleto.setEndereco(modeloGi.getEndereco());
		 * boleto.setNumero(modeloGi.getNumero()); } listaBoletos.add(boleto); } }
		 * System.out.println(i); } System.out.println(encontrado);
		 * 
		 * if (listaBoletos!=null) { if (listaBoletos.size()>0) { ExportarExcel ex = new
		 * ExportarExcel(); ex.gerarGT(listaBoletos); File file =ex.getFile(); URI uri =
		 * s3Service.uploadFile(file); return ResponseEntity.created(uri).build(); } }
		 * 
		 * 
		 * }
		 */
		return ResponseEntity.ok("ok");
	}

	public String getCodgioImovel(String nome) {
		String novoNome = "";
		boolean pegarnome = false;
		if (nome.length() > 12) {
			for (int i = 3; i < nome.length(); i++) {
				if (nome.charAt(i) != '_') {
					novoNome = novoNome + nome.charAt(i);
				} else {
					i = 100;
				}
			}
		} else {
			for (int i = 3; i < nome.length(); i++) {
				if (nome.charAt(i) != '.') {
					novoNome = novoNome + nome.charAt(i);
				} else {
					i = 100;
				}
			}
		}
		if (novoNome.length() > 0) {
			novoNome = novoNome.replace(" ", "");
		}
		return novoNome;
	}

}
