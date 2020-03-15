package br.com.brognoli.casan.bean;

import java.io.File;
import java.io.FileNotFoundException;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import br.com.brognoli.casan.model.Fatura;
import br.com.brognoli.casan.model.Imovelcasan;


public class LerSite {

	private WebDriver driver;
	//private WebClient webClient; 
	private HtmlPage pagina;
	WebElement buttonSair;
	HtmlButton buttonLWebSair;
	private int linhaGeral = 0;
	
	public List<Imovelcasan> getBoletos(List<Imovelcasan> lista) {
	    int linha=linhaGeral;
	   // inicar();
	    //iniciarWebClinet();
	    for (int i=linha;i<lista.size();i++) {
	    	try {
	    		inicar();
	    		Thread.sleep(1000);
				if (logar(lista.get(i))) {
					lista.set(i,verificarSituacao(lista.get(i)));
					logof();
					
				
					
					} else {
						lista.get(i).setSituacao("Erro ao logar");
						
					}
					linhaGeral = i;
					driver.close();
	    		} catch (Exception e) {
	    			System.out.println(e.getMessage());
	    			lista.get(linha).setSituacao("Ocorreu erro " + e.getMessage());
	    			linhaGeral = i;
	    			driver.close();
	    		}
	    	
		}
		return lista;
	}
	
	public void iniciarWebClinet() {
		
	}
	
	public void inicar() {
		Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
        System.setProperty("webdriver.chrome.driver", "C:/Logs/drive/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://e.casan.com.br/segundavia/");
	}
	
