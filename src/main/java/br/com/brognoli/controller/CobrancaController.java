package br.com.brognoli.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import br.com.brognoli.bean.AdmModelos;
import br.com.brognoli.bean.ExportarExcel;
import br.com.brognoli.bean.ModeloAdelante;
import br.com.brognoli.bean.ModeloGrupoEmbracon;
import br.com.brognoli.bean.ModeloGrupoEmbraconAposVencimento;
import br.com.brognoli.bean.ModeloGrupoEmbraconCodigoBarras;
import br.com.brognoli.bean.ModeloNovara;
import br.com.brognoli.bean.ModeloResumoRateio;
import br.com.brognoli.bean.ModeloResumoRateioPonto;
import br.com.brognoli.bean.ModeloResumoRateioUmaColuna;
import br.com.brognoli.bean.RegraCondominio;
import br.com.brognoli.model.Boletos;
import br.com.brognoli.model.Cobranca;
import br.com.brognoli.model.Cobrancaarquivo;
import br.com.brognoli.model.Filtros;
import br.com.brognoli.model.Modelos;
import br.com.brognoli.model.Periodo;
import br.com.brognoli.model.Query;
import br.com.brognoli.repository.CobrancaArquivoRepository;
import br.com.brognoli.repository.CobrancaRepository;


@SuppressWarnings("unused")
@CrossOrigin
@RestController
@RequestMapping("/cobrancas")
public class CobrancaController {
	
	@Autowired
	private CobrancaRepository cobrancaRepository;
	@Autowired
	private CobrancaArquivoRepository cobrancaArquivoRepository;
	
	@PostMapping("/salvar")
	@ResponseStatus(HttpStatus.CREATED)
	public Cobranca salvar(@Valid @RequestBody Cobranca acesso) {
		return cobrancaRepository.save(acesso);
	}
	
