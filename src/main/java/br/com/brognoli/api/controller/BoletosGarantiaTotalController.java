package br.com.brognoli.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.transfer.Upload;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.api.bean.AdmModelos;
import br.com.brognoli.api.bean.Diretorios;
import br.com.brognoli.api.bean.ExportarExcel;
import br.com.brognoli.api.bean.ModeloAdecon;
import br.com.brognoli.api.bean.ModeloAdelante;
import br.com.brognoli.api.bean.ModeloBRCONDOS;
import br.com.brognoli.api.bean.ModeloCNeto;
import br.com.brognoli.api.bean.ModeloControll;
import br.com.brognoli.api.bean.ModeloDuo;
import br.com.brognoli.api.bean.ModeloDuplic2;
import br.com.brognoli.api.bean.ModeloDuplique;
import br.com.brognoli.api.bean.ModeloExato;
import br.com.brognoli.api.bean.ModeloExato2;
import br.com.brognoli.api.bean.ModeloFama;
import br.com.brognoli.api.bean.ModeloFeltrin;
import br.com.brognoli.api.bean.ModeloGi;
import br.com.brognoli.api.bean.ModeloGrupoEmbracon;
import br.com.brognoli.api.bean.ModeloGrupoEmbraconAposVencimento;
import br.com.brognoli.api.bean.ModeloGrupoEmbraconCodigoBarras;
import br.com.brognoli.api.bean.ModeloGrupoEmbraconMalbec;
import br.com.brognoli.api.bean.ModeloGrupoEmbranconAdministrdora;
import br.com.brognoli.api.bean.ModeloLideranca;
import br.com.brognoli.api.bean.ModeloMaxima;
import br.com.brognoli.api.bean.ModeloNovara;
import br.com.brognoli.api.bean.ModeloPontual;
import br.com.brognoli.api.bean.ModeloResumoRateio;
import br.com.brognoli.api.bean.ModeloResumoRateioPonto;
import br.com.brognoli.api.bean.ModeloResumoRateioUmaColuna;
import br.com.brognoli.api.bean.RegraCondominio;
import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.Imoveladm;
import br.com.brognoli.api.model.Linhas;
import br.com.brognoli.api.model.Modelos;
import br.com.brognoli.api.repository.ImovelAdmRepository;
import br.com.brognoli.api.service.S3Service;

@CrossOrigin
@RestController
@RequestMapping("/gtboletos")
public class BoletosGarantiaTotalController {

	@Autowired
	private ImovelAdmRepository imovelAdmRepository;
	@Autowired
	private S3Service s3Service;
	
	private String caminhoDir;
	
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
	
	@PostMapping("/setdiretorio")
	@ResponseStatus(HttpStatus.CREATED)
	public void setDiretorio(@Valid @RequestBody Diretorios diretorios) {
		caminhoDir = diretorios.getCaminho();
		caminhoDir = caminhoDir.replace("@", "\\");
		caminhoDir = "G:\\0 - GESTÃO DE CONTRATOS\\11 - GARANTIA CONDOMÍNIO\\Garantia Total\\" + caminhoDir + "\\";
	}
	
	@GetMapping("/getdiretorios")
	@ResponseStatus(HttpStatus.CREATED)
	public String getDiretorios() {
		return caminhoDir;
	}
	