	public boolean logarWebClient(Imovelcasan imovel, int i) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		/*pagina = null;
		pagina = webClient.getPage("https://e.casan.com.br/segundavia/");
		List<HtmlForm> formularios = pagina.getForms();
		HtmlForm formulario = formularios.get(0);
		if (formulario.equals("HtmlForm[<form action=\"/j_spring_security_logout\" method=\"post\">])")) {
			buttonLWebSair = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
			pagina = buttonLWebSair.click();
			pagina = webClient.getPage("https://e.casan.com.br/segundavia/");
			formularios = pagina.getForms();
			formulario = formularios.get(0);
		}
		HtmlInput inputCPF = formulario.getInputByName("j_username");
		inputCPF.setValueAttribute(imovel.getCpfcasan());
		HtmlInput inputMatricula = formulario.getInputByName("j_password");
		inputMatricula.setValueAttribute(imovel.getMatricula());
		HtmlButton button = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
		pagina = button.click();
		
		if (pagina.getUrl().toString().equals("https://e.casan.com.br/segundavia/")) {
			buttonLWebSair = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
			return true;
		}*/
		return false;

	}
	
	public boolean logar(Imovelcasan imovel) throws InterruptedException {
		String cpf;
		String matricula;
		if (imovel.getMatricula()==null) {
			return false;
		} else {
			matricula = imovel.getMatricula();
		}
		if (imovel.getCpfcasan()!=null) {
			cpf = imovel.getCpfcasan();
		}else if (imovel.getCpflocatario()!=null) {
			cpf = imovel.getCpflocatario();
		}else {
			return false;
		}
		
		
		
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		WebElement inputCPF = driver.findElement(By.id("j_username"));
		inputCPF.sendKeys(cpf);
		WebElement inputMatricula = driver.findElement(By.id("j_password"));
		inputMatricula.sendKeys(matricula);
		WebElement botaoLogin = driver.findElement(By.xpath("/html/body/div[3]/div/form/div/button"));
		botaoLogin.click();
		Thread.sleep(1000);
		if (driver.getCurrentUrl().equalsIgnoreCase("https://e.casan.com.br/loginincorreto")) {
			return false;
		}
		if (driver.getCurrentUrl().equalsIgnoreCase("https://e.casan.com.br/login")) {
			return false;
		}
		if (!driver.getCurrentUrl().equalsIgnoreCase("https://e.casan.com.br/segundavia/)")){
			buttonSair =   driver.findElement(By.xpath("/html/body/div[3]/div[1]/div/div[1]/form/input[2]"));
			return true;
		}
		return false;
	}
	
	public void logof() throws IOException {
		pagina = buttonLWebSair.click();
		buttonSair.click();
	}
	
	
	public Imovelcasan verificarSituacao(Imovelcasan imovel) throws FailingHttpStatusCodeException, MalformedURLException, IOException  {
		imovel.setListaFatura(new ArrayList<Fatura>());
		List<WebElement> listaElementos = driver.findElements(By.xpath("//*[@id=\"pendingBills\"]/p"));
		if ((listaElementos!=null) && (listaElementos.size()>0)) {
			if (listaElementos.get(0).getText().equalsIgnoreCase("Não existem faturas pendentes para este CPF/CNPJ relacionadas à esta unidade.")) {
				imovel.setSituacao("SEM DÉBITOS");
				return imovel;
				
			}
		} 
		
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
		// O CookieManager vai gerenciar os dados da sessão
		CookieManager cookieMan = new CookieManager();
		cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setTimeout(10000000);
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		
		pagina = null;
		pagina = webClient.getPage("https://e.casan.com.br/segundavia/");
		List<HtmlForm> formularios = pagina.getForms();
		HtmlForm formulario = formularios.get(0);
		if (formulario.equals("HtmlForm[<form action=\"/j_spring_security_logout\" method=\"post\">])")) {
			buttonLWebSair = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
			pagina = buttonLWebSair.click();
			pagina = webClient.getPage("https://e.casan.com.br/segundavia/");
			formularios = pagina.getForms();
			formulario = formularios.get(0);
		}
		HtmlInput inputCPF = formulario.getInputByName("j_username");
		inputCPF.setValueAttribute(imovel.getCpfcasan());
		HtmlInput inputMatricula = formulario.getInputByName("j_password");
		inputMatricula.setValueAttribute(imovel.getMatricula());
		HtmlButton button = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
		pagina = button.click();
		
		if (pagina.getUrl().toString().equals("https://e.casan.com.br/segundavia/")) {
			buttonLWebSair = formulario.getOneHtmlElementByAttribute("button", "type", "submit");
		}
		
		
		String atualURL = driver.getCurrentUrl();
		listaElementos = driver.findElements(By.className("billbox"));
		if ((listaElementos!=null) && (listaElementos.size()>0)) {
			//logarWebClient(imovel);
			for (int i = 1; i<= listaElementos.size();i++ ) {
				try {
				String xpaht = "//*[@id=\"pendingBills\"]/ul/li[" + i + "]/div/div[2]/p[2]/a[1]";
				WebElement botaoImprimir = driver.findElement(By.xpath(xpaht));
				String url = botaoImprimir.getAttribute("href").toString();
				Fatura fatura = new Fatura();
				fatura.setArquivo(salvarPDF(url, i, imovel, webClient));
				imovel.getListaFatura().add(fatura);
				} catch (Exception e) {
					System.out.println("Erro emitir pdf - " + e.getMessage());
				}
			}
			imovel.setSituacao("POSSUI DEBITOS");
			return imovel;
		}
		imovel.setSituacao("ERRO DESCONHECIDO");
		return imovel;
	}
	
	public String salvarPDF(String url, int i, Imovelcasan imovel, WebClient webClient) throws IOException {
		
		
		
		
		String filename = getNomePDF(url, imovel.getMatricula());
		File file = new File(filename);
		InputStream is;
		
		
		webClient.getPage("https://e.casan.com.br/segundavia/");
		is = webClient.getPage(url).getWebResponse().getContentAsStream();
         OutputStream out = new FileOutputStream(file);
						byte[] buffer = new byte[8 * 1024];
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							out.write(buffer, 0, bytesRead);
						}
						out.close();
		return filename;
		
	}
	
	public String getNomePDF(String url, String matricula) {
		String filename= "";
		for (int i=url.length()-3;i>0;i--) {
			if (url.charAt(i)!='/') {
				filename = url.charAt(i) + filename;
			}else {
				i = -1;
			}
		}
		filename = "c:\\logs\\casan\\" + matricula + "_" + filename + ".pdf";
		return filename;
	}
	
	public List<Imovelcasan> gerarExcelPDF(List<Imovelcasan> listaImoveis) throws Exception {
        
        List<Imovelcasan> novaListaImoveis = new ArrayList<Imovelcasan>();
		for (int i=0;i<listaImoveis.size();i++) {
			List<Fatura> novaListaFatura = new ArrayList<Fatura>();
        	for (int n=0; n<listaImoveis.get(i).getListaFatura().size();n++) {
        		if (listaImoveis.get(i).getListaFatura().get(n).getArquivo() !=null) {
        			File file = new File(listaImoveis.get(i).getListaFatura().get(n).getArquivo());
            		if (file!=null) {
            			Fatura fatura = lerPDF(file);
            			fatura.setArquivo(listaImoveis.get(i).getListaFatura().get(n).getArquivo());
                		System.out.println(i + "," + n);
                		if (fatura !=null) {
                			novaListaFatura.add(fatura);
                		}
            		}
        		}
        		//File file = new File("c:\\logs\\casan\\08510873_01-01-2020-1.pdf");
        	}
        	listaImoveis.get(i).setListaFatura(novaListaFatura);
        	novaListaImoveis.add(listaImoveis.get(i));
        }
        exportarExcel(listaImoveis);
        return listaImoveis;
	}
	
	
	public Fatura lerPDF(File file) throws IOException {
		 //File file = new File(path);
		 Fatura fatura;
	     PDDocument document = PDDocument.load(file);
	     if (!document.isEncrypted()) {
	    	 PDFTextStripperByArea stripper = new PDFTextStripperByArea();
             stripper.setSortByPosition(true);

             PDFTextStripper tStripper = new PDFTextStripper();

             String pdfFileInText = tStripper.getText(document);

             String lines[] = pdfFileInText.split("\\r?\\n");
             fatura = new Fatura();
             for (int i=0;i<lines.length-1;i++) {
            	 fatura.setMatricula(lines[34]);
            	 fatura.setReferencia(lines[17]);
            	 fatura.setDatavencimento(lines[47]);
            	 fatura.setNumeroHidrometro(lines[50]);
            	 fatura.setProprietario(lines[6].substring(0, (lines[6].length()-14)));
            	 fatura.setCpfProprietario(lines[6].substring((lines[6].length()-14), lines[6].length()));
            	 fatura.setUsuario(lines[23]);
            	 fatura.setCpfUsuario(lines[31]);
            	 fatura.setEndereco(lines[28]);
            	 fatura.setCep(lines[38]);
            	 fatura.setMunicipio(lines[56]);
            	 fatura.setValor(getValor(lines));
            	 fatura.setLinhaDigitavel(getLinhaDigitavel(lines));
             }
             
	     } else {
	    	 return null;
	     }
	    	 
	     return fatura;
	}
	
	public void exportarExcel(List<Imovelcasan> listaImoveis) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Debitos Casan");
		FileOutputStream fos = new FileOutputStream(new File("c:\\logs\\casan\\casan.xls"));
		try {
			
			int linha = 0;
			HSSFRow row = firstSheet.createRow(linha);
			row.createCell(0).setCellValue("Matricula");
			row.createCell(1).setCellValue("Mês Faturamento");
            row.createCell(2).setCellValue("Vencimento");
            row.createCell(3).setCellValue("Proprietario");
            row.createCell(4).setCellValue("CPF Proprietario");
            row.createCell(5).setCellValue("Usuario");
            row.createCell(6).setCellValue("CPF Usuario");
            row.createCell(7).setCellValue("Endereço");
            row.createCell(8).setCellValue("CEP");
            row.createCell(9).setCellValue("Municipio");
            row.createCell(10).setCellValue("Numero Hidrometro");
            row.createCell(11).setCellValue("Valor");
            row.createCell(12).setCellValue("Linha digitave");
			
			linha++;
			
			for (int i=0;i<listaImoveis.size();i++) {
	        	for (int n=0; n<listaImoveis.get(i).getListaFatura().size();n++) {
	        		Fatura fatura = listaImoveis.get(i).getListaFatura().get(n);
	        		row = firstSheet.createRow(linha);
	        		row.createCell(0).setCellValue(fatura.getMatricula());
	    			row.createCell(1).setCellValue(fatura.getReferencia());
	                row.createCell(2).setCellValue(fatura.getDatavencimento());
	                row.createCell(3).setCellValue(fatura.getProprietario());
	                row.createCell(4).setCellValue(fatura.getCpfProprietario());
	                row.createCell(5).setCellValue(fatura.getUsuario());
	                row.createCell(6).setCellValue(fatura.getCpfUsuario());
	                row.createCell(7).setCellValue(fatura.getEndereco());
	                row.createCell(8).setCellValue(fatura.getCep());
	                row.createCell(9).setCellValue(fatura.getMunicipio());
	                row.createCell(10).setCellValue(fatura.getNumeroHidrometro());
	                row.createCell(11).setCellValue(fatura.getValor());
	                row.createCell(12).setCellValue(fatura.getLinhaDigitavel());
	                linha++;
	        	}
	        }
			workbook.write(fos);

		}catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getLinhaDigitavel(String linha[]) {
		for(int i=0;i<linha.length;i++) {
			if (linha[i].charAt(0) == '8') {
				if (linha[i].length()==51) {
					 return linha[i];
				}
			} else {
				if (linha[i].equalsIgnoreCase("FATURA PARA SIMPLES CONFERÊNCIA - DÉBITO EM CONTA")) {
					return linha[i];
				}
			}
		}
		return "";
	}
	
	public String getValor(String linha[]) {
		for(int i=0;i<linha.length;i++) {
			if (linha[i].equalsIgnoreCase("USO BANCO")) {
				return linha[i+1];
			}
		}
		return "";
	}
	
	
	
}
