package br.com.brognoli.api.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.brognoli.api.model.Boletos;
import br.com.brognoli.api.model.Boletoseguro;
import br.com.brognoli.api.model.Carne;
import br.com.brognoli.api.model.CarneIPTU;
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
			file = new File("Winker_" + mesano + ".xls");
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
				file = new File("GarantiaTotal.xls");
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
	
public void gerarOp(List<Boletos> listaBoletos) {
		
		
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet gs = workbook.createSheet("Eventos Valores");
		HSSFSheet op = workbook.createSheet("OP");
		FileOutputStream fos = null;
		HSSFCellStyle style =  workbook.createCellStyle();
		style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
		style.setAlignment(HorizontalAlignment.RIGHT);

		try {
			file = new File("GarantiaTotalS.xls");
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
	
	public void exportarResultadoExcelSJ(List<CarneIPTU> listaIPTU) {
        HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("DAM-PM");
		FileOutputStream fos = null;
		try {
			file = new File("IPTU_SJ.xls");
			fos = new FileOutputStream(file);
			int i = 0;
			HSSFRow row = firstSheet.createRow(i);
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
				row = firstSheet.createRow(i);
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
	
		public void gerarSeguro(List<Boletoseguro> listaBoletos) {
		
		
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet firstSheet = workbook.createSheet("Eventos Valores");
		FileOutputStream fos = null;

		try {
			file = new File("BoletoSeguros.xls");
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
			String linha4 = linha.substring(36,48);
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
	
	
	
	
	

}