	@PostMapping("/gerarexcel")
	@ResponseStatus(HttpStatus.CREATED)
	public URI exportarExcel(@RequestParam(name = "file") MultipartFile[] files) {

		int encontrado = 0;

		List<String> listaAdm = new ArrayList<String>();
		if (files != null) {
			List<Boletos> listaBoletos = new ArrayList<Boletos>();
			String codigoImovel = null;
			for (MultipartFile uploadFile : files) {
				InputStream is = null;
				try {
					is = uploadFile.getInputStream();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				boolean gPDF = false;
				String fileName = "";
				codigoImovel = getCodgioImovel(uploadFile.getOriginalFilename());
				gPDF = true;
				if (codigoImovel == null) {
					gPDF = false;
				}
				if (gPDF) {
					Imoveladm imovelAdm = new Imoveladm();
					imovelAdm = imovelAdmRepository.getImovel(Integer.parseInt(codigoImovel));
					AdmModelos admModelos = new AdmModelos();
					Modelos modelo;
					if (imovelAdm == null) {
						modelo = null;
					} else {
						modelo = admModelos.validarModelo(imovelAdm.getAdmwinker());
						if (modelo != null) {
							// file.delete();
							// modelo = null;
							// encontrado++;
							// if (!modelo.getAdministradora().equalsIgnoreCase("GI-GESTÃO")) {
							// modelo = null;
							// }
						}

					}
					if (modelo != null) {
						encontrado++;
						System.out.println(modelo.getAdministradora() + " - " + uploadFile.getName());
						RegraCondominio regraCondominio = new RegraCondominio();
						if (imovelAdm.getEdificio() != null) {
							modelo = regraCondominio.retornaModelo(modelo, imovelAdm.getEdificio());
						}
						Boletos boleto = new Boletos();
						boleto.setCobranca(null);
						boleto.setImovelAdm(imovelAdm);
						boleto.setNomearquivo(uploadFile.getName());
						if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio")) {
							ModeloResumoRateio modeloResumoRateio = new ModeloResumoRateio();
							try {
								boleto.setListaResumo(modeloResumoRateio.gerarTXT(fileName, is));
							} catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloResumoRateio.getLinhaDigitavel());
							boleto.setEndereco(modeloResumoRateio.getEndereco());
							boleto.setNumero(modeloResumoRateio.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio ponto")) {
							ModeloResumoRateioPonto modeloResumoRateioPonto = new ModeloResumoRateioPonto();
							try {
								boleto.setListaResumo(modeloResumoRateioPonto.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloResumoRateioPonto.getLinhaDigitavel());
							boleto.setEndereco(modeloResumoRateioPonto.getEndereco());
							boleto.setNumero(modeloResumoRateioPonto.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ModeloResumoRateioUmaColuna")) {
							ModeloResumoRateioUmaColuna modeloResumoRateioPontoUmaColuna = new ModeloResumoRateioUmaColuna();
							try {
								boleto.setListaResumo(modeloResumoRateioPontoUmaColuna.gerarTXT(fileName, is));
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
								boleto.setListaResumo(modeloAdelante.gerarTXT(fileName,is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloAdelante.getLinhaDigitavel());
							boleto.setEndereco(modeloAdelante.getEndereco());
							boleto.setNumero(modeloAdelante.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo Correta")) {
							ModeloAdelante modeloAdelante = new ModeloAdelante();
							try {
								boleto.setListaResumo(modeloAdelante.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloAdelante.getLinhaDigitavel());
							boleto.setEndereco(modeloAdelante.getEndereco());
							boleto.setNumero(modeloAdelante.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo Novara")) {
							ModeloNovara modeloNovara = new ModeloNovara();
							try {
								boleto.setListaResumo(modeloNovara.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloNovara.getLinhaDigitavel());
							boleto.setEndereco(modeloNovara.getEndereco());
							boleto.setNumero(modeloNovara.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo Grupo Embracon")) {
							ModeloGrupoEmbracon modeloGrupoEmbracon = new ModeloGrupoEmbracon();
							try {
								boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
							boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
							boleto.setNumero(modeloGrupoEmbracon.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconAposVencimento")) {
							ModeloGrupoEmbraconAposVencimento modeloGrupoEmbracon = new ModeloGrupoEmbraconAposVencimento();
							try {
								boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
							boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
							boleto.setNumero(modeloGrupoEmbracon.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconCodigoBarras")) {
							ModeloGrupoEmbraconCodigoBarras modeloGrupoEmbracon = new ModeloGrupoEmbraconCodigoBarras();
							try {
								boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
							boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
							boleto.setNumero(modeloGrupoEmbracon.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ModeloGrupoEmbraconMalbec")) {
							ModeloGrupoEmbraconMalbec modeloGrupoEmbracon = new ModeloGrupoEmbraconMalbec();
							try {
								boleto.setListaResumo(modeloGrupoEmbracon.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloGrupoEmbracon.getLinhaDigitavel());
							boleto.setEndereco(modeloGrupoEmbracon.getEndereco());
							boleto.setNumero(modeloGrupoEmbracon.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ModeloLideranca")) {
							ModeloLideranca modeloLideranca = new ModeloLideranca();
							try {
								boleto.setListaResumo(modeloLideranca.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloLideranca.getLinhaDigitavel());
							boleto.setEndereco(modeloLideranca.getEndereco());
							boleto.setNumero(modeloLideranca.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Duplique")) {
							ModeloDuplique modeloDuplique = new ModeloDuplique();
							try {
								boleto.setListaResumo(modeloDuplique.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloDuplique.getLinhaDigitavel());
							boleto.setEndereco(modeloDuplique.getEndereco());
							boleto.setNumero(modeloDuplique.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Exato")) {
							ModeloExato modeloExato = new ModeloExato();
							try {
								boleto.setListaResumo(modeloExato.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloExato.getLinhaDigitavel());
							boleto.setEndereco(modeloExato.getEndereco());
							boleto.setNumero(modeloExato.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Pontual")) {
							ModeloPontual modeloPontual = new ModeloPontual();
							try {
								boleto.setListaResumo(modeloPontual.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloPontual.getLinhaDigitavel());
							boleto.setEndereco(modeloPontual.getEndereco());
							boleto.setNumero(modeloPontual.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Constâncio Neto")) {
							ModeloCNeto modeloCNeto = new ModeloCNeto();
							try {
								boleto.setListaResumo(modeloCNeto.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloCNeto.getLinhaDigitavel());
							boleto.setEndereco(modeloCNeto.getEndereco());
							boleto.setNumero(modeloCNeto.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Exato2")) {
							ModeloExato2 modeloExato = new ModeloExato2();
							try {
								boleto.setListaResumo(modeloExato.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloExato.getLinhaDigitavel());
							boleto.setEndereco(modeloExato.getEndereco());
							boleto.setNumero(modeloExato.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo Duplic2")) {
							ModeloDuplic2 modeloDuplic2 = new ModeloDuplic2();
							try {
								boleto.setListaResumo(modeloDuplic2.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloDuplic2.getLinhaDigitavel());
							boleto.setEndereco(modeloDuplic2.getEndereco());
							boleto.setNumero(modeloDuplic2.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Maxima")) {
							ModeloMaxima modeloMaxima = new ModeloMaxima();
							try {
								boleto.setListaResumo(modeloMaxima.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloMaxima.getLinhaDigitavel());
							boleto.setEndereco(modeloMaxima.getEndereco());
							boleto.setNumero(modeloMaxima.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Feltrin")) {
							ModeloFeltrin modeloFeltrin = new ModeloFeltrin();
							try {
								boleto.setListaResumo(modeloFeltrin.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloFeltrin.getLinhaDigitavel());
							boleto.setEndereco(modeloFeltrin.getEndereco());
							boleto.setNumero(modeloFeltrin.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio BRCONDOS")) {
							ModeloBRCONDOS modeloBRCONDOS = new ModeloBRCONDOS();
							try {
								boleto.setListaResumo(modeloBRCONDOS.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloBRCONDOS.getLinhaDigitavel());
							boleto.setEndereco(modeloBRCONDOS.getEndereco());
							boleto.setNumero(modeloBRCONDOS.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio CONTROLL")) {
							ModeloControll modeloControll = new ModeloControll();
							try {
								boleto.setListaResumo(modeloControll.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloControll.getLinhaDigitavel());
							boleto.setEndereco(modeloControll.getEndereco());
							boleto.setNumero(modeloControll.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio DUO SERVICOS1")) {
							ModeloDuo modeloDuo = new ModeloDuo();
							try {
								boleto.setListaResumo(modeloDuo.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloDuo.getLinhaDigitavel());
							boleto.setEndereco(modeloDuo.getEndereco());
							boleto.setNumero(modeloDuo.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Fama")) {
							ModeloFama modeloFama = new ModeloFama();
							try {
								boleto.setListaResumo(modeloFama.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloFama.getLinhaDigitavel());
							boleto.setEndereco(modeloFama.getEndereco());
							boleto.setNumero(modeloFama.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("Resumo de rateio Gi")) {
							ModeloGi modeloGi = new ModeloGi();
							try {
								boleto.setListaResumo(modeloGi.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloGi.getLinhaDigitavel());
							boleto.setEndereco(modeloGi.getEndereco());
							boleto.setNumero(modeloGi.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ModeloAdeconSC")) {
							ModeloAdecon modeloAdecon = new ModeloAdecon();
							try {
								boleto.setListaResumo(modeloAdecon.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloAdecon.getLinhaDigitavel());
							boleto.setEndereco(modeloAdecon.getEndereco());
							boleto.setNumero(modeloAdecon.getNumero());
						} else if (modelo.getModelo().equalsIgnoreCase("ResumoGrupoEmbraconAdministradora")) {
							ModeloGrupoEmbranconAdministrdora modeloGrupo = new ModeloGrupoEmbranconAdministrdora();
							try {
								boleto.setListaResumo(modeloGrupo.gerarTXT(fileName, is));
							} catch (IOException e) {
								// TODO Auto-generated catch block e.printStackTrace();
							}
							boleto.setLinhaDigitavel(modeloGrupo.getLinhaDigitavel());
							boleto.setEndereco(modeloGrupo.getEndereco());
							boleto.setNumero(modeloGrupo.getNumero());
						}
						listaBoletos.add(boleto);
					}
				}
			}
			System.out.println(encontrado);
			if (listaBoletos != null) {
				if (listaBoletos.size() > 0) {
					ExportarExcel ex = new ExportarExcel();
					ex.gerarGT(listaBoletos);
					File file = ex.getFile();
					URI uri = s3Service.uploadFile(file);
					return uri;
				}
			}
		}
		return null;
	}

	public String getCodgioImovel(String nome) {
		String novoNome = "";
		boolean pegarnome = false;
		for (int i=0;i<nome.length();i++) {
			if (nome.charAt(i) != '_') {
				novoNome = novoNome + nome.charAt(i);
			} else {
				i = 100;
			}
		}
		if (novoNome.length()>= 4) {
			novoNome = novoNome.replace(" ", "");
			novoNome = novoNome.replace(".pdf", "");
			return novoNome;
		}
		novoNome = "";
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
		novoNome = novoNome.replace(".pdf", "");
		return novoNome;
	}
	
	@PostMapping("/upload")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> uploadPDF(@RequestParam(name="file") MultipartFile[] files) {
		URI uri = exportarExcel(files);
		return ResponseEntity.created(uri).build();
	}

	@PostMapping("/sf/upload")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> uploadPDFSimplficada(@RequestParam(name="file") MultipartFile[] files) {
		String codigoImovel = null;
		Imoveladm imovelAdm = new Imoveladm();
		List<Boletos> listaBoletos = new ArrayList<>();
		String tipo ="";
		for (MultipartFile uploadFile : files) {
			
			InputStream is = null;
			try {
				is = uploadFile.getInputStream();
				codigoImovel = getCodgioImovel(uploadFile.getOriginalFilename());
				System.out.println(uploadFile.getOriginalFilename());
				imovelAdm = imovelAdmRepository.getImovel(Integer.parseInt(codigoImovel));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PdfReader reader;
			List<Linhas> lines = new ArrayList<Linhas>();
			try {
				reader = new PdfReader(is);
				PdfReaderContentParser parser = new PdfReaderContentParser(reader);
				lines = new ArrayList<Linhas>();
				PrintWriter out = new PrintWriter(new FileOutputStream(new File("c:\\logs\\texto.txt")));
				TextExtractionStrategy strategy;
				for (int i = 1; i <= reader.getNumberOfPages(); i++) {
					strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
					out.println(strategy.getResultantText());
				}
				out.flush();
				out.close();
				reader.close();
				BufferedReader br = new BufferedReader(new FileReader(new File("c:\\logs\\texto.txt")));

				while (br.ready()) {
					String linha = br.readLine();
					Linhas l = new Linhas();
					l.setLinha(linha);
					lines.add(l);
				}
				br.close();
				tipo = retornoTipoBoleto(lines);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (tipo.equalsIgnoreCase("Condominio")) {
					if (imovelAdm!=null) {
						listaBoletos.add(boletoGarantiaGetCB(is, uploadFile.getOriginalFilename(), imovelAdm, lines));
					} else {
						System.out.println("NULO = " + uploadFile.getOriginalFilename());
						Boletos b = new Boletos();
						b.setLinhaDigitavel("Erro localizar código");
						b.setTipo(tipo);
						listaBoletos.add(b);
					}
				} else if (tipo.equalsIgnoreCase("Casan")) {
					Boletos b = lerPdfCasan(lines);
					b.setNomearquivo(uploadFile.getOriginalFilename());
					b.setNomearquivo(b.getNomearquivo().replace(".pdf", ""));
					b.setTipo(tipo);
					listaBoletos.add(b);
				} else if (tipo.equalsIgnoreCase("Celesc")) {
					Boletos b = lerPdfCelesc(lines);
					b.setNomearquivo(uploadFile.getOriginalFilename());
					b.setNomearquivo(b.getNomearquivo().replace(".pdf", ""));
					b.setTipo(tipo);
					listaBoletos.add(b);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (listaBoletos != null) {
			if (listaBoletos.size() > 0) {
				ExportarExcel ex = new ExportarExcel();
				ex.gerarGTSimpificada(listaBoletos);
				File file = ex.getFile();
				URI uri = s3Service.uploadFile(file);
				return ResponseEntity.created(uri).build();
			}
		}
		return null;
	}
	
	public Boletos boletoGarantiaGetCB(InputStream is, String nomearquivo, Imoveladm imovelAdm, List<Linhas> lines) throws IOException {
		Boletos boleto = new Boletos();
		boolean achou = false;
		boolean achouCNPJ = false;
		
		for (int i = 0; i < lines.size() - 1; i++) {
			if (!achouCNPJ) {
				achouCNPJ = true;
				String cnpj = (lerCNPJ(lines, imovelAdm.getPadraocnpj()));
				cnpj = validarCNPJ(cnpj);
				boleto.setCnpj(cnpj);
			}
			if (imovelAdm.getAdmwinker()!=null) {
				if (imovelAdm.getAdmwinker().equalsIgnoreCase("ADELANTE FLORIANÓPOLIS")) {
					boleto.setLinhaDigitavel(linhaDigitavelAdelante(lines));
					boleto.setNomearquivo(nomearquivo);
					achou = true;
				}
			} else if (imovelAdm.getAdministradora().equalsIgnoreCase("IDEAL")) {
				boleto.setLinhaDigitavel(linhaDigitavelIdeal(lines));
				boleto.setNomearquivo(nomearquivo);
				achou = true;
			}
			if (!achou) {
				
				if (lines.get(i).getLinha().length() == 62) {
					System.out.println(lines.get(i).getLinha().substring(5, 6));
					if (lines.get(i).getLinha().substring(5, 6).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha());
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}

				} else if (lines.get(i).getLinha().length() == 54) {
					if (lines.get(i).getLinha().substring(5, 6).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha());
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 58) {
					if (lines.get(i).getLinha().substring(5, 6).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha());
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 63) {
					if (lines.get(i).getLinha().substring(5, 6).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha());
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 60) {
					if (lines.get(i).getLinha().substring(11, 12).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha().substring(6, 60));
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 61) {
					if (lines.get(i).getLinha().substring(12, 13).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha().substring(6, 61));
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 68) {
					if (lines.get(i).getLinha().substring(19, 20).equalsIgnoreCase(".")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha().substring(14, 68));
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 73) {
					if (lines.get(i).getLinha().substring(50, 55).equalsIgnoreCase("Venc.")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha().substring(0, 48));
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 65) {
					if (lines.get(i).getLinha().substring(0, 11).equalsIgnoreCase("ITAÚ 341-7 ")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha().substring(11, lines.get(i).getLinha().length()));
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				} else if (lines.get(i).getLinha().length() == 71) {
					if (lines.get(i).getLinha().substring(0, 16).equalsIgnoreCase("BANSICREDI 748-0")) {
						boleto.setLinhaDigitavel(lines.get(i).getLinha().substring(18, lines.get(i).getLinha().length()));
						boleto.setNomearquivo(nomearquivo);
						achou = true;

					}
				}
			}

		}
		if (!achou) {
			boleto.setLinhaDigitavel("Não encontrado");
			nomearquivo = nomearquivo.replace(".pdf", "");
			boleto.setNomearquivo(nomearquivo);
		} else {
			boleto.setLinhaDigitavel(boleto.getLinhaDigitavel().replace(" ", ""));
			boleto.setLinhaDigitavel(boleto.getLinhaDigitavel().replace(".", ""));
			boleto.setLinhaDigitavel(boleto.getLinhaDigitavel().replace("-", ""));
		}
		boleto.setTipo("Condominio");
		return boleto;
	}
	
	
	public String lerCNPJ(List<Linhas> lines, int padrao) {
		if (padrao==1) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Uso do Banco (=) Valor do Documento")) {
					return lines.get(i-1).getLinha();
				} else if (lines.get(i).getLinha().equalsIgnoreCase("Uso Banco (=) Valor do Documento")) {
					return lines.get(i-1).getLinha();
				}else if (lines.get(i).getLinha().equalsIgnoreCase("(=) Valor do Documento")) {
					return lines.get(i-1).getLinha();
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Uso do Banco Valor documento")) {
					return lines.get(i-1).getLinha();
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Uso do Banco (=) Valor do Documento")) {
					return lines.get(i-1).getLinha();
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Uso do Banco Valor do Documento")) {
					return lines.get(i-1).getLinha();
				}
			}
		}else if (padrao==2) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("CPF/CNPJ do Beneficiário")) {
					return lines.get(i+1).getLinha();
				}
			}
		}else if (padrao==3) {
			for(int i=0;i<lines.size();i++) {
				if ((lines.get(i).getLinha().equalsIgnoreCase("Preferencialmente nas redes Siccob")) ||
				(lines.get(i).getLinha().equalsIgnoreCase("PAGÁVEL PREFERENCIALMENTE NAS COOPERATIVAS DA REDE SICOOB")) ||
				(lines.get(i).getLinha().equalsIgnoreCase("PAGAVEL EM QUALQUER BANCO")) ||
				(lines.get(i).getLinha().equalsIgnoreCase("BENEFICIÁRIO")) ||
				(lines.get(i).getLinha().equalsIgnoreCase("Pagavél preferencialmente nas cooperativas de rede SICOOb ou qualquer banco até o ven")) 
				
				){
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==4) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Agência/Código Beneficiário")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(0, 18);
					return r;
				}
			}
		}else if (padrao==5) {
			String l = lines.get(lines.size()-2).getLinha(); 
			String r =  l.substring(l.length()-18, l.length());
			return r;
				
			
		}else if (padrao==6) {
			for(int i=0;i<lines.size();i++) {
				if ((lines.get(i).getLinha().equalsIgnoreCase("BENEFICIÁRIO (CNPJ/CPF)")) ||
						(lines.get(i).getLinha().equalsIgnoreCase("Beneficiário:")) ||
						(lines.get(i).getLinha().equalsIgnoreCase("*Pagável em qualquer banco.")) ||
						(lines.get(i).getLinha().equalsIgnoreCase("PAGÁVEL EM QUALQUER BANCO ATÉ O VENCIMENTO"))
						
					) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				} else if (lines.get(i).getLinha().equalsIgnoreCase("Pagavel em qualquer banco")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("PAGÁVEL EM QUALQUER BANCO")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("PREFERENCIALMENTE NAS AGÊNCIAS DA CAIXA OU LOTÉRICAS ATÉ O VENCIMENTO")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("PAGÁVEL EM QUALQUER AGÊNCIA BANCÁRIA/CORRESPONDENTE BANCÁRIO")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("PAGÁVEL EM TODA REDE BANCÁRIA ATÉ O VENCIMENTO")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Pagar Prefer.nas Coop. do Sistema CECRED. Após Vcto, pagar somente na Coop CREDELESC")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("CASAS LOTÉRICAS, AG. DA CAIXA E REDE BANCÁRIA")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("PREFERENCIALMENTE NAS CASAS LOTERICAS ATE O VALOR LIMITE")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("BENEFICIÁRIO")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==7) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Corte na linha pontilhada")) {
					String l = lines.get(i+4).getLinha(); 
					String r =  l.substring(l.length()-15, l.length());
					return r;
				}
			}
		}else if (padrao==8) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Não receber o documento após o vencimento.")) {
					String l = lines.get(i+2).getLinha(); 
					String r =  l.substring(l.length()-15, l.length());
					return r;
				} else if (lines.get(i).getLinha().equalsIgnoreCase("CNPJ")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					r = r.replace(" ", "");
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Agência código do Beneficiário")) {
					String l = lines.get(i-2).getLinha(); 
					String r =  l.substring(l.length()-15, l.length());
					return r;
				} 
			}
		}else if (padrao==9) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Sacador/Avalista")) {
					String l = lines.get(i+5).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==10) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiário")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-19, l.length()-1);
					return r;
				} else if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiário:")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Agência código do Beneficiário")) {
					String l = lines.get(i-2).getLinha(); 
					String r =  l.substring(l.length()-15, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("PAGAR PREFERENCIALMENTE NO SANTANDER")) {
					String l = lines.get(i-2).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("BENEFICIÁRIO (CNPJ/CPF)")) {
					String l = lines.get(i-2).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==11) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("N")) {
					String l = lines.get(i-3).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==12) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Até o vencimento pagável em qualquer Banco.")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+15));
							n = l.length()+100;
						}
					}
					return r;
				}
			}
		}else if (padrao==13) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Pagável preferencialmente no banco Santander")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+18));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				}
			}
		}else if (padrao==14) {
			String l = lines.get(2).getLinha();
			String r = "";
			for (int n=0;n<l.length();n++) {
				if (l.charAt(n)=='(') {
					r = l.substring((n+1), (n+19));
					n = l.length()+100;
				}
			}
			r = r.replace(")", "");
			return r;
		}else if (padrao==15) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica - Ficha de Compensação")) {
					String l = lines.get(i+3).getLinha(); 
					if (l.length()<30) {
						l = lines.get(i+4).getLinha(); 
					}
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica Ficha de Compensação")) {
					String l = lines.get(i+3).getLinha(); 
					if (l.length()<30) {
						l = lines.get(i+4).getLinha(); 
					}
					String r =  l.substring(l.length()-18, l.length());
					return r;
				} 
			}
		}else if (padrao==16) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Pagador")) {
						String l = lines.get(i-1).getLinha();
						String r = "";
						for (int n=0;n<l.length();n++) {
							if (l.charAt(n)=='-') {
								r = l.substring((n+2), (n+18));
								n = l.length()+100;
							}
						}
						r = r.replace(")", "");
						return r;
					
				}
			}
		}else if (padrao==17) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Cedente")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+19));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				}
			}
		}else if (padrao==18) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("PAGAVEL PREFERENCIALMENTE NAS COOPERATIVAS DO SISTEMA AILOS.")) {
						String l = lines.get(i+1).getLinha();
						String r = "";
						for (int n=0;n<l.length();n++) {
							if (l.charAt(n)=='(') {
								r = l.substring((n+1), (n+16));
								n = l.length()+100;
							}
						}
						r = r.replace(")", "");
						return r;
					
				}
			}
		}else if (padrao==19) {
			String l = lines.get(lines.size()-1).getLinha();
			String r =  l.substring(l.length()-18, l.length());
			return r;
		}else if (padrao==20) {
			String l = lines.get(0).getLinha();
			String r =  l.substring(l.length()-18, l.length());
			return r;
		}else if (padrao==21) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("PAGAR PREFERENCIALMENTE NO SANTANDER")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==22) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Pagavél preferencialmente nas Cooperativas do Sistema Ailos.")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(8, l.length());
					return r;
				} else if (lines.get(i).getLinha().contains(" Pagável em qualquer banco ")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-15, l.length());
					return r;
				}if (lines.get(i).getLinha().equalsIgnoreCase("PAGAVEL PREFERENCIALMENTE NAS COOPERATIVAS DO SISTEMA AILOS.")) {
					String l = lines.get(i+1).getLinha(); 
					String r="";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+16));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				}
			}
		}else if (padrao==23) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Feltrin Administração de condomínios")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+19));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				} 
			}
		}else if (padrao==24) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiário")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					return r;
				} else if (lines.get(i).getLinha().equalsIgnoreCase("BENEFICIÁRIO")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					return r;
				}
			}
		}else if (padrao==25) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains(" Pagável em qualquer banco ")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==26) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("CPF/CNPJ do Beneficiário")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l;
					return r;
				}else if (lines.get(i).getLinha().contains("CPF/CNPJ do Beneciário")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l;
					return r;
				}else if (lines.get(i).getLinha().contains("BENEFICIÁRIO (CNPJ/CPF)")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}  
			}
		}else if (padrao==27) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Uso do Banco Valor documento")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					return r;
				}
			}
		}else if (padrao==28) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("PAGAR PREFERENCIALMENTE NO SANTANDER")) {
					if (lines.get(i-1).getLinha().length()>10) {
						String l = lines.get(i-1).getLinha(); 
						String r =  l.substring(l.length()-18, l.length());
						return r;
					}
				} else if (lines.get(i).getLinha().equalsIgnoreCase("PAGAR PREFERENCIALMENTE NO SANTANDER - APÓS VENCIMENTO SOMENTE NO SANTANDER")) {
					if (lines.get(i-1).getLinha().length()>10) {
						String l = lines.get(i-1).getLinha(); 
						String r =  l.substring(l.length()-18, l.length());
						return r;
					}
				}
			}
		}else if (padrao==29) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Data de Emissão")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					return r;
				}
			}
		}else if (padrao==30) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Pagador")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==31) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Pagador")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l.substring(24, 43);
					return r;
				}
			}
		}else if (padrao==32) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiário ")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+19));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				} 
			}
		}else if (padrao==33) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Pagador")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==34) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("www.caixa.gov.br")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(27, 46);
					return r;
				}
			}
		}else if (padrao==35) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Recibo do pagador")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==36) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiario: NOME E CNPJ/CPF Agencia/Codigo do Beneficiario")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(37, 56);
					return r;
				}
			}
		}else if (padrao==37) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Feltrin Administração de condomínios")) {
					String l = lines.get(i+5).getLinha();
					String r = l;
					return r;
				} 
			}
		}else if (padrao==38) {
			String l = lines.get(0).getLinha();
			String r = l.substring(6,24);
			return r;
			
		}else if (padrao==39) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("(=) Valor do Documento Valor Cobrado")) {
					String l = lines.get(i+1).getLinha();
					String r = l.substring(0,18);
					return r;
				} 
			}
		}else if (padrao==40) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica / RECIBO DO PAGADOR")) {
					String l = lines.get(i+2).getLinha();
					String r = l.substring(5,25);
					return r;
				} 
			}
		}else if (padrao==41) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("CPF/CNPJ")) {
					String l = lines.get(i+1).getLinha();
					String r = l;
					return r;
				} 
			}
		}else if (padrao==42) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiário Agência/Código do Beneficiário Espécie Quantidade Nosso número")) {
					String l = lines.get(i+1).getLinha();
					String r = l.substring(l.length()-15, l.length());
					return r;
				} 
			}
		}else if (padrao==43) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Beneficiário:")) {
					String l = lines.get(i+3).getLinha(); 
					String r =  l;
					return r;
				}else if (lines.get(i).getLinha().contains("Beneficiário")) {
					String l = lines.get(i+3).getLinha(); 
					String r =  l;
					return r;
				} 
			}
		}else if (padrao==44) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Preferencialmente nas casas lotéricas até o valor limite.")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+19));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				}
			}
		}else if (padrao==45) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Uso do Banco (=) Valor do Documento")) {
					String l = lines.get(i-2).getLinha(); 
					String r =  l;
					return r;
				}
			}
		}else if (padrao==46) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("N. do Documento")) {
					String l = lines.get(i+1).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==47) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Pagável preferencialmente no banco Banco do Brasil")) {
					String l = lines.get(i+1).getLinha();
					String r = "";
					for (int n=0;n<l.length();n++) {
						if (l.charAt(n)=='(') {
							r = l.substring((n+1), (n+19));
							n = l.length()+100;
						}
					}
					r = r.replace(")", "");
					return r;
				}
			}
		}else if (padrao==48) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Uso do Banco (=) Valor do Documento")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					return r;
				}
			}
		}else if (padrao==49) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Local de Pagamento")) {
					String l = lines.get(i+2).getLinha(); 
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}
			}
		}else if (padrao==50) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("CNPJ")) {
					String l = lines.get(i-1).getLinha(); 
					String r =  l;
					return r;
				}
			}
		}else if (padrao==51) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Beneficiário")) {
					String l = lines.get(i+2).getLinha();
					String r = l;
					
					r = r.replace(")", "");
					r = r.replace("(", "");
					return r;
				} 
			}
		}else if (padrao==52) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica - Ficha de Compensação")) {
					String l = lines.get(i+2).getLinha();
					String r = l;
					
					return r;
				} 
			}
		}else if (padrao==53) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica - Ficha de Compensação")) {
					String l = lines.get(i+3).getLinha(); 
					if (l.length()<30) {
						l = lines.get(i+4).getLinha(); 
					}
					String r =  l.substring(l.length()-18, l.length());
					return r;
				}else if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica Ficha de Compensação")) {
					String l = lines.get(i+3).getLinha(); 
					if (l.length()<30) {
						l = lines.get(i+4).getLinha(); 
					}
					String r =  l.substring(l.length()-18, l.length());
					return r;
				} 
			}
		}else if (padrao==54) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica/FICHA DE COMPENSAÇÃO")) {
					String l = lines.get(i+2).getLinha();
					String r = l;
					
					return r;
				} 
			}
		}else if (padrao==55) {
			String l = lines.get(3).getLinha();
			String r = l.substring(1, 19);
			return r;
			
		}else if (padrao==-1) {
			return "SEM CNPJ";
		}
		return "Não encontrado";
	}
	
	public String validarCNPJ(String cnpj) {
		cnpj = cnpj.replace(" ", "");
		cnpj.replace(":", "");
		char p = cnpj.charAt(1);
		if (cnpj.charAt(0)==':') {
			cnpj = cnpj.substring(1, cnpj.length());
		}
		if ((p == '0') || (p == '1') || (p == '2') || (p == '3') || (p == '4') || (p == '5') || (p == '6') || (p == '7')
				|| (p == '8') || (p == '9')) {
			if (cnpj.length()==14) {
				cnpj = cnpj.substring(0,2) + "." + cnpj.substring(2,5) + "." + cnpj.substring(5,8) + "/" +cnpj.substring(8,12) + "-" + cnpj.substring(12,14);
			}
			return cnpj;
		} else {
			return "SEM CNPJ";
		}
	}
	
	public String linhaDigitavelAdelante(List<Linhas> linhas) {
		String linha1  = "";
		String linha2 = "";
		String linha = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Pagável em qualquer Banco até o vencimento.")) {
				linha1 = linhas.get(i+1).getLinha();
			}else if (linhas.get(i).getLinha().equalsIgnoreCase("R$")) {
				linha2 = linhas.get(i+2).getLinha();
				i = linhas.size()+100;
			}else if (linhas.get(i).getLinha().equalsIgnoreCase("REAL")) {
				linha2 = linhas.get(i+2).getLinha();
				i = linhas.size()+100;
			}
			if ((linha1.length()>0) && (linha2.length()>0)) {
				linha = linha1.substring(0,11);
				linha = linha + linha2;
				linha = linha + linha1.substring(12,26);
			}
		}
		return linha;
	}
	
	public String linhaDigitavelIdeal(List<Linhas> linhas) {
		String linha = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("LOCAL DE PAGAMENTO")) {
				if (linhas.get(i-1).getLinha().length()==60) {
					linha = linhas.get(i-1).getLinha();
					linha = linha.replace("237-2 ", "");
				}
			}
		}
		return linha;
	}
	
	public String retornoTipoBoleto(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("Celesc Distribuicao S.A")) {
				return "Celesc";
			}
		}
		if (lines.get(0).getLinha().equalsIgnoreCase("DESCRIÇÃO DOS SERVIÇOS FATURADOS")) {
			return "Casan";
		}
		return "Condominio";
	}
	
	public Boletos lerPdfCasan(List<Linhas> lines) {
		Boletos boleto = new Boletos(); 
		boleto.setLinhaDigitavel(getLinhaDigitavelCasan(lines));
        boleto.setDatavencimento(getDataVencimentoCasan(lines));
        boleto.setValor(getValor(lines));
        boleto.setReferencia(lines.get(17).getLinha());
        boleto.setCnpj("82.508.433/0001-17");
        return boleto;
	}
	
	public Boletos lerPdfCelesc(List<Linhas> lines) {
		Boletos boleto = new Boletos();
		boleto.setDatavencimento(getDataVencimentoCelesc(lines));
   	 	boleto.setValor(getValorCelesc(lines));
		boleto.setLinhaDigitavel(getLinhaDigitavelCelesc(lines));
		boleto.setDatavencimento(getDataVencimentoCelesc(lines));
   	 	String cnpj = (lines.get(5).getLinha().substring(0,24));
   	 	cnpj = cnpj.replace("CNPJ: ", "");
   	 	boleto.setCnpj(cnpj);
   	 	boleto.setReferencia(lines.get(8).getLinha().substring(lines.get(8).getLinha().length()-7, lines.get(8).getLinha().length()));
		return boleto;
	}
	
	public String getLinhaDigitavelCasan(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().charAt(0) == '8') {
				if (lines.get(i).getLinha().length()==51) {
					 String codigo = lines.get(i).getLinha();
					 codigo = codigo.replace(" ", "");
					 return codigo;
				}
			} else {
				if (lines.get(i).getLinha().equalsIgnoreCase("FATURA PARA SIMPLES CONFERÊNCIA - DÉBITO EM CONTA")) {
					String codigo = lines.get(i).getLinha();
					 codigo = codigo.replace(" ", "");
					 return codigo;
				}
			}
		}
		return "";
	}
	
	public String getValor(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("USO BANCO")) {
				return lines.get(i+1).getLinha();
			}
		}
		return "";
	}
	
	public String getValorCelesc(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("VALOR ATÉ O VENCIMENTO")) {
				String valor = lines.get(i+1).getLinha();
				valor = valor.replace("R$ ", "");
				return valor;
			}
		}
		return "";
	}
	
	public String getDataVencimentoCelesc(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("VENCIMENTO")) {
				return lines.get(i+1).getLinha();
			}
		}
		return "";
	}
	
	
	public String getDataVencimentoCasan(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("DATA DE VENCIMENTO")) {
				return lines.get(i+4).getLinha();
			}
		}
		return "";
	}
	
	public String getLinhaDigitavelCelesc(List<Linhas> lines) {
		String codigo = lines.get(lines.size()-2).getLinha();
		codigo = codigo.replace(" ", "");
		return codigo;
	}
	
	

}
