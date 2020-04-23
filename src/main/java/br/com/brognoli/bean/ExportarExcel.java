package br.com.brognoli.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import br.com.brognoli.model.Boletos;
import br.com.brognoli.model.Resumo;

public class ExportarExcel {

	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void gerar(List<Boletos> listaBoletos, String mesano) {

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

}
