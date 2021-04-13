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

import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.Boletoseguro;
import br.com.brognoli.api.model.CelescDados;
import br.com.brognoli.api.model.Imoveladm;
import br.com.brognoli.api.model.Linhas;
import br.com.brognoli.api.model.Resposta;
import br.com.brognoli.api.service.S3Service;

@CrossOrigin
@RestController
@RequestMapping("/seguros")
public class BoletoSeguroController {
	
	
	private String caminhoDir;
	
	@PostMapping("/upload")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Resposta> uploadPDFSimplficada(@RequestParam(name="file") MultipartFile[] files) {
		List<Boletoseguro> listaBoletos = new ArrayList<Boletoseguro>();
		Resposta r = new Resposta();
		r.setResultado("erro");
		for (MultipartFile uploadFile : files) {
			
			InputStream is = null;
			try {
				is = uploadFile.getInputStream();
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

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
					Boletoseguro b = lerPdfSeguro(lines);
					String nome = uploadFile.getOriginalFilename();
					nome = nome.replace(".pdf", "");
					b.setImovel(nome);
					listaBoletos.add(b);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (listaBoletos != null) {
			if (listaBoletos.size() > 0) {
				ExportarExcel ex = new ExportarExcel();
				ex.gerarSeguro(listaBoletos);
				File file = ex.getFile();
				r.setResultado("ok");
				return ResponseEntity.ok(r);
			}
		}
		return ResponseEntity.ok(r); 
	}
	
	public Boletoseguro lerPdfSeguro(List<Linhas> lines) {
		Boletoseguro boleto = new Boletoseguro();
		boleto.setDatavencimento(getDataVencimentoSeguro(lines));
   	 	boleto.setValor(getValorSeguro(lines));
		boleto.setLinhadigitavel(getLinhaDigitavelSeguro(lines));
		return boleto;
	}
	
	public String getDataVencimentoSeguro(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("Vencimento")) {
				String data = lines.get(i+1).getLinha();
				data = data.replace(" ", "");
				return data;
			}
		}
		return "";
	}
	
	public String getLinhaDigitavelSeguro(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica")) {
				String codigo = lines.get(i+1).getLinha();
				codigo = codigo.replace("BANCO ITAU 341-7", "");
				codigo = codigo.replace(" ", "");
				codigo = codigo.replace(".", "");
				return codigo;
			}
		}
		return "";
	}
	
	public String getValorSeguro(List<Linhas> lines) {
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("Espécie")) {
				String valor = lines.get(i+3).getLinha();
				return valor;
			}
		}
		return "";
	}
	
	@GetMapping("cb")
	@ResponseStatus(HttpStatus.CREATED)
	public void calcularCB(){
		ExportarExcel ex = new ExportarExcel();
		//ex.converterCodigoBarras("34191757289198011293181008030009983220000033150");
		
	}

}
