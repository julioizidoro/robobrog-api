package br.com.brognoli.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.model.Despesas;
import br.com.brognoli.model.Linhas;
import br.com.brognoli.model.Resumo;

public class ModeloDuo {
	
	private List<Resumo> listaResumo;
	private int linhaResumo;
	private String Endereco;
	private String numero;
	private String linhaDigitavel;

	public List<Resumo> gerarTXT(String fileName) throws IOException {
		PdfReader reader = new PdfReader(fileName);
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
        if (linhas.get(0).getLinha().equalsIgnoreCase("Recibo do Sacado")) {
        	lerResumoRecibo(linhas);
        	lerLinhaDigitavelRecibo(linhas);
        	lerEnderecoRecibo(linhas);
        } else if (linhas.get(0).getLinha().contains("R$")) {
        	lerResumoR$(linhas);
            lerLinhaDigitavelR$(linhas);
            lerEnderecoR$(linhas);
        } else {
        	lerResumo(linhas);
            lerLinhaDigitavel(linhas);
            lerEndereco(linhas);
        }
        return listaResumo;
	}
	
	public void lerResumo(List<Linhas> linhas) {
		String campo = "Correio:";
		boolean lendo = false;
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		int inicio=0;
		int posicao =0;
		boolean achouDescricao = false;
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().contains("Rateio das despesas do mês") && (!lendo)) {
				lendo = true;
			} else if (lendo) {
				Despesas despesa = new Despesas();
				String desc = getDescricao(linhas.get(i).getLinha());
				desc = desc.replace("-", "");
				desc = desc.replace(".", "");
				desc = desc.replace("R$", "");
				despesa.setDescricao(desc);
				String valor = getValor(linhas.get(i).getLinha());
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				valor = valor.replace(" ", "");
				if (!valor.isEmpty()) {
					despesa.setValor(Float.parseFloat(valor));
					listaDepesas.add(despesa);
				}
				
			}
			if (linhas.get(i+1).getLinha().equalsIgnoreCase("Recibo do Pagador")) {
				i = linhas.size() + 100;
			} else if (linhas.get(i+1).getLinha().equalsIgnoreCase("Instruções: (Texto de Responsabilidade do Cedente)")) {
				i = linhas.size() + 100;
			}
		}
		
		resumo.setListaDespesas(listaDepesas);
		listaResumo.add(resumo);
	}
	
	public String getDescricao(String linha) {
		String novo = "";
		for (int i=0;i<linha.length();i++) {
			if (linha.charAt(i)!='$') {
				if (linha.charAt(i)!='.') {
					novo = novo + linha.charAt(i);
				}
			} else {
				i = linha.length() + 100;
			}
		}
		return novo;
	}
	
	public String getValor(String linha) {
		String novo = "";
		boolean achou = false;
		boolean numero = false;
		for (int i=0;i<linha.length();i++) {
			if (linha.charAt(i)=='$') {
				achou = true;

			} else if (achou) {
				novo = novo + linha.charAt(i);
				if (linha.charAt(i) != ' ') {
					numero = true;
				}
			}
			if ((numero) && (linha.charAt(i) == ' ')) {
				i = linha.length() + 100;
			}
		}
		return novo;
	}
	
	
	
	public void lerLinhaDigitavel(List<Linhas> linhas) {
		String codigobarras = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Carteira")) {
				codigobarras = linhas.get(i+1).getLinha();
				i = linhas.size() + 100;
			}
		}
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
	
	public void lerResumoRecibo(List<Linhas> linhas) {
		String campo = "Correio:";
		boolean lendo = false;
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		int inicio=0;
		int posicao =0;
		boolean achouDescricao = false;
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().contains("COMPOSIÇÃO DAS DESPESAS:") && (!lendo)) {
				lendo = true;
			} else if (lendo) {
				Despesas despesa = new Despesas();
				String desc = getDescricao(linhas.get(i).getLinha());
				desc = desc.replace("-", "");
				desc = desc.replace(".", "");
				desc = desc.replace("R$", "");
				despesa.setDescricao(desc);
				String valor = getValor(linhas.get(i).getLinha());
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				valor = valor.replace(" ", "");
				if (!valor.isEmpty()) {
					despesa.setValor(Float.parseFloat(valor));
					listaDepesas.add(despesa);
				}
				
			}
			if (linhas.get(i+1).getLinha().equalsIgnoreCase("Recibo do Pagador")) {
				i = linhas.size() + 100;
			} else if (linhas.get(i+1).getLinha().equalsIgnoreCase("Instruções: (Texto de Responsabilidade do Cedente)")) {
				i = linhas.size() + 100;
			}
		}
		
		resumo.setListaDespesas(listaDepesas);
		listaResumo.add(resumo);
	}
	
		
	
	
	public void lerLinhaDigitavelRecibo(List<Linhas> linhas) {
		String codigobarras = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Recortar Aqui")) {
				codigobarras = linhas.get(i+1).getLinha().substring(0,55);
				i = linhas.size() + 100;
			}
		}
		codigobarras = codigobarras.replace(".", "");
		codigobarras = codigobarras.replace(" ", "");
		setLinhaDigitavel(codigobarras);
	}
	
	public void lerEnderecoRecibo(List<Linhas> linhas) {
		String endereco = linhas.get(linhas.size()-4).getLinha();
		
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
	
	public void lerResumoR$(List<Linhas> linhas) {
		String campo = "Correio:";
		boolean lendo = false;
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		int inicio=0;
		int posicao =0;
		boolean achouDescricao = false;
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().contains("R$ ")) {
				Despesas despesa = new Despesas();
				String valor = getValor(linhas.get(i).getLinha());
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				valor = valor.replace(" ", "");
				if (!valor.isEmpty()) {
					despesa.setValor(Float.parseFloat(valor));
					listaDepesas.add(despesa);
				}
			} else {
				i = linhas.size() + 100;
			}
		}
		int contador=0;
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().contains("Composição da Cobrança") && (!lendo)) {
				lendo = true;
			} else if (lendo) {
				String desc = linhas.get(i).getLinha().substring(7, (linhas.get(i).getLinha().length()-1));
				listaDepesas.get(contador).setDescricao(desc);
				contador++;
			}
			if (linhas.get(i+1).getLinha().equalsIgnoreCase("CPF/CNPJ do Pagador")) {
				i = linhas.size() + 100;
			} 
		}
		resumo.setListaDespesas(listaDepesas);
		listaResumo.add(resumo);
	}
	
		
	
	public void lerLinhaDigitavelR$(List<Linhas> linhas) {
		String codigobarras = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Recibo do pagador")) {
				codigobarras = linhas.get(i+2).getLinha();
				i = linhas.size() + 100;
			}
		}
		codigobarras = codigobarras.replace(".", "");
		codigobarras = codigobarras.replace(" ", "");
		setLinhaDigitavel(codigobarras);
	}
	
	public void lerEnderecoR$(List<Linhas> linhas) {
		String endereco = linhas.get(linhas.size()-3).getLinha();
		
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