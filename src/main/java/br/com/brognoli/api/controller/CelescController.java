package br.com.brognoli.api.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import br.com.brognoli.api.bean.ExportarExcel;
import br.com.brognoli.api.bean.LerPDFCelesc;
import br.com.brognoli.api.model.Boletos;

import br.com.brognoli.api.model.CelescDados;
import br.com.brognoli.api.model.CelescFatura;
import br.com.brognoli.api.model.CelescHistorico;
import br.com.brognoli.api.model.Proprietario;
import br.com.brognoli.api.model.Resposta;
import br.com.brognoli.api.service.S3Service;
import br.com.brognoli.api.util.Conversor;

@CrossOrigin
@RestController
@RequestMapping("/celesc")
public class CelescController {
	
	private WebDriver driver;
	private CelescDados celescDados;
	private List<CelescDados> listaCelescDados;
	HtmlPage pagina;
	WebClient webClient;
	@Autowired
	private S3Service s3Service;
	private int posicao;
	private List<CelescDados> novaLista;
	
	@GetMapping("/{opcao}/{unidade}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CelescDados> get2Via(@PathVariable("opcao") String opcao, @PathVariable("unidade") String unidade) {
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		celescDados = new CelescDados();
		if (iniciarPagina()) {
			getUnidade(unidade, opcao, true, diretorio, false, false);
			return ResponseEntity.ok(celescDados);
		} else  {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			driver.close();
			driver.quit();
			return ResponseEntity.ok(null);
		}
		
	}
	
	
	@GetMapping("/debito/boletos")
	@ResponseStatus(HttpStatus.CREATED)
	public void getboeltos() {
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		try {
			getPDF("debito", 0, diretorio, false);
			Thread.sleep(5000);
			driver.close();
			driver.quit();
		}catch (Exception e) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			driver.close();
			driver.quit();
		}
		
	}
	
	@GetMapping("/hp/boletos")
	@ResponseStatus(HttpStatus.CREATED)
	public void getHpboeltos() {
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		try {
			getPDF("hp", -1, diretorio, false);
			driver.close();
			driver.quit();
		} catch (Exception e) {
			driver.close();
			driver.quit();
		}
	}
	
	@GetMapping("/hp/fatura/{posicao}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Resposta> getHpFatura(@PathVariable("posicao") int posicao) {
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		Resposta res = new Resposta();
		res.setResultado("ERRO");
		if (celescDados.getListaHistorico()!=null) {
			if (celescDados.getListaHistorico().size()>0) {
				String unidade = celescDados.getCodigo();
				if (iniciarPagina()) {
					try {
					getUnidade(unidade, "hp", false, diretorio, false, false);
					WebElement menu2Via = driver.findElement(By.xpath("//*[@id=\"mn\"]/table/tbody/tr[16]/td/a"));
					menu2Via.click();
					getPDF("hp", posicao, diretorio, false);
					driver.close();
					driver.quit();
					res.setResultado("OK");
					} catch (Exception e) {
						driver.close();
						driver.quit();
					}
				}else {
					driver.close();
					driver.quit();
					res.setResultado("CONEXAO");
				}
			}
		}
		return ResponseEntity.ok(res); 
	}
	
	
	@PostMapping("/unidade")
	@ResponseStatus(HttpStatus.CREATED)
	public String setDiretorio(@Valid @RequestBody Proprietario proprietario) {
		String unidade = getUnidade(proprietario);
		return unidade;
	}
	
	public String getUnidade(Proprietario proprietario){
        try {
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
        System.setProperty("webdriver.chrome.driver", "C:/Logs/drive/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        //options.addArguments("--headless");
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);

        driver.get("https://agenciaweb.celesc.com.br/AgenciaWeb/autenticar/loginUC.do");
        try {
        } catch (Exception ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
        WebElement inputCPF = driver.findElement(By.name("numDoc"));
        inputCPF.sendKeys(proprietario.getCpf());
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        WebElement inputData = driver.findElement(By.name("dtaNascimentoDoc"));
        Conversor c = new Conversor();
        String dataNasc = c.ConvercaoDataBR(proprietario.getDatanascimento()); 
        inputData.sendKeys(dataNasc);
        WebElement botaoEntrar = driver.findElement(By.xpath("//*[@id=\"fundoPrincipalLogout\"]/form/div[5]/input"));
		botaoEntrar.click();
		WebElement unidade = driver.findElement(By.xpath("//*[@id=\"listaFat\"]/span[2]"));
		String sUnidade = unidade.getText();
		driver.close();
		driver.quit();
		return sUnidade;
		
        }catch (Exception e) {
        	System.out.println(e.getMessage());
        	System.out.println(e.getStackTrace());
        	driver.close();
    		driver.quit();
			return "";
		}
        
        
    }
	
	@GetMapping("/desligamentouc/{unidade}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CelescDados> get2Via(@PathVariable("unidade") String unidade) {
		celescDados = new CelescDados();
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		if (iniciarPagina()) {
			try {
				getUnidade(unidade, "desligamento", true, diretorio, false, false);
				driver.close();
				driver.quit();
				return ResponseEntity.ok(celescDados);
			}catch (Exception e) {
				driver.close();
				driver.quit();
			}
		} else  {
			driver.close();
			driver.quit();
			return ResponseEntity.ok(null);
		}
		return ResponseEntity.ok(null);
		
	}
	
	@GetMapping("/getlista")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<CelescDados>> getListarCarne() {
		return ResponseEntity.ok(listaCelescDados);
	}
	
	@PostMapping("/setlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void setLista(@Valid @RequestBody List<CelescDados> lista) {
		listaCelescDados = lista;
	}
	
	@PostMapping("/gerarlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void lerXLSX(@RequestParam(name = "file") MultipartFile file) throws IOException, InvalidFormatException {
		listaCelescDados = new ArrayList<CelescDados>();
		String url = "c:\\logs\\" + file.getOriginalFilename();
		File fileToSave = new File(url);
		fileToSave.createNewFile();
		FileOutputStream fos = new FileOutputStream(fileToSave);
		fos.write(file.getBytes());
		fos.close();
		BufferedReader br = null;
		InputStream is = file.getInputStream();
		OPCPackage pkg = OPCPackage.open(fileToSave);
		XSSFWorkbook workbook = new XSSFWorkbook(pkg);
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator linhas = sheet.rowIterator();
		linhas.next();
		while (linhas.hasNext()) {
			XSSFRow linha = (XSSFRow) linhas.next();
			Iterator celulas = linha.cellIterator();
			CelescDados celescDados = new CelescDados();
			celescDados.setNome("");
			celescDados.setCpf("");
			celescDados.setPedidodesligamento("");
			celescDados.setResultado("Carregado");
			celescDados.setImovel(0);
			
			while (celulas.hasNext()) {
				XSSFCell cel = (XSSFCell) celulas.next();
				int z = cel.getColumnIndex();
				switch (z) {
				case 0:
					if (cel.getCellTypeEnum() == CellType.STRING) {
						String dados = cel.getStringCellValue();
						celescDados.setImovel(Integer.parseInt(dados));
					} else if (cel.getCellTypeEnum() == CellType.NUMERIC) {
						int dados = (int) cel.getNumericCellValue();
						celescDados.setImovel(dados);
					}
				case 1:
					if (cel.getCellTypeEnum() == CellType.STRING) {
						String dados = cel.getStringCellValue();
						dados = dados.replace("*", "");
						celescDados.setCodigo(dados);
					} else if (cel.getCellTypeEnum() == CellType.NUMERIC) {
						int dados = (int) cel.getNumericCellValue();
						celescDados.setCodigo(String.valueOf(dados));
					}
					
				}
			}
			
			listaCelescDados.add(celescDados);
		}
	}
	
	@GetMapping("/getop/{nomepasta}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<CelescDados>> getOP(@PathVariable("nomepasta") String nomepasta) {
		if (nomepasta.equalsIgnoreCase("@")) {
			Conversor c = new Conversor();
			nomepasta = c.getDiaMesAno(new Date());
		}
		String caminhoDir="\\\\192.168.1.58\\documentos\\centralfinanceira\\BOLETOS DE CONDOMÍNIOS\\celesc\\" + nomepasta;
		File dirFile = new File(caminhoDir);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		boolean driverOpen = false;
		caminhoDir = caminhoDir + "\\";
		Resposta r = new Resposta();
		if (posicao==0) {
			novaLista = new ArrayList<CelescDados>();
		}
		if (listaCelescDados!=null) {
			//listaCelescDados.size()
			for (int i=posicao;i<listaCelescDados.size();i++) {
				celescDados = new CelescDados();
				celescDados.setCodigo(listaCelescDados.get(i).getCodigo());
				celescDados.setImovel(listaCelescDados.get(i).getImovel());
				if (listaCelescDados.get(i).getResultado().equalsIgnoreCase("PDF SALVO")) {
					novaLista.add(listaCelescDados.get(i));
				} else if (listaCelescDados.get(i).getResultado().equalsIgnoreCase("UC INVÁLIDA")) {
					novaLista.add(listaCelescDados.get(i));
				} else if (listaCelescDados.get(i).getResultado().equalsIgnoreCase("CARREGADO")) {
					
					
					if (driverOpen) {
						getUnidade(listaCelescDados.get(i).getCodigo(), "debito", true, caminhoDir, driverOpen, true);
						driverOpen = true;
						celescDados.setCodigo(listaCelescDados.get(i).getCodigo());
						celescDados.setImovel(listaCelescDados.get(i).getImovel());
						novaLista.add(celescDados);
					} else {
						if (iniciarPagina()) {
							try {
							getUnidade(listaCelescDados.get(i).getCodigo(), "debito", true, caminhoDir, driverOpen, true);
							driverOpen = true;
							celescDados.setCodigo(listaCelescDados.get(i).getCodigo());
							celescDados.setImovel(listaCelescDados.get(i).getImovel());
							novaLista.add(celescDados);
							}catch (Exception e) {
								driver.close();
								driver.quit();
							}
						} else {
							if (driverOpen) {
								driver.close();
								driver.quit();
							}	
							driverOpen = false;
						}
					}
				} else if (listaCelescDados.get(i).getResultado().equalsIgnoreCase("ERRO")) {
					if (driverOpen) {
						getUnidade(listaCelescDados.get(i).getCodigo(), "debito", true, caminhoDir, driverOpen, true);
						celescDados.setCodigo(listaCelescDados.get(i).getCodigo());
						driverOpen = true;
						celescDados.setImovel(listaCelescDados.get(i).getImovel());
						novaLista.add(celescDados);
					} else {
						if (iniciarPagina()) {
							try {
							getUnidade(listaCelescDados.get(i).getCodigo(), "debito", true, caminhoDir, driverOpen, true);
							celescDados.setCodigo(listaCelescDados.get(i).getCodigo());
							driverOpen = true;
							celescDados.setImovel(listaCelescDados.get(i).getImovel());
							novaLista.add(celescDados);
							}catch (Exception e) {
								driver.close();
								driver.quit();
							}
						} else {
							driverOpen = false;
						}
					}
				} else {
					novaLista.add(listaCelescDados.get(i));
				}
				posicao++;
			}
			if (driverOpen) {
				driver.close();
				driver.quit();
			}	
		}	
		posicao = 0;
		if (novaLista.size()>0) {
			List<Boletos> listaBoletos = new ArrayList<Boletos>();
			LerPDFCelesc lerPDFCelesc = new LerPDFCelesc();
			for (int i=0;i<novaLista.size();i++) {
				List<CelescFatura> listaFatura = novaLista.get(i).getListaFatura();
				if (listaFatura!=null) {
					if (listaFatura.size()>0) {
						LerPDFCelesc lerPdfCelesc = new LerPDFCelesc();
						for(int l=0;l<listaFatura.size();l++) {
							Boletos b = lerPDFCelesc.carregarPDF(listaFatura.get(l), caminhoDir, true, novaLista.get(i).getImovel());
							if (b!=null) {
								b.setCodigoImovel(String.valueOf(novaLista.get(i).getImovel()));
								b.setUC(novaLista.get(i).getCodigo());
								listaBoletos.add(b);
							}
						}
					}
				}
			}
			if (listaBoletos != null) {
				if (listaBoletos.size() > 0) {
					ExportarExcel ex = new ExportarExcel();
					//ex.gerarGTSimpificada(listaBoletos);
					ex.gerarOpCelesc(listaBoletos, caminhoDir, novaLista);
					File file = ex.getFile();
					URI uri = s3Service.uploadFile(file);
					r.setResultado("ok");
					return ResponseEntity.ok(novaLista);
				}
			}
		}
		return ResponseEntity.ok(novaLista);
	}
	
	
	
	
	public boolean iniciarPagina(){
        try {
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
        System.setProperty("webdriver.chrome.driver", "C:/Logs/drive/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        //options.addArguments("--headless");
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);

        driver.get("https://agenciaweb.celesc.com.br/AgenciaWeb/autenticar/loginPrestador.do");
        try {
          //  Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
        WebElement inputLogin = driver.findElement(By.name("login"));
        WebElement inputSenha = driver.findElement(By.name("senha"));
        inputLogin.sendKeys("IMA14A001");
        inputSenha.sendKeys("IMBI2010");
        WebElement botaoEntrar = driver.findElement(By.xpath("//*[@id=\"form0\"]/div[6]/input[1]"));
		botaoEntrar.click();
		return true;
        }catch (Exception e) {
			return false;
		}
        
        
    }
	
		
	public void getUnidade(String unidade, String opcao, boolean consHP, String diretorio, boolean driverOpen, boolean op) {
		try {
			if (driverOpen) {
				driver.get("https://agenciaweb.celesc.com.br/AgenciaWeb/autenticar/escolherUC.do");
				WebElement inputUnidade = driver.findElement(By.name("codUnCons"));
				inputUnidade.sendKeys(unidade);
				WebElement botaoEntrar = null;
				try {
					botaoEntrar = driver.findElement(By.xpath("//*[@id=\"fundoPrincipalLogin\"]/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[2]/td[2]/div/input"));
				} catch (Exception e) {
					botaoEntrar = driver.findElement(By.xpath("//*[@id=\"pg\"]/table[2]/tbody/tr[2]/td/form/table/tbody/tr[2]/td[2]/div/input"));
				}
				if (botaoEntrar!=null) {
					botaoEntrar.click();
				}
			} else {
			WebElement inputUnidade = driver.findElement(By.name("codUnCons"));
			inputUnidade.sendKeys(unidade);
			WebElement botaoEntrar = driver.findElement(By.xpath("//*[@id=\"fundoPrincipalLogin\"]/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[2]/td[2]/div/input"));
			
			botaoEntrar.click();
			}
		
            getDataCorte();
            if (opcao.equalsIgnoreCase("2via")) {
            	get2ViaBoleto(diretorio, op);
            	getDesligamento();
            } else if (opcao.equalsIgnoreCase("debito")) {
            	if (consHP) {
            		getDesligamento();
            		getDebitos(diretorio, op);
            	}
            } else if (opcao.equalsIgnoreCase("hp")) {
            	if (consHP) {
            		getDesligamento();
            		getHistoricoPagamento(diretorio, op);
            	}
            }  else if (opcao.equalsIgnoreCase("desligamento")) {
            	getDesligamento();
            }
        } catch (Exception ex) {
        	celescDados.setResultado("ERRO");
            Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void getDesligamento() {
		try {
		WebElement menuDesligamento = driver.findElement(By.xpath("//*[@id=\"mn\"]/table/tbody/tr[9]/td/a"));
		menuDesligamento.click();
		List<WebElement> listaElementos = driver.findElements(By.className("textoErroMensagem"));
		if (listaElementos!=null) {
			if (listaElementos.size()>0) {
				celescDados.setPedidodesligamento(listaElementos.get(0).getText());
			} else {
				celescDados.setPedidodesligamento("Unidade não possui pedido de desligamento");
			}
		} else {
			celescDados.setPedidodesligamento("Unidade não possui pedido de desligamento");
		}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
		
	public void getDataCorte() {
		WebElement elemento = driver.findElement(By.xpath("//*[@id=\"pg\"]/table[2]/tbody/tr[1]/td/fieldset[1]/legend"));
		if (elemento!=null) {
			List<WebElement> listaElementos = driver.findElements(By.className("textoGeral"));
			celescDados.setDataCorte(listaElementos.get(11).getText());
		}

	}
	
	public void get2ViaBoleto(String diretorio, boolean op) {
		List<HtmlElement> elements = pagina.getByXPath("//tr[@class='textoGeral']");
		WebElement menu2Via = driver.findElement(By.xpath("//*[@id=\"mn\"]/table/tbody/tr[3]/td/a"));
		menu2Via.click();
		List<WebElement> listaElementos = driver.findElements(By.className("textoGeral"));
		celescDados.setNome(listaElementos.get(0).getText());
		celescDados.setCpf(listaElementos.get(1).getText());
		celescDados.setCodigo(listaElementos.get(2).getText());
		celescDados.setEndereco(listaElementos.get(3).getText());
		celescDados.setCidade(listaElementos.get(4).getText());
		celescDados.setTelefone(listaElementos.get(5).getText());
		celescDados.setCelular(listaElementos.get(6).getText());
		celescDados.setFax(listaElementos.get(7).getText());
		celescDados.setEmail(listaElementos.get(8).getText());
		celescDados.setEmailfatura(listaElementos.get(9).getText());
		celescDados.setSituacao(listaElementos.get(10).getText());
		//celescDados.setDataCorte(listaElementos.get(11).getText());
		if (listaElementos.size()>=14) {
			celescDados.setListaFatura(new ArrayList<CelescFatura>());
			for (int i=11;i<listaElementos.size()-1;i++) {
				CelescFatura fatura = new CelescFatura();
				fatura.setMes(listaElementos.get(i).getText());
				i++;
				String dataVencimento = listaElementos.get(i).getText();
				dataVencimento = dataVencimento.replace(" ", "");
				fatura.setDataVencimento(dataVencimento);
				i++;
				fatura.setNumero(listaElementos.get(i).getText());
				i++;
				fatura.setValor(listaElementos.get(i).getText());
				celescDados.getListaFatura().add(fatura);
			}

		}
		if (celescDados.getListaFatura()!=null) {
			if (celescDados.getListaFatura().size()>0) {
				getPDF("2Via",0, diretorio, op);
			}
		}
		
	}
	
	public void getDebitos(String diretorio, boolean op) {
		WebElement menu2Via = driver.findElement(By.xpath("//*[@id=\"mn\"]/table/tbody/tr[4]/td/a"));
		menu2Via.click();
		List<WebElement> listaElementos = driver.findElements(By.className("textoGeral"));
		List<WebElement> listaLinks = driver.findElements(By.partialLinkText("https://agenciaweb.celesc.com.br/AgenciaWeb/imprimirSegundaVia/imprimirSegundaVia.do?MES_REF="));
		
		celescDados.setNome(listaElementos.get(0).getText());
		celescDados.setCpf(listaElementos.get(1).getText());
		celescDados.setCodigo(listaElementos.get(2).getText());
		celescDados.setEndereco(listaElementos.get(3).getText());
		celescDados.setCidade(listaElementos.get(4).getText());
		celescDados.setTelefone(listaElementos.get(5).getText());
		celescDados.setCelular(listaElementos.get(6).getText());
		celescDados.setFax(listaElementos.get(7).getText());
		celescDados.setEmail(listaElementos.get(8).getText());
		celescDados.setEmailfatura(listaElementos.get(9).getText());
		celescDados.setSituacao(listaElementos.get(10).getText());
		if (listaElementos.size()>=14) {
			celescDados.setListaFatura(new ArrayList<CelescFatura>());
			for (int i=11;i<listaElementos.size()-1;i++) {
				CelescFatura fatura = new CelescFatura();
				if (listaElementos.get(i).getText().equalsIgnoreCase("LIGADA")) {
					i++;
				}
				fatura.setMes(listaElementos.get(i).getText());
				i++;
				fatura.setDataVencimento(listaElementos.get(i).getText());
				i++;
				fatura.setNumero(listaElementos.get(i).getText());
				i++;
				fatura.setValor(listaElementos.get(i).getText());
				
				celescDados.getListaFatura().add(fatura);
			}

		}
		
		 if (celescDados.getListaFatura()!=null) { 
			 if (celescDados.getListaFatura().size()>0) { 
				 getPDF("debito", 0, diretorio, op); 
			 }else {
				celescDados.setResultado("SEM PDF");
			 }
		}else {
			celescDados.setResultado("SEM PDF");
		}
	}
	
	
	
	public void baixarPDF(WebClient webClient, String paginaTipo, String diretorio, boolean op) {
		int linha =0;
		int tab = 2;
		driver.get(paginaTipo);
		for (int i=0;i<this.celescDados.getListaFatura().size();i++) {
			try {
		    linha = linha + 3;
			String xpaht = "//*[@id=\"pg\"]/table[2]/tbody/tr[1]/td/table[" + tab + "]/tbody/tr[" + linha + "]/td[2]/a";
			WebElement botaoImprimir = driver.findElement(By.xpath(xpaht));
			String url = botaoImprimir.getAttribute("href").toString();
			celescDados.getListaFatura().get(i).setUrl(url);
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				linha = 0;
				tab++;
				i--;
			}
		}
		for (int i=0;i<this.celescDados.getListaFatura().size();i++) {
			try {
				String filename = diretorio;
				if (op) {
					filename = filename + this.celescDados.getImovel() + '_';
				}
				filename = filename + this.celescDados.getListaFatura().get(i).getNumero() + ".pdf";
				File file = new File(filename);
				InputStream is;
				driver.get(celescDados.getListaFatura().get(i).getUrl());
				is = webClient.getPage(driver.getCurrentUrl()).getWebResponse().getContentAsStream();
				OutputStream out = new FileOutputStream(file);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
				celescDados.setResultado("PDF SALVO");
			} catch (IOException ex) {
				Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);
				celescDados.setResultado("ERRO");
			} catch (FailingHttpStatusCodeException ex) {
				Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);
				celescDados.setResultado("ERRO");
			}		
			
			
	
		}
		
		
		
	}
	
	public void baixarPDFHistorico(WebClient webClient, String paginaTipo, int posicao) {
		//String diretorio= "c:\\logs\\celesc\\";
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		int linha =0;
			try {
				linha = posicao;
				posicao = posicao - 1;
				String xpaht = "//*[@id=\"histFat\"]/tbody/tr[" + linha + "]/td[2]/a";
			
				WebElement botaoImprimir = driver.findElement(By.xpath(xpaht));
				String url = botaoImprimir.getAttribute("href").toString();
				celescDados.getListaHistorico().get(posicao).setUrl(url);
			
			
			//Thread.sleep(1000);
			
				String ref = this.celescDados.getListaHistorico().get(posicao).getMesreferencia();
			    ref = ref.replace("/", "");
				String filename = diretorio + this.celescDados.getListaHistorico().get(posicao).getUc() + "_" + ref +".pdf";
				File file = new File(filename);
				InputStream is;
				driver.get(celescDados.getListaHistorico().get(posicao).getUrl());
				is = webClient.getPage(driver.getCurrentUrl()).getWebResponse().getContentAsStream();
				OutputStream out = new FileOutputStream(file);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
			} catch (IOException ex) {
				Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (FailingHttpStatusCodeException ex) {
				Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);	
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		
		
		
		
	}
	
	public void baixarPDFHistoricoTodos(WebClient webClient, String paginaTipo, String diretorio) {
		int linha =0;
		driver.get(paginaTipo);
		try {
				
		for (int i=1;i<celescDados.getListaHistorico().size();i++) {
				linha = i+1;
				String xpaht = "//*[@id=\"histFat\"]/tbody/tr[" + linha + "]/td[2]/a";
			
				WebElement botaoImprimir = driver.findElement(By.xpath(xpaht));
				String url = botaoImprimir.getAttribute("href").toString();
				celescDados.getListaHistorico().get(i).setUrl(url);
			}
		for (int i=1;i<celescDados.getListaHistorico().size();i++) {
				String ref = this.celescDados.getListaHistorico().get(i).getMesreferencia();
			    ref = ref.replace("/", "");
				String filename = diretorio + this.celescDados.getListaHistorico().get(i).getUc() + "_" + ref +".pdf";
				File file = new File(filename);
				InputStream is;
				driver.get(celescDados.getListaHistorico().get(i).getUrl());
				is = webClient.getPage(driver.getCurrentUrl()).getWebResponse().getContentAsStream();
				OutputStream out = new FileOutputStream(file);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
		}
			} catch (IOException ex) {
				Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);
			} catch (FailingHttpStatusCodeException ex) {
				Logger.getLogger(CelescController.class.getName()).log(Level.SEVERE, null, ex);	
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	
		
	
		
		
	}
	
	
	
	
	
	public void getPDF(String tipo, int posicao, String diretorio, boolean op) {

		try {
			webClient = new WebClient(BrowserVersion.CHROME);
			CookieManager cookieMan = new CookieManager();
			cookieMan = webClient.getCookieManager();
			cookieMan.setCookiesEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setTimeout(10000000);
			webClient.getOptions().setUseInsecureSSL(true);
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

			
			pagina = webClient.getPage("http://agenciaweb.celesc.com.br/AgenciaWeb/autenticar/loginPrestador.do");
			List<HtmlForm> formularios = pagina.getForms();
			HtmlForm formulario = pagina.getFormByName("LoginForm");
			HtmlInput inputLogin = formulario.getInputByName("login");
			HtmlInput inputSenha = formulario.getInputByName("senha");
			inputLogin.setValueAttribute("IMA14A001");
			inputSenha.setValueAttribute("IMBI2010");
			HtmlInput inputBotao = formulario.getInputByValue("Entrar");
			pagina = inputBotao.click();
			
			formulario = pagina.getFormByName("SelecionarUCForm");
			HtmlInput inputUC = formulario.getInputByName("codUnCons");
			inputUC.setValueAttribute(celescDados.getCodigo());
			inputBotao = formulario.getInputByValue("Entrar");
			pagina = inputBotao.click();
			String paginaTipo = "";
			if (tipo.equalsIgnoreCase("2Via")) {
				pagina = webClient.getPage(
						"https://agenciaweb.celesc.com.br/AgenciaWeb/imprimirSegundaVia/iniciarImprimirSegundaVia.do");
				paginaTipo = "https://agenciaweb.celesc.com.br/AgenciaWeb/imprimirSegundaVia/iniciarImprimirSegundaVia.do";
				baixarPDF(webClient, paginaTipo, diretorio, op);
			} else if (tipo.equalsIgnoreCase("debito")) {
				pagina = webClient
						.getPage("https://agenciaweb.celesc.com.br/AgenciaWeb/consultarDebito/consultarDebito.do");
				paginaTipo = "https://agenciaweb.celesc.com.br/AgenciaWeb/consultarDebito/consultarDebito.do";
				baixarPDF(webClient, paginaTipo, diretorio, op);
			} else if (tipo.equalsIgnoreCase("hp")) {
				pagina = webClient
						.getPage("https://agenciaweb.celesc.com.br/AgenciaWeb/consultarHistoricoPagto/consultarHistoricoPagto.do");
				paginaTipo = "https://agenciaweb.celesc.com.br/AgenciaWeb/consultarHistoricoPagto/consultarHistoricoPagto.do";
				if (posicao<0) {
					baixarPDFHistoricoTodos(webClient, paginaTipo, diretorio);
				} else baixarPDFHistorico(webClient, paginaTipo, posicao);
			} 
			
			webClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			celescDados.setResultado("ERRO");
			e.printStackTrace();
		}

	}
	
	public void getHistoricoPagamento(String diretorio, boolean op) {
		WebElement menu2Via = driver.findElement(By.xpath("//*[@id=\"mn\"]/table/tbody/tr[16]/td/a"));
		menu2Via.click();
		List<WebElement> listaElementos = driver.findElements(By.className("textoGeral"));
		List<WebElement> listaLinks = driver.findElements(By.partialLinkText("https://agenciaweb.celesc.com.br/AgenciaWeb/imprimirSegundaVia/imprimirSegundaVia.do?MES_REF="));
		
		celescDados.setNome(listaElementos.get(0).getText());
		celescDados.setCpf(listaElementos.get(1).getText());
		celescDados.setCodigo(listaElementos.get(2).getText());
		celescDados.setEndereco(listaElementos.get(3).getText());
		celescDados.setCidade(listaElementos.get(4).getText());
		celescDados.setTelefone(listaElementos.get(5).getText());
		celescDados.setCelular(listaElementos.get(6).getText());
		celescDados.setFax(listaElementos.get(7).getText());
		celescDados.setEmail(listaElementos.get(8).getText());
		celescDados.setEmailfatura(listaElementos.get(9).getText());
		celescDados.setSituacao(listaElementos.get(10).getText());
		int posicao = 1;
		int inicoFor = 14;
		if (listaElementos.size()>=15) {
			try {
				WebElement numeroPaginas = driver.findElement(By.xpath("//*[@id=\"pg\"]/table[2]/tbody/tr[6]/td/table[2]/tbody/tr[1]/td[2]/a[1]"));
				inicoFor = 15;
		}catch (Exception e) {
		    inicoFor = 14;
		}
			
			celescDados.setListaHistorico(new ArrayList<CelescHistorico>());
			for (int i=inicoFor;i<listaElementos.size()-3;i++) {
				CelescHistorico hp = new CelescHistorico();
				hp.setUc(listaElementos.get(i).getText());
				i++;
				hp.setMesreferencia(listaElementos.get(i).getText());
				i++;
				hp.setSituacao(listaElementos.get(i).getText());
				i++;
				hp.setDatavencimento(listaElementos.get(i).getText());
				i++;
				hp.setDatapagamento(listaElementos.get(i).getText());
				i++;
				hp.setValoremissao(listaElementos.get(i).getText());
				i++;
				hp.setValorpago(listaElementos.get(i).getText());
				hp.setPosicao(posicao);
				posicao++;
				celescDados.getListaHistorico().add(hp);
			}

		}
		if (celescDados.getListaHistorico()!=null) {
			if (celescDados.getListaHistorico().size()>0) {
				getPDF("hp", 1, diretorio, op);
			}
		}
	}
	
	
	
	

}
