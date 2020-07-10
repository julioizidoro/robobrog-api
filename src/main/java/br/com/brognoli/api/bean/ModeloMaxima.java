package br.com.brognoli.api.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.api.model.Despesas;
import br.com.brognoli.api.model.Linhas;
import br.com.brognoli.api.model.Resumo;

public class ModeloMaxima {
	
	private List<Resumo> listaResumo;
	private int linhaResumo;
	private String Endereco;
	private String numero;
	private String linhaDigitavel;

	public List<Resumo> gerarTXT(String fileName, InputStream is) throws IOException {
		PdfReader reader;
		if (is != null) {
			reader = new PdfReader(is);
		}else reader = new PdfReader(fileName);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        List<Linhas> linhas = new ArrayList<Linhas>();
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
        while(br.ready()){
        	String linha = br.readLine();
        	Linhas l = new Linhas();
        	l.setLinha(linha);
        	linhas.add(l);
        }
        br.close();
        listaResumo = new ArrayList<Resumo>();
        linhaResumo =0;
        lerResumo(linhas);
        lerLinhaDigitavel(linhas);
        lerEndereco(linhas);
       return listaResumo;
	}
	
	public void lerResumo(List<Linhas> linhas) {
		boolean lendo = false;
		int contTotal=0;
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=0;i<linhas.size();i++) {
			if ((linhas.get(i).getLinha().contains("DEMONSTRATIVO DO RATEIO - ")) && (!lendo)){
				lendo = true;
				i++;
			} else if (lendo)  {
				Despesas despesa = new Despesas();
				String desc = linhas.get(i).getLinha().substring(0, 19);
				if (!desc.isEmpty()) {
				despesa.setDescricao(desc);
				String valor="";
				if (desc.contains("Leitura do Gás")) {
					i=i+2;
					contTotal++;
					valor = linhas.get(i).getLinha().substring(60, linhas.get(i).getLinha().length());
				} else valor = linhas.get(i).getLinha().substring((linhas.get(i).getLinha().length()-7), (linhas.get(i).getLinha().length()));
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				valor = valor.replace("", "");
				if (!valor.isEmpty()) {
					String novovalor="";
					if (valor.length()>10) {
						novovalor = valor.substring(0,10);
					} else novovalor =valor;
					valor= "";
					for(int c=novovalor.length()-1;c>=0;c--) {
					
						if ((novovalor.charAt(c)=='0') 
						    || (novovalor.charAt(c)=='1')	
						    || (novovalor.charAt(c)=='2')
						    || (novovalor.charAt(c)=='3')
						    || (novovalor.charAt(c)=='4')
						    || (novovalor.charAt(c)=='5')
						    || (novovalor.charAt(c)=='6')
						    || (novovalor.charAt(c)=='7')
						    || (novovalor.charAt(c)=='8')
						    || (novovalor.charAt(c)=='9')
						    || (novovalor.charAt(c)=='.')	
								) {
							valor = novovalor.charAt(c) + valor;
						} 
					}
					if (!valor.isEmpty()) {
						despesa.setValor(Float.parseFloat(valor));
						listaDepesas.add(despesa);
					}
				}
			}
				if (linhas.size()>(i+1)) {
					if ((linhas.get(i+1).getLinha().contains("Total"))) {
						if (contTotal<=0) {
							contTotal++;
						} else {
							i= linhas.size() + 100;
						}
					} 
				}
				
			}
			
		}
		
		resumo.setListaDespesas(listaDepesas);
		listaResumo.add(resumo);
	}
	
	public String getDescricao(String linha) {
		String descricao="";
		for (int i=0;i<linha.length()-1;i++) {
			if ((linha.charAt(i)!=' ') || (linha.charAt(i+1)!=' ')) {
				descricao = descricao + linha.charAt(i);
			} else {
				i = linha.length() + 100;
			}
		}
		return descricao;
	}
	
	public String getValor(String linha, int ndesc) {
		String novo = "";
		for (int i=linha.length();i>0;i--) {
			if (linha.charAt(i-1)!=' ') {
				novo = linha.charAt(i-1) + novo;
			} else {
				i = -1;
			}
		}
		return novo;
	}
	
	
	
	public void lerLinhaDigitavel(List<Linhas> linhas) {
		String codigobarras = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equals("Autenticação Mecânica")) {
				codigobarras = linhas.get(i-1).getLinha();
				i = linhas.size() + 100;
			} 
		}
		codigobarras = codigobarras.replace(".", "");
		codigobarras = codigobarras.replace(" ", "");
		codigobarras = codigobarras.replace("-", "");
		setLinhaDigitavel(codigobarras);
	}
	
	public void lerEndereco(List<Linhas> linhas) {
		String endereco = "";
		for (int i = 0; i < linhas.size(); i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Pagador")) {
				endereco = linhas.get(i-2).getLinha();
				i = linhas.size() + 100;
			} 
		}
		
		String soEndereco="";
		String numero = "";
		boolean achouEndereco = false;
		for (int i = 0; i < endereco.length(); i++) {
			if (!achouEndereco) {
				if (endereco.charAt(i)!=',') {
					soEndereco = soEndereco + endereco.charAt(i);
				}else {
					achouEndereco = true;
					i = i +1;
				}
			} else {
				if (endereco.charAt(i)!=' ') {
					numero = numero + endereco.charAt(i);
				}else {
					if  (!numero.equalsIgnoreCase("Nº")){
						i = linhas.size() + 100;
					} else {
						numero = numero + endereco.charAt(i);
					}
				}
			}
		}
		setEndereco(soEndereco);
		setNumero(numero);

	}
	
	public List<Resumo> getListaResumo() {
		return listaResumo;
	}

	public void setListaResumo(List<Resumo> listaResumo) {
		this.listaResumo = listaResumo;
	}

	public int getLinhaResumo() {
		return linhaResumo;
	}

	public void setLinhaResumo(int linhaResumo) {
		this.linhaResumo = linhaResumo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getLinhaDigitavel() {
		return linhaDigitavel;
	}

	public void setLinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel;
	}
	
	public String getEndereco() {
		return Endereco;
	}

	public void setEndereco(String endereco) {
		Endereco = endereco;
	}

}

