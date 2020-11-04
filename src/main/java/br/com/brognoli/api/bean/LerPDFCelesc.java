package br.com.brognoli.api.bean;

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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.api.casan.model.Fatura;
import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.CelescFatura;
import br.com.brognoli.api.model.Imoveladm;
import br.com.brognoli.api.model.Linhas;
import br.com.brognoli.api.model.Resposta;

public class LerPDFCelesc {
	
	private String caminhoDir= "";
	
	public Boletos carregarPDF(CelescFatura fatura, String diretorio) {
		caminhoDir = diretorio;
		Boletos boletos = new Boletos();
		String tipo ="";
			String fileName = caminhoDir + fatura.getNumero() + ".pdf"; 
			PdfReader reader;
			List<Linhas> lines = new ArrayList<Linhas>();
			try {
				reader = new PdfReader(fileName);
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
				 if (tipo.equalsIgnoreCase("CelescSegundaVia")) {
					Boletos b = lerPdfCelesc(lines, tipo);
					b.setCodigoImovel("0");
					b.setNomearquivo(fatura.getNumero() + ".pdf");
					b.setTipo(tipo);
					return b;
				}else if (tipo.equalsIgnoreCase("CelescAgrupada")) {
					Boletos b = lerPdfCelesc(lines, tipo);
					b.setCodigoImovel("0");
					b.setNomearquivo(fatura.getNumero() + ".pdf");
					b.setNomearquivo(b.getNomearquivo().replace(".pdf", ""));
					b.setTipo(tipo);
					return b;
				}else if (tipo.equalsIgnoreCase("CelescNaoVencida")) {
					Boletos b = lerPdfCelesc(lines, tipo);
					b.setCodigoImovel("0");
					b.setNomearquivo(fatura.getNumero() + ".pdf");
					b.setNomearquivo(b.getNomearquivo().replace(".pdf", ""));
					b.setTipo(tipo);
					return b;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}



	public String retornoTipoBoleto(List<Linhas> lines) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("Celesc Distribuicao S.A")) {
				return "CelescSegundaVia";
			}
		}
		if (lines.size() >= 2) {
			if (lines.get(1).getLinha().equalsIgnoreCase("-     -")) {
				return "CelescAgrupada";
			}
		}
		if (lines.size() >= 2) {
			if (lines.get(0).getLinha().contains("Nota Fiscal/Conta de Energia Eletrica")) {
				return "CelescNaoVencida";
			}
		}
		return " ";
	}
	
	public Boletos lerPdfCelesc(List<Linhas> lines, String tipo) {
		Boletos boleto = new Boletos();
   	 	boleto.setValor(getValorCelesc(lines, tipo));
		boleto.setLinhaDigitavel(getLinhaDigitavelCelesc(lines, tipo));
		boleto.setDatavencimento(getDataVencimentoCelesc(lines, tipo));
		boleto.setCnpj("08.336.783/0001-90");
   	 	//boleto.setReferencia(lines.get(8).getLinha().substring(lines.get(8).getLinha().length()-7, lines.get(8).getLinha().length()));
		boleto.setReferencia(getReferenciaCelesc(lines, tipo));
		return boleto;
	}
	
	public String getReferenciaCelesc(List<Linhas> lines, String tipo) {
		String referencia = "";
		if (tipo.equalsIgnoreCase("CelescNaoVencida")) {
			if (lines.get(2).getLinha().length()>15) {
					referencia = lines.get(2).getLinha().substring(0,7);
					return referencia;
			}
		} else if (tipo.equalsIgnoreCase("CelescSegundaVia")) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().equals("Energia Elétrica")) {
					referencia = lines.get(i+1).getLinha().substring(lines.get(i+1).getLinha().length()-7, lines.get(i+1).getLinha().length());
				}
				
			}
		}
		
		return referencia;
	}
	
	public String getValorCelesc(List<Linhas> lines, String tipo) {
		if (tipo.equalsIgnoreCase("CelescNaoVencida")) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Reservado ao Fisco Periodo Fiscal:")) {
					String linha = lines.get(i-1).getLinha();
					linha = linha.substring(13, linha.length());
					return linha;
				}
			}
		}
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("VALOR ATÉ O VENCIMENTO")) {
				String valor = lines.get(i+1).getLinha();
				valor = valor.replace("R$ ", "");
				return valor;
			} else if (lines.get(i).getLinha().equalsIgnoreCase("Valor a Pagar:")) {
				String valor = lines.get(i+1).getLinha();
				valor = valor.replace("R$ ", "");
				return valor;
			}
		}
		return "";
	}
	
	public String getDataVencimentoCelesc(List<Linhas> lines, String tipo) {
		if (tipo.equalsIgnoreCase("CelescNaoVencida")) {
			for(int i=0;i<lines.size();i++) {
				if (lines.get(i).getLinha().contains("Reservado ao Fisco Periodo Fiscal:")) {
					String linha = lines.get(i-1).getLinha();
					linha = linha.substring(0, 13);
					return linha;
				}
			}
		}
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("VENCIMENTO")) {
				return lines.get(i+1).getLinha();
			}else if (lines.get(i).getLinha().equalsIgnoreCase("Vencimento:")) {
				return lines.get(i+1).getLinha();
			}
		}
		return "";
	}
	
	public String getLinhaDigitavelCelesc(List<Linhas> lines, String tipo) {
		String codigo = "";
		boolean primeira   = false;
		for(int i=0;i<lines.size();i++) {
			if (lines.get(i).getLinha().equalsIgnoreCase("DOCUMENTO DE COBRANÇA")) {
				if (primeira) {
					String linha = lines.get(i+3).getLinha();
					linha = linha.replace(" ", "");
					return linha;
				} else {
					primeira = true;
				}
				
			}
		}
		if (lines.size()>45) {
			if (lines.get(45).getLinha().length()==57) {
				codigo = lines.get(45).getLinha();
				codigo = codigo.replace(" ", "");
			}else if (lines.get(lines.size()-2).getLinha().length()==57) {
				codigo = lines.get(lines.size()-2).getLinha();
				codigo = codigo.replace(" ", "");
			}
		} else if (lines.get(lines.size()-2).getLinha().length()==57) {
			codigo = lines.get(lines.size()-2).getLinha();
			codigo = codigo.replace(" ", "");
		} else {
			
		}
		return codigo;
	}
	

}
