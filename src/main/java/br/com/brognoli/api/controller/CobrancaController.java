package br.com.brognoli.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.brognoli.api.bean.AdmModelos;
import br.com.brognoli.api.bean.Cobrancaresultado;
import br.com.brognoli.api.bean.ExportarExcel;
import br.com.brognoli.api.bean.ModeloAdelante;
import br.com.brognoli.api.bean.ModeloGrupoEmbracon;
import br.com.brognoli.api.bean.ModeloGrupoEmbraconAposVencimento;
import br.com.brognoli.api.bean.ModeloGrupoEmbraconCodigoBarras;
import br.com.brognoli.api.bean.ModeloGrupoEmbraconMalbec;
import br.com.brognoli.api.bean.ModeloLideranca;
import br.com.brognoli.api.bean.ModeloNovara;
import br.com.brognoli.api.bean.ModeloResumoRateio;
import br.com.brognoli.api.bean.ModeloResumoRateioPonto;
import br.com.brognoli.api.bean.ModeloResumoRateioUmaColuna;
import br.com.brognoli.api.bean.RegraCondominio;
import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.Cobranca;
import br.com.brognoli.api.model.Cobrancaarquivo;
import br.com.brognoli.api.model.Modelos;
import br.com.brognoli.api.repository.CobrancaArquivoRepository;
import br.com.brognoli.api.repository.CobrancaRepository;
import br.com.brognoli.api.service.S3Service;
import br.com.brognoli.api.util.Conversor;



@SuppressWarnings("unused")
@CrossOrigin
@RestController
@RequestMapping("/cobrancas")
public class CobrancaController {
	
	@Autowired
	private CobrancaRepository cobrancaRepository;
	@Autowired
	private CobrancaArquivoRepository cobrancaArquivoRepository;
	private List<Cobrancaresultado> listaCobrancaResultado;
	@Autowired
	private S3Service s3Service;
	private String caminhoDir="\\\\192.168.1.58\\documentos\\centralfinanceira\\BOLETOS DE CONDOMÍNIOS\\Winker\\"; 
			//"c:\\logs\\winker\\"; 
	
	
	@PostMapping("/salvar")
	@ResponseStatus(HttpStatus.CREATED)
	public Cobranca salvar(@Valid @RequestBody Cobranca acesso) {
		return cobrancaRepository.save(acesso);
	}
	
	@GetMapping("/getlista")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Cobrancaresultado>> getListarCobranca() {
		return ResponseEntity.ok(listaCobrancaResultado);
	}
	
	
	
