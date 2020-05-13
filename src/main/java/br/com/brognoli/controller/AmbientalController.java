package br.com.brognoli.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.brognoli.model.Carne;



@CrossOrigin("*")
@RestController
@RequestMapping("/ambiental")
public class AmbientalController {
	
	private List<Carne> listaCarne;
	
	@GetMapping("sj/getlista")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<List<Carne>> getListaCarne() {
		return ResponseEntity.ok(listaCarne);
	}
	
	@PostMapping("sj/gerarlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void listarCobranca(@RequestParam(name="file") MultipartFile file) {
		try {
			lerXLSX(file);
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void lerXLSX(MultipartFile file) throws IOException, InvalidFormatException {
        listaCarne = new ArrayList<Carne>();
        InputStream is = file.getInputStream();
        OPCPackage pkg = OPCPackage.open(is);
        XSSFWorkbook workbook = new XSSFWorkbook(pkg);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator linhas = sheet.rowIterator();
        linhas.next();
         while (linhas.hasNext()) {
             XSSFRow linha = (XSSFRow) linhas.next();
             Iterator celulas = linha.cellIterator();
             Carne carne = new Carne();
             carne.setSituacao("Carregado");
             while (celulas.hasNext()) {
                XSSFCell celula = (XSSFCell) celulas.next();
                int z = celula.getColumnIndex();
                 switch (z) {
                     case 0:
                         carne.setInscricao(celula.toString());
                     case 1:
                         if (CellType.NUMERIC.equals(celula.getCellTypeEnum())) {
                         int valor = (int) celula.getNumericCellValue();
                         String digitos= Integer.toString(valor);
                          carne.setCadastro(digitos);
                         }
                 }
             }
             listaCarne.add(carne);
         }
    }

}
