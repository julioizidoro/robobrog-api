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

public class ModeloPontual {
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
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=0;i<linhas.size();i++) {
			if ((linhas.get(i).getLinha().contains(" Lançado ")) && (!lendo)){
				lendo = true;
			} else if (lendo)  {
				if ((!linhas.get(i).getLinha().contains("Anterior")) && (!linhas.get(i).getLinha().contains("Leitura do Gás (")) && 
						(!linhas.get(i).getLinha().contains("Leitura da Água ("))) {
				Despesas despesa = new Despesas();
				String valor = linhas.get(i).getLinha().substring((linhas.get(i).getLinha().length()-6), (linhas.get(i).getLinha().length()));
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				valor = valor.replace("", "");
				String desc = getDescricao(linhas.get(i).getLinha().substring(0, linhas.get(i).getLinha().length()-valor.length()));
				despesa.setDescricao(desc);
				if (!valor.isEmpty()) {
					String novovalor =valor;
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
						} else {
							c = -1;
						}
					}
					if (!valor.isEmpty()) {
						despesa.setValor(Float.parseFloat(valor));
						listaDepesas.add(despesa);
					}
				}
				
			}else {
				int p =0;
			}
			if (linhas.size()>(i+1)) {
				if (!linhas.get(i+1).getLinha().contains("Despesa Total Lançado")) {
					if (linhas.get(i+1).getLinha().contains("Total")) {
						i = linhas.size() + 100;
					}
				}
			}
			}
		}
		
		resumo.setListaDespesas(listaDepesas);
		listaResumo.add(resumo);
	}
	
	public String getDescricao(String linha) {
		String novo = "";
		int contador=0;
		boolean branco=true;
		for (int i=linha.length();i>0;i--) {
			if (linha.charAt(i-1)==' ') {
				contador++;
				if (!branco) {
					return linha.substring(0, linha.length()-contador);
				}
			} else {
				contador++;
				branco=false;
			}
		}
		return " ";
	}
	
	public String getValor(String linha) {
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
			if (linhas.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica")) {
				codigobarras = linhas.get(i-1).getLinha();
				i = linhas.size() + 100;
			}
		}
		codigobarras = codigobarras.replace("237-2", "");
		codigobarras = codigobarras.replace(".", "");
		codigobarras = codigobarras.replace(" ", "");
		setLinhaDigitavel(codigobarras);
	}
	
	public void lerEndereco(List<Linhas> linhas) {
		String endereco = "";
		for (int i = 0; i < linhas.size(); i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Recibo do Pagador")) {
				endereco = linhas.get(i+2).getLinha();
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
					i = linhas.size() + 100;
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
