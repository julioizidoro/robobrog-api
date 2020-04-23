package br.com.brognoli.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.DefaultEditorKit.BeepAction;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import br.com.brognoli.model.Despesas;
import br.com.brognoli.model.Linhas;
import br.com.brognoli.model.Resumo;

public class ModeloLideranca {

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
        lerEndereco(linhas);
        return listaResumo;
	}
	
	public void lerResumo(List<Linhas> linhas) {
		String campo = "DEMONSTRATIVO DO RATEIO";
		boolean lendo = false;
		Resumo resumo = new Resumo();
		resumo.setDescicao("Composição das Despesas");
		List<Despesas> listaDepesas = new ArrayList<Despesas>();
		boolean iniciar = false;
		int posicaoTotal = 0;
		for (int i=0;i<linhas.size();i++) {
			String valor = "";
			if (linhas.get(i).getLinha().contains("DEMONSTRATIVO DO RATEIO")) {
				iniciar = true;
			}
			if (iniciar) {
				try {
				Despesas despesa = new Despesas();
				if (linhas.get(i).getLinha().contains("Taxa de Condomínio")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,20));
					valor = linhas.get(i).getLinha().substring(34, 42);
				} else if (linhas.get(i).getLinha().contains("Taxa de Condomínio")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,20));
					valor = linhas.get(i).getLinha().substring(34, 42);
				} else if (linhas.get(i).getLinha().contains("Avanço Área Comum")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,18));
					valor = linhas.get(i).getLinha().substring(31, 45);
				} else if (linhas.get(i).getLinha().contains("Aquisição de cartão/crachá")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,28));
					valor = linhas.get(i).getLinha().substring(30, 45);
				} else if (linhas.get(i).getLinha().contains("Taxa Fundo de Reserva (")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,31));
					valor = linhas.get(i).getLinha().substring(32, 38);
				} else if (linhas.get(i).getLinha().contains("Fundo de Reserva (")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,25));
					valor = linhas.get(i).getLinha().substring(26, 32);
				} else if (linhas.get(i).getLinha().contains("Fundo de Reserva (")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,26));
					valor = linhas.get(i).getLinha().substring(26, 32);
					
				} else if (linhas.get(i).getLinha().contains("Água")) {
					if (linhas.get(i).getLinha().length()>30) {
						despesa.setDescricao(linhas.get(i).getLinha().substring(0,9));
						valor = linhas.get(i).getLinha().substring(23, 30);
					}else {
						despesa.setDescricao(linhas.get(i).getLinha().substring(0,7));
						valor = linhas.get(i).getLinha().substring(13, 27);
					}
					
				} else if (linhas.get(i).getLinha().contains("Agua")) {
						despesa.setDescricao(linhas.get(i).getLinha().substring(0,7));
						valor = linhas.get(i).getLinha().substring(20, 27);
				} else if (linhas.get(i).getLinha().contains("Despesas com Materiais")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,25));
					valor = linhas.get(i).getLinha().substring(36, 46);
				} else if (linhas.get(i).getLinha().contains("Despesas com Serviços")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,23));
					valor = linhas.get(i).getLinha().substring(36, 46);
				} else if (linhas.get(i).getLinha().contains("Despesas C/ Serviços")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,23));
					valor = linhas.get(i).getLinha().substring(36, 45);
				} else if (linhas.get(i).getLinha().contains("Despesas Administrativas e de Consumo")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,39));
					valor = linhas.get(i).getLinha().substring(52, 61);
				} else if (linhas.get(i).getLinha().contains("Despesas com Pessoal")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,23));
					valor = linhas.get(i).getLinha().substring(35, 45);
				} else if (linhas.get(i).getLinha().contains("Despesas Pessoal")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,23));
					valor = linhas.get(i).getLinha().substring(31, 40);
				} else if (linhas.get(i).getLinha().contains("Reposição Fundo de Reserva")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,32));
					valor = linhas.get(i).getLinha().substring(45, 53);
				} else if (linhas.get(i).getLinha().contains("Reserva Técnica (")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,25));
					valor = linhas.get(i).getLinha().substring(27, 34);
				} else if (linhas.get(i).getLinha().contains("Reserva Técnica (")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,25));
					valor = linhas.get(i).getLinha().substring(27, 34);
				}  else if (linhas.get(i).getLinha().contains("Diferença Consumo Gás")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,23));
					valor = linhas.get(i).getLinha().substring(35, 45);
				} else if (linhas.get(i).getLinha().contains("Leitura do Gás")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,27));
					valor = linhas.get(i+2).getLinha().substring(61, 69);
				} else if (linhas.get(i).getLinha().contains("Rateio Reforma Hall/Corredores")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,37));
					valor = linhas.get(i).getLinha().substring(49, 60);
				} else if (linhas.get(i).getLinha().contains("Celesc")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,9));
					valor = linhas.get(i).getLinha().substring(20, 30);
				} else if (linhas.get(i).getLinha().contains("Fundo de Funcionários")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,24));
					valor = linhas.get(i).getLinha().substring(30, 45);
				} else if (linhas.get(i).getLinha().contains("Fundo de Obras")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,17));
					valor = linhas.get(i).getLinha().substring(30, 39);
				} else if (linhas.get(i).getLinha().contains("Rateio Pintura/Restauração")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,34));
					valor = linhas.get(i).getLinha().substring(47, 55);
				} else if (linhas.get(i).getLinha().contains("Unidade")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,9));
					valor = linhas.get(i).getLinha().substring(23, 31);
				} else if (linhas.get(i).getLinha().contains("Fundo Subst. Portões")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,22));
					valor = linhas.get(i).getLinha().substring(35, 43);
				} else if (linhas.get(i).getLinha().contains("Fundo de Invest.")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,18));
					valor = linhas.get(i).getLinha().substring(30, 38);
				} else if (linhas.get(i).getLinha().contains("Garagem:")) {
					
					if (linhas.get(i).getLinha().length()>51) {
						despesa.setDescricao(linhas.get(i).getLinha().substring(0,34));
						valor = linhas.get(i).getLinha().substring(44, 53);
					} else if (linhas.get(i).getLinha().length()>44) {
						despesa.setDescricao(linhas.get(i).getLinha().substring(0,24));
						valor = linhas.get(i).getLinha().substring(38, 44);
					} else {
						despesa.setDescricao(linhas.get(i).getLinha().substring(0,18));
						valor = linhas.get(i).getLinha().substring(29, 36);
					}  
				} else if (linhas.get(i).getLinha().contains("Garagem")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,9));
					valor = linhas.get(i).getLinha().substring(24, 31);
				} else if (linhas.get(i).getLinha().contains("Garagens")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,13));
					valor = linhas.get(i).getLinha().substring(18, 25);
				} else if (linhas.get(i).getLinha().contains("Fundo de Manutenção e Melhorias")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,34));
					valor = linhas.get(i).getLinha().substring(47, 55);
				} else if (linhas.get(i).getLinha().contains("CASAN")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,8));
					valor = linhas.get(i).getLinha().substring(21, 29);
				} else if (linhas.get(i).getLinha().contains("Despesas Gerais")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,18));
					valor = linhas.get(i).getLinha().substring(31, 39);
				} else if (linhas.get(i).getLinha().contains("Fundo de Revitalização Predial")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,33));
					valor = linhas.get(i).getLinha().substring(31, 39);
				}else if (linhas.get(i).getLinha().contains("Rateio Lavação/Pintura Predial")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,42));
					valor = linhas.get(i).getLinha().substring(56, 64);
				}else if (linhas.get(i).getLinha().contains("Energia Elétrica")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,18));
					valor = linhas.get(i).getLinha().substring(30, 39);
				}else if (linhas.get(i).getLinha().contains("Correios")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,11));
					valor = linhas.get(i).getLinha().substring(16, 23);
				}else if (linhas.get(i).getLinha().contains("Casan")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,8));
					valor = linhas.get(i).getLinha().substring(22, 30);
				}else if (linhas.get(i).getLinha().contains("Despesa de Condomínio")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,24));
					valor = linhas.get(i).getLinha().substring(37, 46);
				}else if (linhas.get(i).getLinha().contains("Despesas Gerais")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,18));
					valor = linhas.get(i).getLinha().substring(31, 39);
				}else if (linhas.get(i).getLinha().contains("Despesas com Salários/Honorários/Encargos")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,45));
					valor = linhas.get(i).getLinha().substring(57, 66);
				}else if (linhas.get(i).getLinha().contains("Ligação Dreno Ar Condicionado")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,31));
					valor = linhas.get(i).getLinha().substring(44, 52);
				}else if (linhas.get(i).getLinha().contains("Adequação Rede Elétrica")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,30));
					valor = linhas.get(i).getLinha().substring(41, 50);
				} else if (linhas.get(i).getLinha().contains("Fundo Trabalhista")) {
					despesa.setDescricao(linhas.get(i).getLinha().substring(0,20));
					valor = linhas.get(i).getLinha().substring(32, 39);
				}
				if (valor.length()>0) {
					
					String novovalor =valor;
					valor= "";
					for(int c=0;c<novovalor.length();c++) {
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
						    || (novovalor.charAt(c)==',')	
								) {
							valor = valor + novovalor.charAt(c);
						}
					}
					try {
						valor = valor.replace(".", "");
						valor = valor.replace(",", ".");
						valor = valor.replace(" ", "");
						valor = valor.replace(" ", "");
						despesa.setValor(Float.parseFloat(valor));
					} catch (Exception e) {
						System.out.println(valor);
						System.out.println(linhas.get(i).getLinha());
					}
				
					listaDepesas.add(despesa);
				
				}
				
				if (linhas.get(i+2).getLinha().contains("DEMONSTRATIVO DE RECEITAS/DESPESAS")) {
					iniciar = false;
					posicaoTotal = i + 1;
					i = linhas.size() +100;
				}
			} catch (Exception e) {
				System.out.println(linhas.get(i).getLinha());
				System.out.println(valor);
			}
				
			}
			
		}
		resumo.setListaDespesas(listaDepesas);
		String valor ="0";
		if (linhas.get(posicaoTotal).getLinha().length()>6) {
			valor = linhas.get(posicaoTotal).getLinha().substring(6, linhas.get(posicaoTotal).getLinha().length());
		} else {
			valor = "0";
		}
		
		valor = valor.replace(".", "");
		valor = valor.replace(",", ".");
		valor = valor.replace(" ", "");
		String novovalor =valor;
		valor= "";
		for(int c=0;c<novovalor.length();c++) {
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
				valor = valor + novovalor.charAt(c);
			}
		}
		resumo.setValor(Float.parseFloat(valor));
		listaResumo.add(resumo);
	}
	
	
	
	public void lerLinhaDigitavel(List<Linhas> linhas) {
		
		String codigobarras = "";
		for (int i=0;i<linhas.size();i++) {
			if (linhas.get(i).getLinha().equalsIgnoreCase("Autenticação Mecânica")) {
				codigobarras = linhas.get(i-1).getLinha();
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
			if (linhas.get(i).getLinha().equalsIgnoreCase("Sacador/Avalista")) {
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
