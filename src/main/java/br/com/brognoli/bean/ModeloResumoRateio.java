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

public class ModeloResumoRateio {
	
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
        lerResumo(linhas);
        lerLinhaDigitavel(linhas);
        return listaResumo;
	}
	
	public void lerResumo(List<Linhas> linhas) {
		
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().length()>15) {
				if (linhas.get(i).getLinha().substring(0, 16).equalsIgnoreCase("Resumo de rateio")) {
					linhaResumo = i;
					lerDespesas(linhas, i);
					i = linhas.size()+1000;
				}
			}
		}
			
	}
	
	public void lerDespesas(List<Linhas> linhas, int i) {
		String despesas = " ";
		String valor = "";
		i++;
		despesas = retornaDescricaoResumo(linhas.get(i));
		while (!despesas.equalsIgnoreCase("Total:")) {
			Resumo resumo = new Resumo();
			
			valor = retornaValorResumo(linhas.get(i));
			resumo.setDescicao(despesas);
			resumo.setValor(Float.parseFloat(valor));
			List<Despesas> listaDepesas = new ArrayList<Despesas>();
			if ((!resumo.getDescicao().contains("Gás")) && (!resumo.getDescicao().contains("gás"))
					&& (!resumo.getDescicao().contains("Água")) && (!resumo.getDescicao().contains("água")) && (!resumo.getDescicao().contains("ÁGUA"))
					&&  (!resumo.getDescicao().contains("Chiller")) && (!resumo.getDescicao().contains("Fundo de")) && (!resumo.getDescicao().contains("Óleo D")) 
					&& (!resumo.getDescicao().contains("Consumo de GLP"))
					&& (!resumo.getDescicao().contains("Diesel")) && (resumo.getDescicao().length() > 0)) {
				listaDepesas =listarDespesas(linhas, despesas);
			}
			if (listaDepesas.size()==0) {
				Despesas desp = new Despesas();
				desp.setDescricao(resumo.getDescicao());
				desp.setValor(resumo.getValor());
				listaDepesas.add(desp);
			}
			resumo.setListaDespesas(listaDepesas);
			listaResumo.add(resumo);
			i++;
			despesas = retornaDescricaoResumo(linhas.get(i));
			if (despesas.equalsIgnoreCase("Total geral:")) {
				despesas = "Total:";
			}
			
		}
	}
	
	public String retornaDescricaoResumo(Linhas linha) {
		String retorno = "";
		for (int i=linha.getLinha().length()-1;i>0;i--) {
			if (linha.getLinha().charAt(i) == ' ') {
				retorno = linha.getLinha().substring(0, i);
					return retorno;
			}
		}
		return retorno;
	}
	
	public String retornaDescricaoDespsas(Linhas linha) {
		String retorno = "";
		boolean ler = false;
		for (int i=linha.getLinha().length()-1;i>0;i--) {
			if (linha.getLinha().charAt(i) == ' ') {
				if (ler) {
					retorno = linha.getLinha().substring(0, i);
					return retorno;
				}else {
					ler = true;
				}
			}
		}
		return retorno;
	}
	
	public String retornaValorDespesas(Linhas linha) {
		if (linha.getLinha().equalsIgnoreCase("Taxa de Condomínio  254,05")) {
			System.out.print("parou");
		}
		String retorno = "";
		boolean ler = false;
		for (int i=linha.getLinha().length()-1;i>0;i--) {
			if (linha.getLinha().charAt(i) ==' ') {
				if (ler) {
					i = -1;
				} else ler = true;
			}else {
				if (ler) {
					retorno = linha.getLinha().charAt(i) + retorno;
				}
			}
		}
		
		retorno = retorno.replace(".", "");
		retorno = retorno.replace(",", ".");
		if (retorno.equalsIgnoreCase("Pagamento")) {
			System.out.println(retorno);
		}
		
		return retorno;
	}
	
	public String retornaValorResumo(Linhas linha) {
		String retorno = "";
		for (int i=linha.getLinha().length()-1;i>0;i--) {
			if (linha.getLinha().charAt(i) ==' ') {
				i = -1;
			}else {
				retorno = linha.getLinha().charAt(i) + retorno;
			}
		}
		retorno = retorno.replace(".", "");
		retorno = retorno.replace(",", ".");
		return retorno;
	}
	
	
	public List<Despesas> listarDespesas(List<Linhas> linhas, String despesa){
		System.out.println(despesa);
		List<Despesas> listaDespesas = new ArrayList<Despesas>();
		
		int d=0;
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().length()>= despesa.length()) {
				if (linhas.get(i).getLinha().substring(0, despesa.length()).equalsIgnoreCase(despesa)) {
					d=i+1;
					i=linhas.size()+100;
				}else if (despesa.equalsIgnoreCase("Garagens")) {
					if (linhas.get(i).getLinha().substring(0, despesa.length()).equalsIgnoreCase("Demonstrativo de garagens")) {
						d=i+1;
						i=linhas.size()+100;
					}
				}
			}
		}
		if (d>0) {
		for (int i =d;i<linhas.size();i++) {
			String descricao = "";
			if(i>linhaResumo) {
				descricao = retornaDescricaoResumo(linhas.get(i));
			} else {
				descricao = retornaDescricaoDespsas(linhas.get(i));
				if (descricao.equalsIgnoreCase("Descrição")) {
					descricao ="Total:";
				}
				if (descricao.equalsIgnoreCase("Total")) {
					descricao ="Total:";
				}
				if (descricao.equalsIgnoreCase("Total geral:")) {
					descricao ="Total:";
				}
			}
			if (descricao.equalsIgnoreCase("Lavação, pint int e ext, rest blocos e")) {
				System.out.println(descricao);
			}
			 
			if ((descricao.equalsIgnoreCase("Total:")) || (descricao.equalsIgnoreCase("Total geral:"))) {
				i = linhas.size()+100;
			} else if (descricao.length()>0) {
				Despesas desp = new Despesas();
				desp.setDescricao(descricao);
				
				if (i>linhaResumo) {
					desp.setValor(Float.parseFloat(retornaValorResumo(linhas.get(i))));
				}else  {
					
					desp.setValor(Float.parseFloat(retornaValorDespesas(linhas.get(i))));
				}
				
				listaDespesas.add(desp);
			}
			
		}
		}
		return listaDespesas;
	}
	
	
	public void lerLinhaDigitavel(List<Linhas> linhas) {
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().length() == 62) {
				if (linhas.get(i).getLinha().charAt(5) == '.') {
					String ld = linhas.get(i).getLinha();
					ld = ld.replace(".", "");
					ld = ld.replace(" ", "");
					setLinhaDigitavel(ld);
					System.out.println(ld);
					i = linhas.size() + 100;
					lerEndereco(linhas);
				}
			}
		}
	}
	
	public void lerEndereco(List<Linhas> linhas) {
		boolean achouEndereco = false;
		for (int i = 0; i < linhas.size(); i++) {
			if (!achouEndereco) {
			boolean achouTipo = false;
			if (achouTipo) {
				i = linhas.size() + 10;
			}
			if (linhas.get(i).getLinha().length() > 4) {
				String tipoLog = "";
				boolean num = false;
				String endereco = "";
				String numero = "";
				for (int n = 0; n < linhas.get(i).getLinha().length() - 1; n++) {
					if (!achouTipo) {
						if (linhas.get(i).getLinha().charAt(n) != ' ') {
							tipoLog = tipoLog + linhas.get(i).getLinha().charAt(n);
						} else {
							if ((tipoLog.equalsIgnoreCase("Rua")) || (tipoLog.contentEquals("Av"))
									|| (tipoLog.equalsIgnoreCase("Rod"))) {
								achouTipo = true;
							}	
						} 
					} else {
						if (!num) {
							if (linhas.get(i).getLinha().charAt(n) != '-') {
								endereco = endereco + linhas.get(i).getLinha().charAt(n);
								
							} else {
								endereco = tipoLog + " " + endereco;
								setEndereco(endereco);
								num = true;
								lerNumeroEndereco();
								achouEndereco = true;
							}

						} /*else {
							if ((linhas.get(i).getLinha().charAt(n) != '-') && (linhas.get(i).getLinha().charAt(n) != ' ')) {
								numero = numero + linhas.get(i).getLinha().charAt(n);
							} else {
								setNumero(numero);
								n = linhas.get(i).getLinha().length() + 100000;
							}
						}*/

					}
				}
				
			}
			}

		}

	}
	
	public void lerNumeroEndereco() {
		String num = "";
		int numEnd = 0;
		for (int i=getEndereco().length()-1;i>0;i--) {
			if (validarNumero(getEndereco().charAt(i))) {
				num = getEndereco().charAt(i) + num;
				if (getEndereco().charAt(i-1) == ' ') {
					numEnd = i-1;
					i = -1;
					setNumero(num);
				}
			}
		}
		setEndereco(getEndereco().substring(0,numEnd));
		setEndereco(getEndereco().replace(",", ""));
	}
	
	public boolean validarNumero(char num) {
		if ((num =='1') || (num =='2') || (num =='3') || (num =='4') || (num =='5') || (num =='6') || 
				(num =='7') || (num =='8') || (num =='9') || (num =='0')) {
			return true;
		}else return false;
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