	@PostMapping("/gerarlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void listarCobranca(@RequestParam(name="file") MultipartFile file) {
		listaCobrancaResultado = new ArrayList<Cobrancaresultado>();
		List<Cobranca> listaCobranca = new ArrayList<Cobranca>();
		BufferedReader br = null;
		try {
			InputStream is = file.getInputStream();
			Reader reader = new InputStreamReader(is);
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			Gson gson = new Gson();// GsonBuilder().setFieldNamingStrategy(strategy).create();
			final Type type = new TypeToken<List<Cobranca>>() {
			}.getType();
			listaCobranca = gson.fromJson(br, type);
			br.close();
			for (Cobranca cobranca : listaCobranca) {
				Cobrancaresultado cr = new Cobrancaresultado();
				cr.setCobranca(cobranca);
				cr.setCapturou(false);
				listaCobrancaResultado.add(cr);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping("/gerarpdf")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Cobrancaresultado>> salvarPDF() {
		int importados = 0;
		if (listaCobrancaResultado != null) {
			Conversor c = new Conversor();
			String mesAno = c.getMesAno(listaCobrancaResultado.get(0).getCobranca().getData_vencimento());
			String dir = caminhoDir +  mesAno;
			File dirFile = new File(dir);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
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
			for (int i = 0;i<listaCobrancaResultado.size();i++) {
				Cobranca cobranca = listaCobrancaResultado.get(i).getCobranca();
				Cobranca cobrancaGravada = null;
				if ((cobranca.getId_unidade()!=null) && (cobranca.getData_ref_rateio()!=null)) {
					cobrancaGravada = cobrancaRepository.getCobranca(cobranca.getId_unidade(), cobranca.getData_ref_rateio());
				}if (cobrancaGravada!=null) {
					cobranca = updateCobranca(cobranca, cobrancaGravada);
				} else {
					cobranca = cobrancaRepository.save(cobranca);
				}
				boolean gPDF = false;
				String url ="";
					String fileName = cobranca.getCondominio() + "_" + cobranca.getUnidade() + "_"
							+ cobranca.getReferencia() + ".pdf"; 
					fileName = fileName.replace("/", " ");
					fileName = caminhoDir + mesAno  + "\\" + fileName;
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
						List<Cobrancaarquivo> listaTemp = cobrancaArquivoRepository.getArquivo(cobranca.getIdcobranca());
						Cobrancaarquivo arquivo = null;
						if (listaTemp !=null) {
							if (listaTemp.size()>0) {
								arquivo =  listaTemp.get(0);
								if (listaTemp.size()>1) {
									cobrancaArquivoRepository.delete(listaTemp.get(1));
								}
							}
						}
						if (arquivo == null) {
							arquivo = new Cobrancaarquivo();
						}
						arquivo.setCobranca(cobranca);
						arquivo.setDatagravacao(new Date());
						fileName = fileName.replace(".pdf", "");
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
						listaCobrancaResultado.get(i).setCapturou(true);
						importados++;
					}else {
						listaCobrancaResultado.get(i).setCapturou(false);
					}
				}	
			}
		System.out.println("Terminou");
		return ResponseEntity.ok(listaCobrancaResultado);

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
	
	
	
	
	@GetMapping("/gerarexcel/{mes}/{ano}/{administradora}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> exportarExcel(@PathVariable("mes") String mes, @PathVariable("ano") String ano, @PathVariable("administradora") String administradora, HttpServletResponse response) {
		if (administradora.equalsIgnoreCase("@")) {
			administradora= "";
		}
		int importados = 0;
		//List<Cobranca> listaCobranca = cobrancaRepository.findAll();
		List<Cobrancaarquivo> listaCobrancaArquivo = cobrancaArquivoRepository.getArquivos(administradora, mes + "/" + ano);
		if (listaCobrancaArquivo != null) {
		List<Boletos> listaBoletos = new ArrayList<Boletos>();
		for (Cobrancaarquivo arquivo : listaCobrancaArquivo) {
			boolean gPDF = false;
			String fileName = "";
			File file = null;
			if (arquivo!=null) {
				fileName = arquivo.getNomearquivo();
				file = new File(fileName);
					gPDF =true;
			}
			if (gPDF) {
				System.out.println(file.getName());
			AdmModelos admModelos = new AdmModelos();
			Modelos modelo = admModelos.validarModelo(arquivo.getCobranca().getAdministradora());
			if (modelo !=null) {
				RegraCondominio regraCondominio = new RegraCondominio();
				modelo = regraCondominio.retornaModelo(modelo, arquivo.getCobranca().getCondominio());
				Boletos boleto = new Boletos();
				boleto.setCobranca(arquivo.getCobranca());
				if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio")) {
					ModeloResumoRateio modeloResumoRateio = new ModeloResumoRateio();
					try {
						boleto.setListaResumo(modeloResumoRateio.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloResumoRateioPonto.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloResumoRateioPontoUmaColuna.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloAdelante.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloAdelante.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloNovara.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, null));
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
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, null));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
					boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
					boleto.setNumero(modeloGrupoEmbracon.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconMalbec")) {
					ModeloGrupoEmbraconMalbec modeloGrupoEmbracon = new ModeloGrupoEmbraconMalbec();
					try {
						boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, null));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
					boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
					boleto.setNumero(modeloGrupoEmbracon.getNumero());
				} else if (modelo.getModelo().equalsIgnoreCase("ModeloLideranca")) {
					ModeloLideranca modeloLideranca = new ModeloLideranca();
					try {
						boleto.setListaResumo(modeloLideranca.gerarTXT(fileName, null));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boleto.setLinhaDigitavel(modeloLideranca.getLinhaDigitavel());
					boleto.setEndereco(modeloLideranca.getEndereco());
					boleto.setNumero(modeloLideranca.getNumero());
				}         
				listaBoletos.add(boleto);
			}
			}
			
		}
		if (listaBoletos!=null) {
			if (listaBoletos.size()>0) {
				ExportarExcel ex = new ExportarExcel();
				ex.gerarWinker(listaBoletos, mes + "_" + ano);
				File file =ex.getFile();
				URI uri = s3Service.uploadFile(file);
				return ResponseEntity.created(uri).build();
			}
		}
					
		}
		return ResponseEntity.notFound().build();
	}
	
	
	@GetMapping("/mesano/{mes}/{ano}/{administradora}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Cobrancaarquivo>> getListaCobranca(@PathVariable("mes") String mes, @PathVariable("ano") String ano, @PathVariable("administradora") String administradora) {
		if (administradora.equalsIgnoreCase("@")) {
			administradora = "";
		}
		List<Cobrancaarquivo> listaCobrancaArquivo = cobrancaArquivoRepository.getArquivos(administradora, mes + "/" + ano);
		if (listaCobrancaArquivo==null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(listaCobrancaArquivo);
	}
	
	
	
	
	
	

}
