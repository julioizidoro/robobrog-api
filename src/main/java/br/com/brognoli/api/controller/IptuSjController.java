package br.com.brognoli.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.javascript.host.Element;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.api.bean.Cobrancaresultado;
import br.com.brognoli.api.bean.Diretorios;
import br.com.brognoli.api.bean.ExportarExcel;
import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.Carne;
import br.com.brognoli.api.model.CarneIPTU;
import br.com.brognoli.api.model.Imoveladm;
import br.com.brognoli.api.model.Linhas;
import br.com.brognoli.api.model.Listapdf;
import br.com.brognoli.api.model.Resposta;
import br.com.brognoli.api.service.S3Service;
import br.com.brognoli.api.util.Conversor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.validation.Valid;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@SuppressWarnings("unused")
@CrossOrigin
@RestController
@RequestMapping("/iptusj")

public class IptuSjController {
	
	private List<Carne> listaCarne;
	private WebDriver driver;
	private int contador;
    private int numeroLinha;
    private boolean dataSetada;
    @Autowired
	private S3Service s3Service;
    private String caminhoDir="\\\\192.168.1.58\\documentos\\centralfinanceira\\BOLETOS DE CONDOMÍNIOS\\iptusj\\";
    //private String caminhoDir="c:\\logs\\julio\\";
    List<CarneIPTU> listaIPTU;
    
