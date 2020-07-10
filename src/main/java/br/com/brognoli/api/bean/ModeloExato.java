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

public class ModeloExato {
	
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
		String campo = "Correio:";
		boolean lendo = false;
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		int inicio=0;
		int posicao =0;
		boolean achouDescricao = false;
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=0;i<linhas.size();i++) {
			try {
			if (linhas.get(i).getLinha().contains("Taxa de Condomínio") && (!lendo)) {
				lendo = true;
				i--;
			} else if (linhas.get(i).getLinha().contains("Login:") && (!lendo)) {
				lendo = true;
			} else if ((lendo) && (!linhas.get(i).getLinha().contains("Detalhe:"))) {
				Despesas despesa = new Despesas();
				String valor = getValor(linhas.get(i).getLinha());
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				valor = valor.replace(" ", "");
				String desc = linhas.get(i).getLinha().substring(0, linhas.get(i).getLinha().length()-valor.length());
				desc = desc.replace("-", "");
				desc = desc.replace(".", "");
				desc = desc.replace("R$", "");
				despesa.setDescricao(desc);
				if (!valor.isEmpty()) {
					despesa.setValor(Float.parseFloat(valor));
					listaDepesas.add(despesa);
				}
				
			}
			if (linhas.size()>(i+1)) {
			if (linhas.get(i+1).getLinha().equalsIgnoreCase("Recibo do Pagador")) {
				i = linhas.size() + 100;
			} else if (linhas.get(i+1).getLinha().equalsIgnoreCase("Instruções: (Texto de Responsabilidade do Cedente)")) {
				i = linhas.size() + 100;
			}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		}
		
		resumo.setListaDespesas(listaDepesas);
		listaResumo.add(resumo);
	}
	
	public String getValor(String linha) {
		String novo = "";
		for (int i=linha.length();i>0;i--) {
			if (linha.charAt(i-1)!='.') {
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
			if (linhas.get(i).getLinha().equalsIgnoreCase("LOCAL DE PAGAMENTO")) {
				codigobarras = linhas.get(i-1).getLinha();
				i = linhas.size() + 100;
			}
		}
		if (codigobarras.length()<15) {
			for (int i=0;i<linhas.size();i++) {
				if (linhas.get(i).getLinha().equalsIgnoreCase("Vencimento")) {
					codigobarras = linhas.get(i+2).getLinha();
					i = linhas.size() + 100;
				}
			}
		}
		codigobarras = codigobarras.replace("237-2", "");
		codigobarras = codigobarras.replace("756-0", "");
		codigobarras = codigobarras.replace("104-0", "");
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