	@GetMapping("/gerarpdf")
	@ResponseStatus(HttpStatus.CREATED)
	public String salvarPDF() {
		int importados = 0;
		List<Cobranca> listaCobranca = new ArrayList<Cobranca>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("c:/logs/winker.txt"));
			Gson gson = new Gson();// GsonBuilder().setFieldNamingStrategy(strategy).create();
			final Type type = new TypeToken<List<Cobranca>>() {
			}.getType();
			listaCobranca = gson.fromJson(br, type);
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (listaCobranca != null) {
			final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
			// O CookieManager vai gerenciar os dados da sessão
			CookieManager cookieMan = new CookieManager();
			cookieMan = webClient.getCookieManager();
			cookieMan.setCookiesEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setTimeout(10000000);
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
			HtmlPage pagina = null;
			try {
				pagina = webClient.getPage("https://app.winker.com.br/intra/default/login?ref=topo");
			} catch (FailingHttpStatusCodeException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			List<HtmlForm> formularios = pagina.getForms();
			HtmlForm formulario = formularios.get(0);
			formulario.getInputByName("LoginForm[username]").setValueAttribute("assfinanceiro7@brognoli.com.br");
			formulario.getInputByName("LoginForm[password]").setValueAttribute("brognoli123");
			HtmlInput button = formulario.getInputByValue("Entrar");
			try {
				button.click();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (Cobranca cobranca : listaCobranca) {
				Cobranca cobrancaGravada = null;
				System.out.println(cobranca.getId_unidade());
				if ((cobranca.getId_unidade()!=null) && (cobranca.getData_ref_rateio()!=null)) {
					cobrancaGravada = cobrancaRepository.getCobranca(cobranca.getId_unidade(), cobranca.getData_ref_rateio());
				}else {
					System.out.println("NULO");
				}
				if (cobrancaGravada!=null) {
					cobranca = updateCobranca(cobranca, cobrancaGravada);
				} else {
					cobranca = cobrancaRepository.save(cobranca);
				}
				boolean gPDF = false;
				String url ="";
					String fileName = cobranca.getCondominio() + "_" + cobranca.getUnidade() + "_"
							+ cobranca.getReferencia() + ".pdf"; 
					fileName = fileName.replace("/", " ");
					fileName = "c:\\logs\\pdf\\" + fileName;
					File file = new File(fileName);
					if (cobranca.getId_unidade_cobranca() != null) {
						url = "https://app.winker.com.br/intra/meuCondominio/boleto/view/id/"
							+ cobranca.getId_unidade_cobranca();
						gPDF =true;
					} else if (cobranca.getUrl_arquivo_cobranca() != null) {
						url = cobranca.getUrl_arquivo_cobranca();
						gPDF = true;
					}
					if (gPDF) {
					Cobrancaarquivo arquivo = cobrancaArquivoRepository.getArquivo(cobranca.getIdcobranca());	
					if (arquivo == null) {
						arquivo = new Cobrancaarquivo();
					}
					arquivo.setCobranca(cobranca);
					arquivo.setDatagravacao(new Date());;
					arquivo.setNomearquivo(fileName);
					arquivo = cobrancaArquivoRepository.save(arquivo);
					InputStream is;
					try {
						is = webClient.getPage(url).getWebResponse().getContentAsStream();
						OutputStream out = new FileOutputStream(file);
						byte[] buffer = new byte[8 * 1024];
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							out.write(buffer, 0, bytesRead);
						}
						out.close();
				
						
					} catch (FailingHttpStatusCodeException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					importados++;
				}
			}
			
			System.out.println("PDF Salvos : " + String.valueOf(importados));
			System.out.println("PDF não Encontrados : " + String.valueOf(listaCobranca.size() - importados));
			
		}
		
		return "ok";
	}
	
	public Cobranca updateCobranca(Cobranca atual, Cobranca gravada) {
		
		gravada.setId_rateio(atual.getId_rateio());
		gravada.setData_vencimento(atual.getData_vencimento());
		gravada.setValor_total_cobranca(atual.getValor_total_cobranca());
		gravada.setCondominio(atual.getCondominio());
		gravada.setData_pagamento(atual.getData_pagamento());
		gravada.setId_unidade(atual.getId_unidade());
		gravada.setData_ref_rateio(atual.getData_ref_rateio());
		gravada.setSerie_rateio(atual.getSerie_rateio());
		gravada.setValor_fundo_reserva(atual.getValor_fundo_reserva());
		gravada.setData_cancelamento(atual.getData_cancelamento());
		gravada.setUrl_arquivo_cobranca(atual.getUrl_arquivo_cobranca());
		gravada.setReferencia(atual.getReferencia());
		gravada.setId(atual.getId());
		gravada.setAdministradora(atual.getAdministradora());
		gravada.setId_condominio(atual.getId_condominio());
		gravada.setBairro(atual.getBairro());
		gravada.setCidade(atual.getCidade());
		gravada.setUf(atual.getUf());
		gravada.setCnpj(atual.getCnpj());
		gravada.setUnidade(atual.getUnidade());
		gravada.setId_unidade_cobranca(atual.getId_unidade_cobranca());
		gravada.setCondominio_ativo(atual.getCondominio_ativo());
		gravada.setSegunda_via_habilitada(atual.isSegunda_via_habilitada());
		gravada.setCobranca_disponivel(atual.getCobranca_disponivel());
		gravada.setPossui_segunda_via_habilitada(atual.getPossui_segunda_via_habilitada());
		gravada.setHasRateio(atual.getHasRateio());
		gravada.setHasCobranca(atual.getHasCobranca());
		gravada.setVencido(atual.isVencido());
		gravada.setDataCadastroWinker(atual.getDataCadastroWinker());
		gravada.setSituacao(atual.getSituacao());
		return cobrancaRepository.save(gravada);
	}
	
	
	@GetMapping("/gerarexcel")
	@ResponseStatus(HttpStatus.CREATED)
	public String exportarExcel() {
		int importados = 0;
		//List<Cobranca> listaCobranca = cobrancaRepository.findAll();
		List<Cobranca> listaCobranca = cobrancaRepository.listarCobranca("Grupo Embracon", "ALBATROZ");
		if (listaCobranca != null) {
		List<Boletos> listaBoletos = new ArrayList<Boletos>();
		for (Cobranca cobranca : listaCobranca) {
			System.out.println(cobranca.toString());
			boolean gPDF = false;
			Cobrancaarquivo arquivo = cobrancaArquivoRepository.getArquivo(cobranca.getIdcobranca());
			String fileName = "";
			File file = null;
			if (arquivo!=null) {
				fileName = arquivo.getNomearquivo();
				file = new File(fileName);
					gPDF =true;
			}
			if (gPDF) {
			AdmModelos admModelos = new AdmModelos();
			Modelos modelo = admModelos.validarModelo(cobranca.getAdministradora());
			if (modelo !=null) {
				if(cobranca.getIdcobranca()==2242) {
					int i=0;
				}
				RegraCondominio regraCondominio = new RegraCondominio();
				modelo = regraCondominio.retornaModelo(modelo, cobranca);
				Boletos boleto = new Boletos();
				boleto.setCobranca(cobranca);
				if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio")) {
					ModeloResumoRateio modeloResumoRateio = new ModeloResumoRateio();
					try {
						boleto.setListaResumo(modeloResumoRateio.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloResumoRateio.getLinhaDigitavel());
					boleto.setEndereco(modeloResumoRateio.getEndereco());
					boleto.setNumero(modeloResumoRateio.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio ponto")) {
					ModeloResumoRateioPonto modeloResumoRateioPonto = new ModeloResumoRateioPonto();
					try {
						boleto.setListaResumo(modeloResumoRateioPonto.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloResumoRateioPonto.getLinhaDigitavel());
					boleto.setEndereco(modeloResumoRateioPonto.getEndereco());
					boleto.setNumero(modeloResumoRateioPonto.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("ModeloResumoRateioUmaColuna")) {
					ModeloResumoRateioUmaColuna modeloResumoRateioPontoUmaColuna = new ModeloResumoRateioUmaColuna();
					try {
						boleto.setListaResumo(modeloResumoRateioPontoUmaColuna.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloResumoRateioPontoUmaColuna.getLinhaDigitavel());
					boleto.setEndereco(modeloResumoRateioPontoUmaColuna.getEndereco());
					boleto.setNumero(modeloResumoRateioPontoUmaColuna.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("Resumo Adelante")) {
					ModeloAdelante modeloAdelante = new ModeloAdelante();
					try {
						boleto.setListaResumo(modeloAdelante.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloAdelante.getLinhaDigitavel());
					boleto.setEndereco(modeloAdelante.getEndereco());
					boleto.setNumero(modeloAdelante.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("Resumo Correta")) {
					ModeloAdelante modeloAdelante = new ModeloAdelante();
					try {
						boleto.setListaResumo(modeloAdelante.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloAdelante.getLinhaDigitavel());
					boleto.setEndereco(modeloAdelante.getEndereco());
					boleto.setNumero(modeloAdelante.getNumero());
				}  else if (modelo.getModelo().equalsIgnoreCase("Resumo Novara")) {
					ModeloNovara modeloNovara = new ModeloNovara();
					try {
						boleto.setListaResumo(modeloNovara.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloNovara.getLinhaDigitavel());
					boleto.setEndereco(modeloNovara.getEndereco());
					boleto.setNumero(modeloNovara.getNumero());
				}  else if (modelo.getModelo().equalsIgnoreCase("Resumo Grupo Embracon")) {
					ModeloGrupoEmbracon modeloGrupoEmbracon = new ModeloGrupoEmbracon();
					try {
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
					boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
					boleto.setNumero(modeloGrupoEmbracon.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconAposVencimento")) {
					ModeloGrupoEmbraconAposVencimento modeloGrupoEmbracon = new ModeloGrupoEmbraconAposVencimento();
					try {
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
					boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
					boleto.setNumero(modeloGrupoEmbracon.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconCodigoBarras")) {
					ModeloGrupoEmbraconCodigoBarras modeloGrupoEmbracon = new ModeloGrupoEmbraconCodigoBarras();
					try {
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
					boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
					boleto.setNumero(modeloGrupoEmbracon.getNumero());
				}    
				listaBoletos.add(boleto);
			}
			}
			
		}
		if (listaBoletos!=null) {
			if (listaBoletos.size()>0) {
				ExportarExcel ex = new ExportarExcel();
				ex.gerar(listaBoletos);
			}
		}
					
		}
		return "ok";
	}
	

}
