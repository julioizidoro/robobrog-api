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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.api.bean.ExportarExcel;
import br.com.brognoli.api.model.BoletoAmbiental;
import br.com.brognoli.api.model.Carne;
import br.com.brognoli.api.model.CarneIPTU;
import br.com.brognoli.api.model.Linhas;
import br.com.brognoli.api.model.Resposta;



@CrossOrigin("*")
@RestController
@RequestMapping("/ambiental")
public class AmbientalController {
	
	private List<Carne> listaCarne;
	private WebDriver driver;
	 private int contador;
	 List<BoletoAmbiental> listaAmbiental;
	 
	 private String caminhoDir="\\\\192.168.1.58\\documentos\\centralfinanceira\\BOLETOS DE CONDOMÍNIOS\\ambiental\\";
	
	@GetMapping("sj/getlista")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Carne>> getListaCarne() {
		return ResponseEntity.ok(listaCarne);
	}
	
	@PostMapping("sj/gerarlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void listarCobranca(@RequestParam(name="file") MultipartFile file) {
		try {
			this.contador = 0;
			lerXLSX(file);
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void lerXLSX(MultipartFile file) throws IOException, InvalidFormatException {
        listaCarne = new ArrayList<Carne>();
        InputStream is = file.getInputStream();
        OPCPackage pkg = OPCPackage.open(is);
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
	
	@GetMapping("sj/lersite/{datavencimento}")
	@ResponseStatus(HttpStatus.CREATED)
	public void iniciarAmbiental(@PathVariable("datavencimento") String datavencimento) {
        // TODO add your handling code here:
		String sdataVencimento = datavencimento.substring(6,8) + "/" +  datavencimento.substring(4,6) + "/" + datavencimento.substring(0,4);
        System.setProperty("webdriver.chrome.driver", "C:/Logs/drive/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        //driver.get("http://ru.ambsc.com.br/atendimento/Identificacao.aspx");
        for (int i=contador;i<listaCarne.size();i++){
            System.out.println(i);
            driver.get("http://ru.ambsc.com.br/atendimento/Identificacao.aspx");
            listaCarne.get(i).setSituacao(lerSite(listaCarne.get(i).getCadastro(), sdataVencimento));
        } 
	}
	
	@GetMapping("/exportarexcel")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<File> UploadExportarExcel() {
		listaAmbiental = new ArrayList<BoletoAmbiental>();
		Resposta r = new Resposta();
		r.setResultado("erro");
		File fileDiretorio = new File(caminhoDir); 
		File[] files = fileDiretorio.listFiles();
		for (File file : files) {
			gerarDadosAmbiental2(file);
		}
		if (listaAmbiental != null) {
			if (listaAmbiental.size() > 0) {
				ExportarExcel ex = new ExportarExcel();
				ex.exportarPDFAmbiental(listaAmbiental, caminhoDir);
				File file = ex.getFile();
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
	
	public String lerSite(String inscricao, String dataVencimento ) {
        System.out.println(inscricao);
        WebElement comboBox = driver.findElement(By.name("ddlMunicipio"));
        comboBox.sendKeys("São José");
        WebElement campoInscricao = driver.findElement(By.name("txtCodigo"));
        campoInscricao.sendKeys(inscricao);
        WebElement botaoAcesar = driver.findElement(By.name("btnAcessar"));
        botaoAcesar.click();
        if (driver.getCurrentUrl().equalsIgnoreCase("http://ru.ambsc.com.br/atendimento/Default.aspx")) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AmbientalController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            WebElement menu2Via = driver.findElement(By.id("Li8"));
            menu2Via.click();
             WebElement dv = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_txtDataReferencia\"]"));
            dv.clear();
            dv.sendKeys(dataVencimento); 
            WebElement botaoCalcular = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_btnPesquisar\"]"));
            botaoCalcular.click();
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            WebElement checkTodos = driver.findElement(By.xpath("//*[@id=\"ctl00_form1\"]/div[2]/div/div[2]/table/tbody/tr[4]/td[1]/span[1]"));
            System.out.println(checkTodos.isSelected());
            jse.executeScript("arguments[0].click();", checkTodos);
            checkTodos.click();
           
            WebElement cpf = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_txtDocumento\"]"));
             String texto = cpf.getAttribute("Value");
             if (texto ==null) {
                cpf.sendKeys("150.081.899-21");
            }else if (texto.length()<11) {
               cpf.clear();
               cpf.sendKeys("150.081.899-21"); 
            } 
            WebElement botaoImprimir = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_btnImprimir\"]"));
            try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AmbientalController.class.getName()).log(Level.SEVERE, null, ex);
                }
            botaoImprimir.click();
            if (!driver.getCurrentUrl().equalsIgnoreCase("http://ru.ambsc.com.br/atendimento/Debitos.aspx")) {
                
               
                WebElement linkImprimir = driver.findElement(By.id("idGerarBoleto"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AmbientalController.class.getName()).log(Level.SEVERE, null, ex);
                }
                linkImprimir.click();

                //menu2Via.click();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AmbientalController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                return "SEM BOLETOS";
            }
        }else {
            return "NÃO ENCONTRADO";
        }
        return "BOLETO BAIXADO";
    }
	
	public void gerarDadosAmbiental2(File file) {
		String nomeArquivo = file.getName();
        try (PDDocument document = PDDocument.load(file)) {

            if (!document.isEncrypted()) {

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);

                String lines[] = pdfFileInText.split("\\r?\\n");
                
                for (int i=0;i<lines.length;i++) {
                    System.out.println(lines[i]);
                }
                  boolean achou = false;
                  boolean novo = false;
                  BoletoAmbiental boleto = null;
                  for (int i=0;i<lines.length;i++) {
                    
                    String linha = lines[i];
                    if (linha.equalsIgnoreCase("Sacado")) {
                        if (!novo) {
                            boleto = new BoletoAmbiental();
                            boleto.setNomeArquivo(nomeArquivo);
                            novo = true;
                        }
                    }
                    if (linha.equalsIgnoreCase("Faturamento")) {
                        boleto.setMes(lines[i+1]);
                    }
                    if (linha.equalsIgnoreCase("Insc. Imob.")) {
                        boleto.setInscricao(lines[i+1]);
                    }
                    if(linha.equalsIgnoreCase("Destaque Aqui")) {
                        String ld = lines[i+1];
                        ld = ld.replace("|104-0| ", "");
                        ld = ld.replace(".", "");
                        ld = ld.replace(" ", "");                      
                        boleto.setLinhaDigitavel(ld);                       
                    }
                    if (linha.equalsIgnoreCase("Autenticação Mecânica / Ficha de Compensação")){
                        if (novo) {
                            listaAmbiental.add(boleto);
                            novo = false;
                        }
                    }
                    
                   }                  
            }
        } catch (IOException ex) {
            Logger.getLogger(AmbientalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
