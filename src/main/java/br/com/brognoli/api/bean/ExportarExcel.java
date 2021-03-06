package br.com.brognoli.api.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.web.client.RestTemplate;

import br.com.brognoli.api.casan.model.Fatura;
import br.com.brognoli.api.casan.model.Imovelcasan;
import br.com.brognoli.api.model.BoletoAmbiental;
import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.Boletoseguro;
import br.com.brognoli.api.model.Carne;
import br.com.brognoli.api.model.CarneIPTU;

import br.com.brognoli.api.model.CelescDados;
import br.com.brognoli.api.model.Fornecedor;
import br.com.brognoli.api.model.Resumo;
import br.com.brognoli.api.util.Conversor;

public class ExportarExcel {

	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void gerarWinker(List<Boletos> listaBoletos, String mesano) {
		
	/*	
		List<Winkerexcel> lista = new ArrayList<Winkerexcel>();
		for (Boletos c : listaBoletos) {
			Winkerexcel we = new Winkerexcel();
			we.setEdificio(c.getCobranca().getCondominio());
			we.setEndereco(c.getEndereco());
			we.setNumero(c.getNumero());
			we.setBloco("");
			we.setUnidade(c.getCobranca().getUnidade());
			we.setCompetencia(c.getCobranca().getData_ref_rateio());
			we.setVencimento(ConvercaoData(c.getCobranca().getData_vencimento()));
			we.setLinhadigitavel(c.getLinhaDigitavel());
			we.setCodigobarras("");
			we.setTotal(calcularValorTotal(c.getListaResumo()));
			we.setDesconto("0,00");
			List<Winkerexcelvalor> listaValor = new ArrayList<Winkerexcelvalor>();
			if (c.getListaResumo() != null) {
				for (Resumo r : c.getListaResumo()) {
					if (r.getListaDespesas() != null) {
						for (int n = 0; n < r.getListaDespesas().size(); n++) {
							Winkerexcelvalor wev = new Winkerexcelvalor();
							wev.setDescricao(r.getListaDespesas().get(n).getDescricao());
							wev.setValor(r.getListaDespesas().get(n).getValor());
						}
					}
				}
			}
			we.setListawinkerexcelvalor(listaValor);
		}
		return lista
		*/

		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Eventos Valores");
		FileOutputStream fos = null;

		try {
			String caminhoExcel = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\extracao\\"; 
			file = new File(caminhoExcel + "Winker_" + mesano + ".xls");
			fos = new FileOutputStream(file);
			int i = 0;
			HSSFRow row = firstSheet.createRow(i);
			row.createCell(0).setCellValue("Edificio");
			row.createCell(1).setCellValue("Endereço");
			row.createCell(2).setCellValue("Numero");
			row.createCell(3).setCellValue("Bloco");
			row.createCell(4).setCellValue("Unidade");
			row.createCell(5).setCellValue("Competencia");
			row.createCell(6).setCellValue("Vencimento");
			row.createCell(7).setCellValue("Linha Digitavel");
			row.createCell(8).setCellValue("Codigo Barras");
			row.createCell(9).setCellValue("Total");
			row.createCell(10).setCellValue("Desconto");
			int l = 11;
			int maior = 0;
			for (Boletos c : listaBoletos) {
				int item = 0;
				if (c.getListaResumo() != null) {
					for (Resumo r : c.getListaResumo()) {
						if (r.getListaDespesas() != null) {
							if (maior < r.getListaDespesas().size()) {
								maior = r.getListaDespesas().size();
							}
						}
					}
				}

			}

			for (int n = 0; n <= maior; n++) {
				if (l <= n) {
					row.createCell(l).setCellValue("Item" + String.valueOf(n));
					l++;
					row.createCell(l).setCellValue("Valor Item" + String.valueOf(n));
					l++;
				}
			}

			i++;

			for (Boletos c : listaBoletos) {
				row = firstSheet.createRow(i);
				row.createCell(0).setCellValue(c.getCobranca().getCondominio());
				row.createCell(1).setCellValue(c.getEndereco());
				row.createCell(2).setCellValue(c.getNumero());
				row.createCell(3).setCellValue("");
				row.createCell(4).setCellValue(c.getCobranca().getUnidade());
				row.createCell(5).setCellValue(c.getCobranca().getData_ref_rateio());
				row.createCell(6).setCellValue(ConvercaoData(c.getCobranca().getData_vencimento()));
				row.createCell(7).setCellValue(c.getLinhaDigitavel());
				row.createCell(8).setCellValue("");
				row.createCell(9).setCellValue(calcularValorTotal(c.getListaResumo()));
				row.createCell(10).setCellValue("0,00");
				l = 11;
				if (c.getListaResumo() != null) {
					for (Resumo r : c.getListaResumo()) {
						if (r.getListaDespesas() != null) {
							for (int n = 0; n < r.getListaDespesas().size(); n++) {
								row.createCell(l).setCellValue(r.getListaDespesas().get(n).getDescricao());
								l++;
								row.createCell(l).setCellValue(r.getListaDespesas().get(n).getValor());
								l++;
							}
						}
					}
				}
				i++;
			}

			workbook.write(fos);

		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	
	public void gerarGT(List<Boletos> listaBoletos) {
		
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet firstSheet = workbook.createSheet("Eventos Valores");
			FileOutputStream fos = null;

			try {
				String caminhoExcel = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\extracao\\"; 
				file = new File(caminhoExcel + "GarantiaTotal.xls");
				fos = new FileOutputStream(file);
				int i = 0;
				HSSFRow row = firstSheet.createRow(i);
				row.createCell(0).setCellValue("Imovel");
				row.createCell(1).setCellValue("Administradora");
				row.createCell(2).setCellValue("Vencimento");
				row.createCell(3).setCellValue("Linha Digitavel");
				row.createCell(4).setCellValue("Codigo Barras");
				row.createCell(5).setCellValue("Total");
				row.createCell(6).setCellValue("Desconto");
				int l = 7;
				int maior = 0;
				for (Boletos c : listaBoletos) {
					int item = 0;
					if (c.getListaResumo() != null) {
						for (Resumo r : c.getListaResumo()) {
							if (r.getListaDespesas() != null) {
								if (maior < r.getListaDespesas().size()) {
									maior = r.getListaDespesas().size();
								}
							}
						}
					}

				}

				for (int n = 0; n <= maior; n++) {
						row.createCell(l).setCellValue("Item" + String.valueOf(n+1));
						l++;
						row.createCell(l).setCellValue("Valor Item" + String.valueOf(n+1));
						l++;
					
				}

				i++;

				for (Boletos c : listaBoletos) {
					if (i==260) {
						int p =0;
					}
					row = firstSheet.createRow(i);
					row.createCell(0).setCellValue(c.getImovelAdm().getImovel());
					row.createCell(1).setCellValue(c.getImovelAdm().getAdmwinker());
					row.createCell(2).setCellValue(c.getDatavencimento());
					row.createCell(3).setCellValue(c.getLinhaDigitavel());
					row.createCell(4).setCellValue("");
					row.createCell(5).setCellValue(calcularValorTotal(c.getListaResumo()));
					row.createCell(6).setCellValue("0,00");
					l = 7;
					if (c.getListaResumo() != null) {
						for (Resumo r : c.getListaResumo()) {
							if (r.getListaDespesas() != null) {
								for (int n = 0; n < r.getListaDespesas().size(); n++) {
									row.createCell(l).setCellValue(r.getListaDespesas().get(n).getDescricao());
									l++;
									row.createCell(l).setCellValue(r.getListaDespesas().get(n).getValor());
									l++;
								}
							}
						}
					}
					System.out.println("i=" + i);
					i++;
					System.out.println("i= " + i);
					System.out.println("l= " + l);
				}

				workbook.write(fos);

			} catch (Exception e) {
				
				System.out.println(e);
			}

			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	
	public void gerarGTSimpificada(List<Boletos> listaBoletos) {
		
		
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Eventos Valores");
		FileOutputStream fos = null;

		try {
			file = new File("GarantiaTotalS.xls");
			fos = new FileOutputStream(file);
			int i = 0;
			HSSFRow row = firstSheet.createRow(i);
			row.createCell(0).setCellValue("Competencia");
			row.createCell(1).setCellValue("Data vencimento");
			row.createCell(2).setCellValue("Linha digitável");
			row.createCell(3).setCellValue("CNPJ");
			row.createCell(4).setCellValue("Nome arquivo");
			row.createCell(5).setCellValue("Tipo");
			for (Boletos c : listaBoletos) {
				i++;
				row = firstSheet.createRow(i);
				row.createCell(0).setCellValue(c.getReferencia());
				row.createCell(1).setCellValue(c.getDatavencimento());
				row.createCell(2).setCellValue(c.getLinhaDigitavel());
				row.createCell(3).setCellValue(c.getCnpj());
				String nomeArquivo = c.getNomearquivo();
				nomeArquivo = nomeArquivo.replace(".pdf", "");
				row.createCell(4).setCellValue(nomeArquivo);
				row.createCell(5).setCellValue(c.getTipo());
			}

			workbook.write(fos);

		} catch (Exception e) {
			
			System.out.println(e);
		}

		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
public void gerarOp(List<Boletos> listaBoletos, String caminhoDir) {
		
		
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet gs = workbook.createSheet("Eventos Valores");
		HSSFSheet op = workbook.createSheet("OP");
		FileOutputStream fos = null;
		HSSFCellStyle style =  workbook.createCellStyle();
		style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
		style.setAlignment(HorizontalAlignment.RIGHT);
		try {
			String caminhoExcel = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\extracao\\"; 
			file = new File(caminhoExcel + "extracaoBoletos.xls");
			fos = new FileOutputStream(file);
			int i = 0;
			HSSFRow row = gs.createRow(i);

			row.createCell(0).setCellValue("Competencia");
			row.createCell(1).setCellValue("Data vencimento");
			row.createCell(2).setCellValue("Linha digitável");
			row.createCell(3).setCellValue("CNPJ");
			row.createCell(4).setCellValue("Nome arquivo");
			row.createCell(5).setCellValue("Tipo");
			for (Boletos c : listaBoletos) {
				i++;
				row = gs.createRow(i);
				row.createCell(0).setCellValue(c.getReferencia());
				row.createCell(1).setCellValue(c.getDatavencimento());
				row.createCell(2).setCellValue(c.getLinhaDigitavel());
				row.createCell(3).setCellValue(c.getCnpj());
				if (c.getNomearquivo()!=null) {
					String nomeArquivo = c.getNomearquivo();
					nomeArquivo = nomeArquivo.replace(".pdf", "");
					row.createCell(4).setCellValue(nomeArquivo);
				}else {
					row.createCell(4).setCellValue("");
				}
						
				if (c.getTipo().equalsIgnoreCase("Celesc1")) {
					row.createCell(5).setCellValue("Celesc");
				} else row.createCell(5).setCellValue(c.getTipo());
			}
			
			i = 0;
			
			
			row = op.createRow(i);
			
			row.createCell(0).setCellValue("Data Vencimento");
			row.createCell(1).setCellValue("Imovel");
			row.createCell(2).setCellValue("Conta");
			row.createCell(3).setCellValue("Agencia");
			row.createCell(4).setCellValue("Valor");
			row.createCell(5).setCellValue("Evento");
			row.createCell(6).setCellValue("Historico");
			row.createCell(7).setCellValue("Complemento");
			row.createCell(8).setCellValue("Cód.Barras");
			row.createCell(9).setCellValue("Fornecedor");
			row.createCell(10).setCellValue("Gerar");
			Conversor conversor = new Conversor();
			for (Boletos c : listaBoletos) {
				try {
				i++;
				String cb = converterCodigoBarras(c.getTipo(), c.getLinhaDigitavel());
				String valor ="0,00";
				String dataVencimento = "";
				
					
				DecimalFormat df = new DecimalFormat("0.00");
				df.setMaximumFractionDigits(2);
				if (c.getTipo().equalsIgnoreCase("Condominio")) {
					if (cb.length()>20) {
						float fvalor = getValor(cb);
						valor = df.format(fvalor);
						dataVencimento = getDataVencimento(cb);
					}
				}else {
					if (c.getValor()!=null) {
						Float fvalor = conversor.formatarStringfloat(c.getValor());
						valor = df.format(fvalor);
						dataVencimento = c.getDatavencimento();
					}
				}
				
				
				row = op.createRow(i);
				row.createCell(0).setCellValue(dataVencimento);
			    String codigo = c.getCodigoImovel();
				if (codigo.length()>5) {
					codigo = codigo.substring(0, 5);
				} 
				row.createCell(1).setCellValue(codigo);
				row.createCell(2).setCellValue("");
				row.createCell(3).setCellValue("");
				row.createCell(4).setCellValue(valor);
				row.getCell(4).setCellStyle(style);
				row.createCell(5).setCellValue("");
				row.createCell(6).setCellValue("");
				row.createCell(7).setCellValue("");
				row.createCell(8).setCellValue(cb);
				if (c.getCnpj().length()==18) {
					row.createCell(9).setCellValue(getFornecedorCNPJ(c.getCnpj()));
				} else row.createCell(9).setCellValue(0);
				row.createCell(10).setCellValue("S");
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			
			workbook.write(fos);

		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		}

		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

public void gerarOpCelesc(List<Boletos> listaBoletos, String caminhoDir, List<CelescDados> listaCelescDados) {
	
	
	
	HSSFWorkbook workbook = new HSSFWorkbook();
	HSSFSheet gs = workbook.createSheet("Eventos Valores");
	HSSFSheet op = workbook.createSheet("OP");
	HSSFSheet res = workbook.createSheet("Resultado");
	FileOutputStream fos = null;
	HSSFCellStyle style =  workbook.createCellStyle();
	style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
	style.setAlignment(HorizontalAlignment.RIGHT);

	try {
		if (caminhoDir.length()==0) {
			file = new File("CelescOP.xls");
		} else file = new File(caminhoDir + "celesc.xls");
		fos = new FileOutputStream(file);
		int i = 0;
		HSSFRow row = gs.createRow(i);

		row.createCell(0).setCellValue("Imóvel");
		row.createCell(1).setCellValue("UC");
		row.createCell(2).setCellValue("Competencia");
		row.createCell(3).setCellValue("Data vencimento");
		row.createCell(4).setCellValue("Linha digitável");
		row.createCell(5).setCellValue("CNPJ");
		row.createCell(6).setCellValue("Nome arquivo");
		row.createCell(7).setCellValue("Tipo");
		for (Boletos c : listaBoletos) {
			i++;
			row = gs.createRow(i);
			row.createCell(0).setCellValue(c.getCodigoImovel());
			row.createCell(1).setCellValue(c.getUC());
			row.createCell(3).setCellValue(c.getReferencia());
			row.createCell(3).setCellValue(c.getDatavencimento());
			row.createCell(4).setCellValue(c.getLinhaDigitavel());
			row.createCell(5).setCellValue(c.getCnpj());
			if (c.getNomearquivo()!=null) {
				String nomeArquivo = c.getNomearquivo();
				nomeArquivo = nomeArquivo.replace(".pdf", "");
				row.createCell(6).setCellValue(nomeArquivo);
			}else {
				row.createCell(6).setCellValue("");
			}
					
			if (c.getTipo().equalsIgnoreCase("Celesc1")) {
				row.createCell(7).setCellValue("Celesc");
			} else row.createCell(7).setCellValue(c.getTipo());
		}
		
		i = 0;
		
		
		row = op.createRow(i);
		
		row.createCell(0).setCellValue("Data Vencimento");
		row.createCell(1).setCellValue("Imovel");
		row.createCell(2).setCellValue("Conta");
		row.createCell(3).setCellValue("Agencia");
		row.createCell(4).setCellValue("Valor");
		row.createCell(5).setCellValue("Evento");
		row.createCell(6).setCellValue("Historico");
		row.createCell(7).setCellValue("Complemento");
		row.createCell(8).setCellValue("Cód.Barras");
		row.createCell(9).setCellValue("Fornecedor");
		row.createCell(10).setCellValue("Gerar");
		Conversor conversor = new Conversor();
		for (Boletos c : listaBoletos) {
			try {
			i++;
			String cb = converterCodigoBarras(c.getTipo(), c.getLinhaDigitavel());
			String valor ="0,00";
			String dataVencimento = "";
			
				
			DecimalFormat df = new DecimalFormat("0.00");
			df.setMaximumFractionDigits(2);
			if (c.getTipo().equalsIgnoreCase("Condominio")) {
				if (cb.length()>20) {
					float fvalor = getValor(cb);
					valor = df.format(fvalor);
					dataVencimento = getDataVencimento(cb);
				}
			}else {
				if (c.getValor()!=null) {
					Float fvalor = conversor.formatarStringfloat(c.getValor());
					valor = df.format(fvalor);
					dataVencimento = c.getDatavencimento();
				}
			}
			
			
			row = op.createRow(i);
			row.createCell(0).setCellValue(dataVencimento);
			row.createCell(1).setCellValue(c.getCodigoImovel());
			row.createCell(2).setCellValue("");
			row.createCell(3).setCellValue("");
			row.createCell(4).setCellValue(valor);
			row.getCell(4).setCellStyle(style);
			row.createCell(5).setCellValue("");
			row.createCell(6).setCellValue("");
			row.createCell(7).setCellValue("");
			row.createCell(8).setCellValue(cb);
			if (c.getCnpj().length()==18) {
				row.createCell(9).setCellValue(getFornecedorCNPJ(c.getCnpj()));
			} else row.createCell(9).setCellValue(0);
			row.createCell(10).setCellValue("S");
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		i = 0;
		
		
		row = res.createRow(i);
		
		row.createCell(0).setCellValue("Imovel");
		row.createCell(1).setCellValue("Nome");
		row.createCell(2).setCellValue("CPF");
		row.createCell(3).setCellValue("UC");
		row.createCell(4).setCellValue("Data Corte");
		row.createCell(5).setCellValue("Pedido desligamento");
		row.createCell(6).setCellValue("Situação");
		row.createCell(7).setCellValue("Resultado");
		row.createCell(8).setCellValue("No. PDF");
		
		for (CelescDados c : listaCelescDados) {
			try {
			i++;
			row = res.createRow(i);
			row.createCell(0).setCellValue(c.getImovel());
			row.createCell(1).setCellValue(c.getNome());
			row.createCell(2).setCellValue(c.getCpf());
			row.createCell(3).setCellValue(c.getCodigo());
			row.createCell(4).setCellValue(c.getDataCorte());
			row.createCell(5).setCellValue(c.getPedidodesligamento());
			row.createCell(6).setCellValue(c.getSituacao());
			row.createCell(7).setCellValue(c.getResultado());
			if (c.getListaFatura()!=null) {
				if (c.getListaFatura().size()>0) {
					row.createCell(8).setCellValue(c.getListaFatura().size());
				}else row.createCell(8).setCellValue(0);
			}else row.createCell(8).setCellValue(0);
			
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		workbook.write(fos);

	} catch (Exception e) {
		
		System.out.println(e.getMessage());
	}

	try {
		fos.flush();
		fos.close();
	} catch (Exception e) {
		e.printStackTrace();
	}

}

		public Float calcularValorTotal(List<Resumo> listaResumo) {
			Float valor = 0.0f;
			if (listaResumo == null) {
				return valor;
			}
			for (int i = 0; i < listaResumo.size(); i++) {
				if (listaResumo.get(i).getListaDespesas() == null) {
					valor = valor + 0;
				} else
					for (int d = 0; d < listaResumo.get(i).getListaDespesas().size(); d++) {
						try {
							valor = valor + listaResumo.get(i).getListaDespesas().get(d).getValor();
						} catch (Exception e) {
							System.out.println(i + " - " + d);
						}
					}
			}
			return valor;

		}

	public String ConvercaoData(Date data) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormatada = df.format(data);
		return dataFormatada;
	}
	
	public void exportarResultadoExcelSJ(List<CarneIPTU> listaIPTU, String caminhoDir, List<Carne> listaCarne) {
        HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet dam = workbook.createSheet("DAM-PM");
		HSSFSheet res = workbook.createSheet("Resultado");
		int contador=0;
		FileOutputStream fos = null;
		try {
			file = new File(caminhoDir + "IPTU_SJ.xls");
			fos = new FileOutputStream(file);
			int i = 0;
			HSSFRow row = dam.createRow(i);
			row.createCell(0).setCellValue("Inscrição");
			row.createCell(1).setCellValue("InscriçãoMascara");
			row.createCell(2).setCellValue("Tipo");
            row.createCell(3).setCellValue("Parcela");
            row.createCell(4).setCellValue("Data Vencimento");
            row.createCell(5).setCellValue("Juros");
            row.createCell(6).setCellValue("Valor");
            row.createCell(7).setCellValue("Linha Digitavel");
            row.createCell(8).setCellValue("Codigo Barras");
            row.createCell(9).setCellValue("Nome arquivo");
                       
			i++;
			
				for (CarneIPTU c : listaIPTU) {
					row = dam.createRow(i);
					row.createCell(0).setCellValue(c.getInscricao());
					row.createCell(1).setCellValue(c.getInscricaoMascara());
					row.createCell(2).setCellValue(c.getTipo());
					row.createCell(3).setCellValue(c.getParcela());
					row.createCell(4).setCellValue(c.getVencimento());
					row.createCell(5).setCellValue(c.getJuros());
					row.createCell(6).setCellValue(c.getValor());
					row.createCell(7).setCellValue(c.getLinhaDigitavel());
					row.createCell(8).setCellValue(c.getCodigoBarras());
					row.createCell(9).setCellValue(c.getNomearquivo());
					i++;
					contador++;
					System.out.println(contador);
				}
			
			if (listaCarne!=null) {
					
			i= 0;
			row = res.createRow(i);
			
			 row.createCell(0).setCellValue("Inscrição");
	         row.createCell(8).setCellValue("Cadastro");
	         row.createCell(9).setCellValue("Situação");
	                       
			 i++;
			 contador = 0;
			 for (Carne carne : listaCarne) {
				 contador++;
				 row = res.createRow(i);
				 row.createCell(0).setCellValue(carne.getInscricao());
				 row.createCell(1).setCellValue(carne.getCadastro());
				 row.createCell(2).setCellValue(carne.getSituacao());
				 i++;
				 System.out.println(contador);
			 }
			
			}
			
			workbook.write(fos);

		}catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
     }
	
		public void gerarSeguro(List<Boletoseguro> listaBoletos ) {
		
		
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Eventos Valores");
		FileOutputStream fos = null;
		
		try {
			String caminhoExcel = "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\extracao\\"; 
			//String caminhoExcel = "C:\\LOGS\\ExTRACAO\\";
			file = new File(caminhoExcel + "BoletoSeguros.xls");
			fos = new FileOutputStream(file);
			int i = 0;
			HSSFRow row = firstSheet.createRow(i);
			row.createCell(0).setCellValue("Imovel");
			row.createCell(1).setCellValue("Data Vencimento");
			row.createCell(2).setCellValue("Valor");
			row.createCell(3).setCellValue("Cód.Barras");
			
			for (Boletoseguro c : listaBoletos) {
				i++;
				row = firstSheet.createRow(i);
				row.createCell(0).setCellValue(c.getImovel());
				row.createCell(1).setCellValue(c.getDatavencimento());
				row.createCell(2).setCellValue(c.getValor());
				row.createCell(3).setCellValue(converterCodigoBarras("Condominio", c.getLinhadigitavel()));
			}

			workbook.write(fos);

		} catch (Exception e) {
			
			System.out.println(e);
		}

		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
		


	public String converterCodigoBarras(String tipo, String linha) {
		String codigoBarras = "";
		if (tipo.equalsIgnoreCase("Condominio")) {
			if (linha.length()>=44) {
				
			//Posicao 1:3
			String linha1 = linha.substring(0, 3);
			//Posicao 4:1
			String linha2 = linha.substring(3, 4);
			//Posicao 33:1
			String linha3 = linha.substring(32, 33);
			//Posicao 34:14
			String linha4 = linha.substring(33, 47);
			//Posicao 5:5
			String linha5 = linha.substring(4, 9);
			//Posicao 11:10
			String linha6 = linha.substring(10, 20);
			//Posicao 22:10
			String linha7 = linha.substring(21, 31);
			codigoBarras = linha1 + linha2 + linha3 + linha4 + linha5 + linha6 + linha7;
			}
		} else {
			//Posicao 0:11
			String linha1 = linha.substring(0,11);
			//Posicao 13:23
			String linha2 = linha.substring(12,23);
			//Posicao 25:35
			String linha3 = linha.substring(24,35);
			//Posicao 37:47
			String linha4 = linha.substring(36,47);
			codigoBarras = linha1 + linha2 + linha3 + linha4;
			
		}

		return codigoBarras;
	}
	
	public Float getValor(String codigoBarras) {
		Float valor = 0.0f;
		String sValor ="";
		if (codigoBarras.length()>15) {
			Conversor c = new  Conversor();
			sValor = codigoBarras.substring(9,19);
			DecimalFormat df = new DecimalFormat("0.00");
			df.setMaximumFractionDigits(2);
			valor = c.formatarStringfloat(sValor);
			valor = valor/100;
			sValor = df.format(valor);
			valor = c.formatarStringfloat(sValor);
		}
		return valor;
	}
	
	public String getDataVencimento(String linha) {
		Conversor conversor = new Conversor();
		Date date = conversor.ConvercaoStringData("1997-10-07");
		String sFator = linha.substring(5,9);
		date = conversor.SomarDiasData(date, Integer.parseInt(sFator));
		return conversor.ConvercaoDataBR(date);
	}
	
	public int getFornecedorCNPJ(String  cnpj) {
		cnpj = cnpj.replace(".","p");
		cnpj = cnpj.replace("/","b");
		cnpj = cnpj.replace("-","t");
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://192.168.1.95/robobrog-link/fornecdores/cnpj/" + cnpj; 
		Fornecedor fornecedor  = restTemplate.getForObject(url, Fornecedor.class); 
		return fornecedor.getCodigo();
	}
	
	
	public void exportarResultadoCasan(List<Imovelcasan> listaImoveis, String caminhoDir) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle();
		style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
		style.setAlignment(HorizontalAlignment.RIGHT);
		HSSFSheet firstSheet = workbook.createSheet("Debitos Casan");
		HSSFSheet op = workbook.createSheet("OP");
		FileOutputStream fos = null;

		try {
			if (caminhoDir.length() == 0) {
				file = new File("debitoscasan.xls");
			} else
				file = new File(caminhoDir + "debitoscasan.xls");
			fos = new FileOutputStream(file);
			int linha = 0;
			HSSFRow row = firstSheet.createRow(linha);
			row.createCell(0).setCellValue("Matricula");
			row.createCell(1).setCellValue("Mês Faturamento");
			row.createCell(2).setCellValue("Vencimento");
			row.createCell(3).setCellValue("Proprietario");
			row.createCell(4).setCellValue("CPF Proprietario");
			row.createCell(5).setCellValue("Usuario");
			row.createCell(6).setCellValue("CPF Usuario");
			row.createCell(7).setCellValue("Endereço");
			row.createCell(8).setCellValue("CEP");
			row.createCell(9).setCellValue("Municipio");
			row.createCell(10).setCellValue("Numero Hidrometro");
			row.createCell(11).setCellValue("Valor");
			row.createCell(12).setCellValue("Linha digitave");
			row.createCell(13).setCellValue("Situação");

			linha++;

			for (int i = 0; i < listaImoveis.size(); i++) {
				
				if (listaImoveis.get(i).getListaFatura() != null) {
					for (int n = 0; n < listaImoveis.get(i).getListaFatura().size(); n++) {
						row = firstSheet.createRow(linha);
						Fatura fatura = (listaImoveis.get(i).getListaFatura().get(n));
						row.createCell(0).setCellValue(fatura.getMatricula());
						row.createCell(1).setCellValue(fatura.getReferencia());
						row.createCell(2).setCellValue(fatura.getDatavencimento());
						row.createCell(3).setCellValue(fatura.getProprietario());
						row.createCell(4).setCellValue(fatura.getCpfProprietario());
						row.createCell(5).setCellValue(fatura.getUsuario());
						row.createCell(6).setCellValue(fatura.getCpfUsuario());
						row.createCell(7).setCellValue(fatura.getEndereco());
						row.createCell(8).setCellValue(fatura.getCep());
						row.createCell(9).setCellValue(fatura.getMunicipio());
						row.createCell(10).setCellValue(fatura.getNumeroHidrometro());
						row.createCell(11).setCellValue(fatura.getValor());
						row.createCell(12).setCellValue(fatura.getLinhaDigitavel());
						row.createCell(13).setCellValue(listaImoveis.get(i).getSituacao());
						linha++;
					}

				} else {
					row = firstSheet.createRow(linha);
					row.createCell(0).setCellValue(listaImoveis.get(i).getMatricula());
					row.createCell(1).setCellValue("");
					row.createCell(2).setCellValue("");
					row.createCell(3).setCellValue(listaImoveis.get(i).getProprietariocasan());
					row.createCell(4).setCellValue(listaImoveis.get(i).getCpfcasan());
					row.createCell(5).setCellValue("");
					row.createCell(6).setCellValue("");
					row.createCell(7).setCellValue("");
					row.createCell(8).setCellValue("");
					row.createCell(9).setCellValue("");
					row.createCell(10).setCellValue("");
					row.createCell(11).setCellValue(0);
					row.createCell(12).setCellValue("");
					row.createCell(13).setCellValue(listaImoveis.get(i).getSituacao());
					linha++;
				}
			}

			linha = 0;
			row = op.createRow(linha);

			row.createCell(0).setCellValue("Data Vencimento");
			row.createCell(1).setCellValue("Imovel");
			row.createCell(2).setCellValue("Conta");
			row.createCell(3).setCellValue("Agencia");
			row.createCell(4).setCellValue("Valor");
			row.createCell(5).setCellValue("Evento");
			row.createCell(6).setCellValue("Historico");
			row.createCell(7).setCellValue("Complemento");
			row.createCell(8).setCellValue("Cód.Barras");
			row.createCell(9).setCellValue("Fornecedor");
			row.createCell(10).setCellValue("Gerar");
			Conversor conversor = new Conversor();
			for (int i = 0; i < listaImoveis.size(); i++) {
				if (listaImoveis.get(i).getListaFatura() != null) {
					for (int n = 0; n < listaImoveis.get(i).getListaFatura().size(); n++) {
						try {
							linha++;
							String cb = converterCodigoBarras("casan",
									listaImoveis.get(i).getListaFatura().get(n).getLinhaDigitavel());
							String valor = "0,00";
							String dataVencimento = "";
							DecimalFormat df = new DecimalFormat("0.00");
							df.setMaximumFractionDigits(2);
							if (listaImoveis.get(i).getListaFatura().get(n).getValor() != null) {
								Float fvalor = conversor
										.formatarStringfloat(listaImoveis.get(i).getListaFatura().get(n).getValor());
								valor = df.format(fvalor);
								dataVencimento = listaImoveis.get(i).getListaFatura().get(n).getDatavencimento();
							}
							row = op.createRow(linha);
							row.createCell(0).setCellValue(dataVencimento);
							row.createCell(1).setCellValue(listaImoveis.get(i).getCodigoimovel());
							row.createCell(2).setCellValue("");
							row.createCell(3).setCellValue("");
							row.createCell(4).setCellValue(valor);
							row.getCell(4).setCellStyle(style);
							row.createCell(5).setCellValue("");
							row.createCell(6).setCellValue("");
							row.createCell(7).setCellValue("");
							row.createCell(8).setCellValue(cb);
							row.createCell(9).setCellValue(getFornecedorCNPJ("82.508.433/0001-17"));
							row.createCell(10).setCellValue("S");
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				}
			}

			workbook.write(fos);

			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exportarPDFAmbiental(List<BoletoAmbiental> listaAmbiental, String caminhoDir) {
		file = new File(caminhoDir + "Ambiental.xls");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Boletos");
		
		try {
			int i = 0;
			HSSFRow row = firstSheet.createRow(i);
			row.createCell(0).setCellValue("LinhaDigitavel");
			row.createCell(1).setCellValue("Inscrição");
            row.createCell(2).setCellValue("Competencia");
            row.createCell(3).setCellValue("Código Barras");
            row.createCell(3).setCellValue("NomeArquivo");
            
                        
			
			i++;
			
			
			for (BoletoAmbiental c : listaAmbiental) {
				row = firstSheet.createRow(i);
				row.createCell(0).setCellValue(c.getLinhaDigitavel());
				row.createCell(1).setCellValue(c.getInscricao());
				row.createCell(2).setCellValue(c.getMes());                                
                row.createCell(3).setCellValue(c.getCodigoBarras());
                row.createCell(3).setCellValue(c.getNomeArquivo());
				i++;
			}
			workbook.write(fos);

		}catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
     }
}
