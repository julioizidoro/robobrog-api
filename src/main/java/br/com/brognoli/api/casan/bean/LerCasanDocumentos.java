package br.com.brognoli.api.casan.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import br.com.brognoli.api.casan.model.Fatura;
import br.com.brognoli.api.casan.model.Imovelcasan;

public class LerCasanDocumentos {
	
	private WebDriver driver;
	//private WebClient webClient; 
	private HtmlPage pagina;
	WebElement buttonSair;
	HtmlButton buttonLWebSair;
	private int linhaGeral = 0;
	private File file;
	//String diretorio = "C:\\Logs\\casan\\";
	String diretorio = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\casan\\";
	
	public Imovelcasan getCertidaoNegativa(Imovelcasan imovel) {
		inicar("https://e.casan.com.br/certidaonegativa/");
		try {
			Thread.sleep(1000);
			String resultado = validarDadosLogin(imovel);
			if (resultado.equals("OK")) {
				if (logar(imovel, "e.casan.com.br/certidaonegativa/")) {
					WebElement elemento = null;
					try {
						elemento = driver.findElement(By.xpath("/html/body/div[3]/div[4]/div/h4"));
					}catch (Exception e) {
						// TODO: handle exception
					}
					if (elemento!=null) {
						imovel.setSituacao("Para emitir a certidão você precisa regularizar as pendencais");
					} else {
						imovel = verificarSituacao(imovel, "https://e.casan.com.br/certidaonegativa/");
						if (!imovel.getSituacao().equalsIgnoreCase("Possui debitos")) {
							imovel.setSituacao(resultado);
						}
					}
    				logof();
    			} else {
					imovel.setSituacao("ERRO AO LOGAR E-CASAN");
				}
			} else {
				imovel.setSituacao(resultado);			
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imovel;
	}
	
	public Imovelcasan getQuitacaoAnual(Imovelcasan imovel) {
		inicar("https://e.casan.com.br/quitacaoanual/");
		try {
			Thread.sleep(1000);
			String resultado = validarDadosLogin(imovel);
			if (resultado.equals("OK")) {
				if (logar(imovel, "e.casan.com.br/quitacaoanual/")) {
					WebElement elemento = null;
					try {
						elemento = driver.findElement(By.xpath("/html/body/div[3]/div[4]/div/div[1]/h4"));
					}catch (Exception e) {
						
					}
					if (elemento!=null) {
						imovel.setSituacao("Nao é possível emitir declarações de quitação anual de débitos para essa unidade usuária. Não há informações de faturamento em 2020.");
					} else {
						imovel = verificarSituacao(imovel, "https://e.casan.com.br/quitacaoanual/");
						if (!imovel.getSituacao().equalsIgnoreCase("Possui debitos")) {
							imovel.setSituacao(resultado);
						}
					}
    				logof();
				} else {
					imovel.setSituacao("ERRO AO LOGAR E-CASAN");
				}
			} else {
				imovel.setSituacao(resultado);			
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imovel;
	}
	
	public void inicar(String site) {
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
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(site);
	}
	
	public String validarDadosLogin(Imovelcasan imovel) throws InterruptedException {
		String resultado = "";
	    resultado = validarMatricula(imovel.getMatricula());
		if (imovel.getCpfcasan()==null) {
			resultado = resultado + " / CPF INVÁLIDO" ;
		}
		return resultado;
	}
	
	public boolean logar(Imovelcasan imovel, String site) throws InterruptedException {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		WebElement inputCPF = driver.findElement(By.id("j_username"));
		inputCPF.sendKeys(imovel.getCpfcasan());
		WebElement inputMatricula = driver.findElement(By.id("j_password"));
		inputMatricula.sendKeys(imovel.getMatricula());
		WebElement botaoLogin = driver.findElement(By.xpath("/html/body/div[3]/div/form/div/button"));
		botaoLogin.click();
		Thread.sleep(1000);
		if (driver.getCurrentUrl().equalsIgnoreCase("https://e.casan.com.br/loginincorreto")) {
			return false;
		}
		if (driver.getCurrentUrl().equalsIgnoreCase("https://e.casan.com.br/login")) {
			return false;
		}
		if (!driver.getCurrentUrl().equalsIgnoreCase(site)){
			//buttonSair =   driver.findElement(By.xpath("/html/body/div[3]/div[1]/div/div[1]/form/input[2]"));
			return true;
		}
		return false;
	}
	
	public void logof() throws IOException {
		driver.close();
		driver.quit();
	}
	
	public Imovelcasan verificarSituacao(Imovelcasan imovel, String site) throws FailingHttpStatusCodeException, MalformedURLException, IOException  {
		
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
		// O CookieManager vai gerenciar os dados da sessão
		CookieManager cookieMan = new CookieManager();
		cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setTimeout(10000000);
		webClient.getOptions().setUseInsecureSSL(true);
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		
		pagina = null;
		pagina = webClient.getPage(site);
		List<HtmlForm> formularios = pagina.getForms();
		HtmlForm formulario = formularios.get(0);
		if (formulario.equals("HtmlForm[<form action=\"/j_spring_security_logout\" method=\"post\">])")) {
			buttonLWebSair = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
			pagina = buttonLWebSair.click();
			pagina = webClient.getPage("site");
			formularios = pagina.getForms();
			formulario = formularios.get(0);
		}
		HtmlInput inputCPF = formulario.getInputByName("j_username");
		inputCPF.setValueAttribute(imovel.getCpfcasan());
		HtmlInput inputMatricula = formulario.getInputByName("j_password");
		inputMatricula.setValueAttribute(imovel.getMatricula());
		HtmlButton button = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
		pagina = button.click();
		
		if (pagina.getUrl().toString().equals(site)) {
			buttonLWebSair = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
		}
		if (site.equalsIgnoreCase("https://e.casan.com.br/certidaonegativa/")) {
			String resultado = salvarPDF("https://e.casan.com.br/certidaonegativa/download/", imovel, webClient,  "_certidao");
			imovel.setSituacao(resultado);
		}else {
			String resultado = salvarPDF("https://e.casan.com.br/quitacaoanual/download", imovel, webClient, "_quitacao");
			imovel.setSituacao(resultado);
		}
		
		
		
		return imovel;
	}
	
	public String salvarPDF(String url, Imovelcasan imovel, WebClient webClient, String nome ) throws IOException {
		
		nome = imovel.getMatricula() + nome + ".pdf";
		//String filename = getNomePDF(url, nome);
		File file = new File(diretorio+nome);
		InputStream is;
		
		try {
			webClient.getPage(url);
		
			is = webClient.getPage(url).getWebResponse().getContentAsStream();
			OutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			if ((bytesRead = is.read(buffer)) != -1) {
				while ((bytesRead = is.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
			}else {
				out.close();
				return "Possui debitos";
			}
		} catch (Exception e) {
			return "Possui debitos";
		}
		return nome;
		
	}
	
	public String validarMatricula(String matricula) {
		
		 if (matricula==null) {
			 return "MATRÚCLA INVÁLIDA LINLK";
		 } else if (matricula.equalsIgnoreCase("COND")) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.equalsIgnoreCase("KITNET") ) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.equalsIgnoreCase("X")) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.equalsIgnoreCase("CONDOMINIO")) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.equalsIgnoreCase("COND.")) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.equalsIgnoreCase("APARTAMENTO")) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.equalsIgnoreCase("RATEIO")) {
			return "MATRÚCLA INVÁLIDA LINLK";
		} else if (matricula.length()<=4) {
			return "MATRÚCLA INVÁLIDA LINLK";
		}
		return "OK";
	}

}
