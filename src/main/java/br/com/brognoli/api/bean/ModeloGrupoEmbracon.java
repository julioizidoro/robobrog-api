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

public class ModeloGrupoEmbracon {
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
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().contains(campo) && (!lendo)) {
				lendo = true;
				i = i+1;
				posicao = i;
			} else if (linhas.get(i).getLinha().contains("RECIBO DO PAGADOR") && (lendo)) {
				lendo = false;
				i = linhas.size()+100;
			}else if (!linhas.get(i).getLinha().equals("RECIBO DO PAGADOR") && (lendo)) {
				if (linhas.get(i).getLinha().charAt(0)=='T') {
					achouDescricao = true;
				}
				if (achouDescricao) {
					char letra = linhas.get(i).getLinha().charAt(0);	
					if (Character.isUpperCase(letra)) {
						inicio++;
					}
				} else {
					inicio++;
				}
			}
		}
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		for (int i=(posicao+1);i<=((posicao+(inicio/2)));i++) {
			Despesas despesa = new Despesas();
			despesa.setDescricao(linhas.get(i+(inicio/2)).getLinha());
			String valor = linhas.get(i).getLinha();
			valor = valor.replace(".", "");
			valor = valor.replace(",", ".");
			try {
				despesa.setValor(Float.parseFloat(valor));
			} catch (Exception e) {
				System.out.println(valor);
				despesa.setValor(0.0f);
			}
			listaDepesas.add(despesa);
		}
		resumo.setListaDespesas(listaDepesas);
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Uso do Banco (=) Valor do Documento")) {
				String valor = linhas.get(i+2).getLinha();
				valor = valor.replace(".", "");
				valor = valor.replace(",", ".");
				resumo.setValor(Float.parseFloat(valor));
				i = linhas.size() +100;
			} 
		}
		listaResumo.add(resumo);
	}
	
	
	
	public void lerLinhaDigitavel(List<Linhas> linhas) {
		String codigobarras = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("RECIBO DO PAGADOR")) {
				if (linhas.get(i+1).getLinha().contains("Após o vencimento")) {
					if (linhas.get(i+2).getLinha().contains("BRADESCO")) {
						codigobarras = linhas.get(i+3).getLinha();
						i = linhas.size() + 100;
					}else {
						codigobarras = linhas.get(i+2).getLinha();
						i = linhas.size() + 100;
					}
					
				} else if (linhas.get(i+1).getLinha().contains("BRADESCO")) {
					codigobarras = linhas.get(i+2).getLinha();
					i = linhas.size() + 100;
				}else if (linhas.get(i+1).getLinha().contains("Caixa")) {
					codigobarras = linhas.get(i+2).getLinha();
					i = linhas.size() + 100;
				}else if (linhas.get(i+1).getLinha().contains("NO BANCO DO BRASIL")) {
					codigobarras = linhas.get(i+2).getLinha();
					i = linhas.size() + 100;
				}else if (linhas.get(i+1).getLinha().contains("Itaú")) {
					codigobarras = linhas.get(i+3).getLinha();
					i = linhas.size() + 100;
				}else if (linhas.get(i+1).getLinha().contains("Apos o venc. ")) {
					codigobarras = linhas.get(i+2).getLinha();
					i = linhas.size() + 100;
				}else if (linhas.get(i+1).getLinha().contains("Bradesco")) {
					codigobarras = linhas.get(i+2).getLinha();
					i = linhas.size() + 100;
				}else {
					codigobarras = linhas.get(i+1).getLinha();
					i = linhas.size() + 100;
				}
				
			}else {
				if (linhas.size()>(i+1)) {
					if (linhas.get(i+1).getLinha().contains("Após vencimento")) {
					codigobarras = linhas.get(i+1).getLinha();
					i = linhas.size() + 100;
				} 
				}
			}
		}
		codigobarras = codigobarras.replace(".", "");
		codigobarras = codigobarras.replace(" ", "");
		setLinhaDigitavel(codigobarras);
	}
	
	public void lerEndereco(List<Linhas> linhas) {
		String endereco = "";
		for (int i = 0; i < linhas.size(); i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Valor Cobrado")) {
				endereco = linhas.get(i+6).getLinha();
				i = linhas.size() + 100;
			}else if (linhas.get(i).getLinha().equalsIgnoreCase("(=) Valor Cobrado")) {
				endereco = linhas.get(i+6).getLinha();
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
