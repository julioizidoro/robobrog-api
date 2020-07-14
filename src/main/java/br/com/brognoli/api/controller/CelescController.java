package br.com.brognoli.api.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import br.com.brognoli.api.casan.model.Fatura;
import br.com.brognoli.api.model.CelescDados;
import br.com.brognoli.api.model.CelescFatura;

@CrossOrigin
@RestController
@RequestMapping("/celesc")
public class CelescController {
	
	private WebDriver driver;
	private CelescDados celescDados;
	HtmlPage pagina;
	
	@GetMapping("/{opcao}/{unidade}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CelescDados> get2Via(@PathVariable("opcao") String opcao, @PathVariable("unidade") String unidade) {
		celescDados = new CelescDados();
		iniciarPagina();
		getUnidade(unidade, opcao);
		driver.close();
		return ResponseEntity.ok(celescDados);
	}
	
	public void iniciarPagina(){
        
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
        System.setProperty("webdriver.chrome.driver", "C:/Logs/drive/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        driver.get("https://agenciaweb.celesc.com.br/AgenciaWeb/autenticar/loginPrestador.do");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
        WebElement inputLogin = driver.findElement(By.name("login"));
        WebElement inputSenha = driver.findElement(By.name("senha"));
        inputLogin.sendKeys("IMA14A001");
        inputSenha.sendKeys("BRIM2020");
        WebElement botaoEntrar = driver.findElement(By.xpath("//*[@id=\"form0\"]/div[6]/input[1]"));
		botaoEntrar.click();
        System.out.println(driver.getCurrentUrl());
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	
	public void getUnidade(String unidade, String opcao) {
		try {
			WebElement inputUnidade = driver.findElement(By.name("codUnCons"));
			inputUnidade.sendKeys(unidade);
			WebElement botaoEntrar = driver.findElement(By.xpath("//*[@id=\"fundoPrincipalLogin\"]/table/tbody/tr/td/table/tbody/tr/td/form/table/tbody/tr[2]/td[2]/div/input"));
			botaoEntrar.click();
		
            Thread.sleep(1000);
            getDataCorte();
            Thread.sleep(1000);
            if (opcao.equalsIgnoreCase("2via")) {
            	get2Via();
            } else if (opcao.equalsIgnoreCase("debito")) {
            	getDebitos();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void getDataCorte() {
		WebElement elemento = driver.findElement(By.xpath("//*[@id=\"pg\"]/table[2]/tbody/tr[1]/td/fieldset[1]/legend"));
		if (elemento!=null) {
			List<WebElement> listaElementos = driver.findElements(By.className("textoGeral"));
			celescDados.setDataCorte(listaElementos.get(11).getText());
		}

	}
	
	public void get2Via() {
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
				getPDF("2Via");
			}
		}
		
	}
	
	public void getDebitos() {
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
				getPDF("debito");
			}
		}
		
		
	}
	
	public void baixarPDF(WebClient webClient, String paginaTipo) {
		//String diretorio= "c:\\logs\\celesc\\";
		String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\celesc\\";
		int linha =0;
		for (int i=0;i<this.celescDados.getListaFatura().size();i++) {
			try {
		    linha = linha + 3;
			String filename = diretorio + this.celescDados.getListaFatura().get(i).getNumero() + ".pdf";
			File file = new File(filename);
			InputStream is;
		    driver.get(paginaTipo);
		    Thread.sleep(1000);
		    
			String xpaht = "//*[@id=\"pg\"]/table[2]/tbody/tr[1]/td/table[2]/tbody/tr[" + linha + "]/td[2]/a";
			
			WebElement botaoImprimir = driver.findElement(By.xpath(xpaht));
			String url = botaoImprimir.getAttribute("href").toString();
			driver.get(url);
			Thread.sleep(1000);
			try {
				is = webClient.getPage(driver.getCurrentUrl()).getWebResponse().getContentAsStream();
				Thread.sleep(1000);
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
			}		
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	
	public void getPDF(String tipo) {

		try {
			WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
			CookieManager cookieMan = new CookieManager();
			cookieMan = webClient.getCookieManager();
			cookieMan.setCookiesEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setTimeout(10000000);
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

			
			pagina = webClient.getPage("https://agenciaweb.celesc.com.br/AgenciaWeb/autenticar/loginPrestador.do");
			List<HtmlForm> formularios = pagina.getForms();
			HtmlForm formulario = pagina.getFormByName("LoginForm");
			HtmlInput inputLogin = formulario.getInputByName("login");
			HtmlInput inputSenha = formulario.getInputByName("senha");
			inputLogin.setValueAttribute("IMA14A001");
			inputSenha.setValueAttribute("BRIM2020");
			HtmlInput inputBotao = formulario.getInputByValue("Entrar");
			pagina = inputBotao.click();
			
			formulario = pagina.getFormByName("SelecionarUCForm");
			HtmlInput inputUC = formulario.getInputByName("codUnCons");
			inputUC.setValueAttribute(celescDados.getCodigo());
			inputBotao = formulario.getInputByValue("Entrar");
			pagina = inputBotao.click();
			System.out.println(pagina.getBaseURI());
			String paginaTipo = "";
			System.out.println(pagina.getUrl());
			if (tipo.equalsIgnoreCase("2Via")) {
				pagina = webClient.getPage(
						"https://agenciaweb.celesc.com.br/AgenciaWeb/imprimirSegundaVia/iniciarImprimirSegundaVia.do");
				paginaTipo = "https://agenciaweb.celesc.com.br/AgenciaWeb/imprimirSegundaVia/iniciarImprimirSegundaVia.do";
			} else if (tipo.equalsIgnoreCase("debito")) {
				pagina = webClient
						.getPage("https://agenciaweb.celesc.com.br/AgenciaWeb/consultarDebito/consultarDebito.do");
				paginaTipo = "https://agenciaweb.celesc.com.br/AgenciaWeb/consultarDebito/consultarDebito.do";
			}
			System.out.println(pagina.getUrl());
			baixarPDF(webClient, paginaTipo);
			webClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