	@PostMapping("/gerarlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void lerXLSX(@RequestParam(name="file") MultipartFile file) throws IOException, InvalidFormatException {
        listaCarne = new ArrayList<Carne>();
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
             Carne carne = new Carne();
             carne.setSituacao("Carregado");
             while (celulas.hasNext()) {
                XSSFCell celula = (XSSFCell) celulas.next();
                int z = celula.getColumnIndex();
                 switch (z) {
                     case 0:
                         carne.setInscricao(celula.toString());
                     case 1:
                         if (CellType.NUMERIC.equals(celula.getCellTypeEnum())) {
                        	 int valor = (int) celula.getNumericCellValue();
                        	 String digitos= Integer.toString(valor);
                        	 carne.setCadastro(digitos);
                         }
                 }
             }
             listaCarne.add(carne);
         }
    }
	
	@GetMapping("/getlista")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Carne>> getListarCarne() {
		return ResponseEntity.ok(listaCarne);
	}
	
	
	@GetMapping("/lersite/{datavencimento}/{repeticao}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Carne>> getSiteSJ(@PathVariable("datavencimento") String datavencimento, @PathVariable("repeticao") int repeticao) {
		dataSetada = false;
		String sdataVencimento = datavencimento.substring(6,8) + "/" +  datavencimento.substring(4,6) + "/" + datavencimento.substring(0,4);
		//numeroLinha = 0;//Integer.parseInt(jTxtNumero.getText());
		numeroLinha = 0;
		contador = 0;
		iniciarPagina();
		
		for (int r=0;r<repeticao;r++) {
		for(int i = numeroLinha;i<listaCarne.size();i++){    
			String situacao ="ERRO";
			try {
				
				if ((listaCarne.get(i).getSituacao().equalsIgnoreCase("Carregado")) || (listaCarne.get(i).getSituacao().equalsIgnoreCase("ERRO"))|| (listaCarne.get(i).getSituacao().equalsIgnoreCase("ERRO PDF"))) {
					File f = new File(caminhoDir + listaCarne.get(i).getInscricao() + "_20201.pdf");
					if (!f.exists()) {
						situacao = lerSitePMSJ(listaCarne.get(i).getCadastro(), sdataVencimento, listaCarne.get(i).getInscricao());
						listaCarne.get(i).setSituacao(situacao);
					}else {
						listaCarne.get(i).setSituacao("PDF SALVO");
					}
					numeroLinha = i;
				}
			} catch (Exception ex) {
				listaCarne.get(i).setSituacao(situacao);
				System.out.println("Parou no Erro" + i);
				System.out.println("Erro" + ex);
				try {
					driver.close();
					driver.quit();
				} catch (Exception e) {
					System.out.println("Erro ao fechar driver");
				}
				dataSetada = false;
				iniciarPagina();
				numeroLinha = i;
				//jTxtNumero.setText(String.valueOf(i));
			}
		}
		}
		driver.close();
		driver.quit();
		return ResponseEntity.ok(listaCarne);
	}
    
    public void iniciarPagina(){
        
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
        driver.get("https://www.saojose.sc.gov.br/");
        WebElement menu2Via = driver.findElement(By.xpath("//*[@id=\"navbar\"]/ul/li[2]/a"));
        menu2Via.click();
        menu2Via = driver.findElement(By.xpath("//*[@id=\"navbar\"]/ul/li[2]/ul/li[5]/a"));
        //menu2Via.click();
        driver.get("https://e-gov.betha.com.br/cdweb/resource.faces?params=q6hNJ_ppyGjqTvq_054cPQ==");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        menu2Via = driver.findElement(By.xpath("//*[@id=\"modoAcesso\"]/div[1]/div[4]/div/div/div/a"));
        jse.executeScript("arguments[0].click();", menu2Via);
        menu2Via.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String lerSitePMSJ(String cadastro, String datavencmento, String inscricao)throws Exception{
    	String situacao = "";
        WebElement menu2Via = driver.findElement(By.id("mainForm:iImoveis"));
        Thread.sleep(1000);
        menu2Via.clear();
        menu2Via.sendKeys(cadastro);
        menu2Via = driver.findElement(By.id("mainForm:btIImoveis"));
        menu2Via.click();
        Thread.sleep(1000);
        if (!dataSetada) {
        	WebElement campoData = driver.findElement(By.xpath("//*[@id=\"mainForm:data\"]"));
        	campoData.clear();
        	campoData.sendKeys(datavencmento);
        	String dataCampo = campoData.getAttribute("value");
        	while (!datavencmento.equalsIgnoreCase(dataCampo)) {
        		campoData.clear();
        		campoData.sendKeys(datavencmento);
        		dataCampo = campoData.getAttribute("value");
        	}
        	dataSetada = true;
        	campoData = driver.findElement(By.xpath("//*[@id=\"mainForm:btAtualizaVlr\"]/div/div/label/strong"));
        	campoData.click();
        }
        List<WebElement> lista = driver.findElements(By.xpath("//*[@id=\"mainForm:master:messageSection:warn\"]"));
       boolean temRegistro = false;
       if ((lista == null) || (lista.size() == 0)){
           temRegistro = false;
       } else if (lista.get(0).getText().contains("Não foi encontrado nenhum lançamento de IPTU para o contribuinte")) {
           situacao = "Não foi encontrado nenhum lançamento"; 
    	   temRegistro = false;
       } else {
           temRegistro = true;
       }
        if (temRegistro) {
            Thread.sleep(2000);
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse = (JavascriptExecutor) driver;
            menu2Via = driver.findElement(By.id("P0"));
            jse.executeScript("arguments[0].click();", menu2Via);
            //menu2Via.click();
            Thread.sleep(1000);
            jse = (JavascriptExecutor) driver;
            jse.executeScript("scrollBy(0,250)", "");
            jse = (JavascriptExecutor) driver;
            WebElement botaoEnviar;
            List<WebElement> listaCheck = driver.findElements(By.xpath("//*[@id=\"mainForm:P:0:F:1:F\"]"));
            if ((listaCheck == null) || (listaCheck.size() == 0)){
                listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                if ((listaCheck != null) && (listaCheck.size() == 1)){
                    listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                    listaCheck.get(0).click();     
                    botaoEnviar = driver.findElement(By.id("mainForm:emitir"));
                    botaoEnviar.click();
                    situacao = salvarPDF(inscricao + "_20201");
                }  else {
                    listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                    if ((listaCheck != null) && (listaCheck.size() == 2)){
                        menu2Via = driver.findElement(By.xpath("//*[@id=\"mainForm:P:0:F:0:resumo:0:selectedUnica\"]"));
                        menu2Via.click();
                        listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                        listaCheck.get(0).click();     
                        botaoEnviar = driver.findElement(By.id("mainForm:emitir"));
                        botaoEnviar.click();
                        situacao = salvarPDF(inscricao + "_20201");
                        
                        menu2Via = driver.findElement(By.xpath("//*[@id=\"mainForm:P:0:F:0:resumo:1:selectedUnica\"]"));
                        menu2Via.click();
                        listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                        listaCheck.get(1).click();
                        botaoEnviar = driver.findElement(By.id("mainForm:emitir"));
                        botaoEnviar.click();
                        situacao = salvarPDF(inscricao + "_20202");
                        //listaCheck.get(1).click();
                    }
                }
            }else {
                //check2019 1 
                listaCheck = driver.findElements(By.xpath("//*[@id=\"mainForm:P:0:F:0:resumo:1:selectedUnica\"]"));
                if ((listaCheck != null) || (listaCheck.size() > 0)){
                	List<WebElement> listaElementos = driver.findElements(By.className("parcela"));
                	if (listaElementos.size()==3) {
                		menu2Via = driver.findElement(By.xpath("//*[@id=\"mainForm:P:0:F:0:resumo:1:selectedUnica\"]"));
                		menu2Via.click();
                		listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                		listaCheck.get(1).click();
                	} else {
                		List<WebElement> listaPlots = driver.findElements(By.className("plots"));
                		menu2Via = listaPlots.get(0);
                		System.out.println(listaPlots.get(0).getAttribute("id"));
                        menu2Via.click();
                        listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                        //listaCheck.get(1).click();
	
                	}	
                    botaoEnviar = driver.findElement(By.id("mainForm:emitir"));
                  
                    botaoEnviar.click();
                    situacao = salvarPDF(inscricao + "_20191");
                    //listaCheck.get(1).click();
                }
             /*   listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                if ((listaCheck != null) || (listaCheck.size() >= 0)){
                    menu2Via = driver.findElement(By.xpath("//*[@id=\"mainForm:P:0:F:0:resumo:0:selectedUnica\"]"));
                    menu2Via.click();
                    listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                    listaCheck.get(0).click();
                    botaoEnviar = driver.findElement(By.id("mainForm:emitir"));
                    botaoEnviar.click();
                    situacao = salvarPDF(inscricao + "_20191");
                    listaCheck.get(0).click();
                }  */
                listaCheck = driver.findElements(By.xpath("//*[@id=\"selectAll\"]"));
                if (listaCheck.size()==3) {
                    listaCheck.get(2).click();
                }else if(listaCheck.size()==2) {
                    listaCheck.get(1).click();
                } else {
                    listaCheck.get(0).click();
                }
                botaoEnviar = driver.findElement(By.id("mainForm:emitir"));
                botaoEnviar.click();
                situacao =  salvarPDF(inscricao + "_20191");
            }    
            
            WebElement botaoNovaConsulta = driver.findElement(By.xpath("//*[@id=\"mainForm:novaConsulta\"]/span/input"));
            botaoNovaConsulta.click();
            
            
            
           //  driver.quit();
        } else {
        	//situacao = "SEM BOLETO";
            WebElement botaoNovaConsulta = driver.findElement(By.xpath("//*[@id=\"mainForm:novaConsulta\"]/span/input"));
            botaoNovaConsulta.click();
        }
        return situacao;
    }
    
    public boolean validarUnica2(List<WebElement> listaPlost) {
    	for (int i=0;i<listaPlost.size();i++) {
    		System.out.println(listaPlost.get(i).getText());
    		if (listaPlost.get(i).getText().contains("Unica2")) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public String salvarPDF(String inscricao){
    	String situacao ="";
        //menu2Via.click();
        String winHableAtual;
        String winHandleBefore = driver.getWindowHandle();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        boolean paginaImpressao = false;
        int contador = 0;
        while ((paginaImpressao==false) && (contador<100)) {
        	try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
            }
           if (driver.getCurrentUrl().contains("reportasync.faces/anonymous_guia-iptu")) {
               paginaImpressao = true;
               contador = 100;
           } else {
                contador ++;
                
           }
            
        }
        if (paginaImpressao) {
            winHableAtual = driver.getWindowHandle();
            baixarPDF(driver.getCurrentUrl(), inscricao);
            driver.switchTo().window(winHableAtual).close();
            driver.switchTo().window(winHandleBefore);
            return "PDF SALVO";
        }else {
            winHableAtual = driver.getWindowHandle();
            driver.switchTo().window(winHableAtual).close();
            driver.switchTo().window(winHandleBefore);
            return "ERRO PDF";
        }
    }
    
    public void baixarPDF(String url, String nome) {
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
		String fileName = nome + ".pdf";
		fileName = fileName.replace("/", " ");
		fileName = "\\\\192.168.1.58\\documentos\\centralfinanceira\\BOLETOS DE CONDOMÍNIOS\\iptusj\\" + fileName;
		File file = new File(fileName);
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
		} catch (IOException ex) {
			Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FailingHttpStatusCodeException ex) {
			Logger.getLogger(IptuSjController.class.getName()).log(Level.SEVERE, null, ex);
		}				
    }
    
	public void gerarDadosPDFSJ(List<Linhas> lines, String nomeArquivo) {
		CarneIPTU carne = new CarneIPTU();
		boolean novo = false;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).getLinha();
			if (line.contains("Página: ")) {
				if (carne.getLinhaDigitavel()!=null) {
					listaIPTU.add(carne);
				}
			    carne = new CarneIPTU();
			}
			
			if (line.equalsIgnoreCase("(-) Desconto")) {
				String linhaDigitavel = lines.get(i + 1).getLinha();
				linhaDigitavel = linhaDigitavel.replace(" ", "");
				linhaDigitavel = linhaDigitavel.replace(".", "");
				carne.setLinhaDigitavel(linhaDigitavel);
			}
			
			if (line.equalsIgnoreCase("IMÓVEL")) {
				carne.setVencimento(lines.get(i - 1).getLinha());
				carne.setParcela(lines.get(i + 3).getLinha());
			}
			
			if (line.length() > 18) {
				if (line.substring(0, 19).equalsIgnoreCase("VENCIMENTO ORIGINAL")) {
					String valor = lines.get(i + 1).getLinha();
					valor = valor.replace(".", "");
					valor = valor.replace(",", ".");
					carne.setValor(Float.parseFloat(valor));
				}
			}
		
			if (line.contains("ACRÉSCIMOS/JURO/MULTA")) {
				String juros = line;
				juros = juros.replace("ACRÉSCIMOS/JURO/MULTA", "");
				juros = juros.replace(" ", "");
				juros = juros.replace(".", "");
				juros = juros.replace(",", ".");
				if (juros.length()>0) {
					carne.setJuros(Float.parseFloat(juros));
				}else carne.setJuros(0.0f);
				
				novo = true;
				
				String inscricao = "";
				for (int l= 0;l<nomeArquivo.length();l++) {
					if (nomeArquivo.charAt(l)!= '_') {
						inscricao = inscricao + nomeArquivo.charAt(l);
					}else {
						l = nomeArquivo.length() + 100;
					}
				}
				inscricao = inscricao.replace(".pdf", "");
				carne.setInscricaoMascara(inscricao);
				carne.setInscricao(inscricao.replace(".", ""));				
			}
			

		}

	}
	
	@GetMapping("/exportarexcel")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<File> UploadExportarExcel() {
		listaIPTU = new ArrayList<CarneIPTU>();
		Resposta r = new Resposta();
		r.setResultado("erro");
		File fileDiretorio = new File(caminhoDir); 
		File[] files = fileDiretorio.listFiles();
		for (File file : files) {
			List<Linhas> lines = new ArrayList<Linhas>();
			try {
				PdfReader reader = new PdfReader(caminhoDir + file.getName());
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

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				gerarDadosPDFSJ(lines, file.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (listaIPTU != null) {
			if (listaIPTU.size() > 0) {
				ExportarExcel ex = new ExportarExcel();
				ex.exportarResultadoExcelSJ(listaIPTU, caminhoDir, listaCarne);
				File file = ex.getFile();
				URI uri = s3Service.uploadFile(file);
				r.setResultado("ok");
				return ResponseEntity.ok(file);
			}
		}
		return ResponseEntity.ok(null);
	}
	
	public String getLinhaDigitavel(List<Linhas> lines) {
		String linhaDigitavel= "";
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).getLinha();
			
		}
		return linhaDigitavel;
	}
	
	
	
	

}
