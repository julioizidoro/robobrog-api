package br.com.brognoli.api.casan.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.brognoli.api.bean.ExportarExcel;
import br.com.brognoli.api.casan.bean.LerCasanDocumentos;
import br.com.brognoli.api.casan.bean.LerSite;
import br.com.brognoli.api.casan.model.Fatura;
import br.com.brognoli.api.casan.model.Imovelcasan;
import br.com.brognoli.api.casan.repository.ImoveisRepository;
import br.com.brognoli.api.service.S3Service;
import br.com.brognoli.api.util.Conversor;

@CrossOrigin
@RestController
@RequestMapping("/casan")
public class DebitosController {
	
	private List<Imovelcasan> listaImoveis;
	@Autowired
	private ImoveisRepository imoveisRepository;
	@Autowired
	private S3Service s3Service;

	@PostMapping("/setlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void getDeibtos(@Valid @RequestBody List<Imovelcasan> lista) {
		//LerSite siteCasan = new LerSite();
		listaImoveis = lista;
		validarSituacaoImovel();//siteCasan.getBoletos(lista);
		//return "Lista da API " + String.valueOf(lista.size());
	}
	
	@GetMapping("/getlista")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Imovelcasan> getLista() throws Exception {
		
		return listaImoveis;
		
	}
	
	@GetMapping("/gerarpdf")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Imovelcasan> gerarPDF()  {
		LerSite siteCasan = new LerSite();
		listaImoveis = siteCasan.getBoletos(listaImoveis); 
		return listaImoveis;
	}
	
	@GetMapping("/gerarresultado")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> getExcelResultados() throws Exception {
		LerSite siteCasan = new LerSite();
		try {
			if (listaImoveis!=null) {
				if (listaImoveis.size()>0) {
					siteCasan.exportarExcelResultado(listaImoveis);
					File file = siteCasan.getFile();
					URI uri = s3Service.uploadFile(file);
					return ResponseEntity.created(uri).build();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.notFound().build();
		
	}
	
	@GetMapping("/gerardebitos")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> getExcelDebitos() throws Exception {
		LerSite siteCasan = new LerSite();
		try {
			if (listaImoveis!=null) {
				if (listaImoveis.size()>0) {
					siteCasan.exportarExcel(listaImoveis);
					File file = siteCasan.getFile();
					URI uri = s3Service.uploadFile(file);
					return ResponseEntity.created(uri).build();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.notFound().build();
		
	}
	
	@GetMapping("/consulta/debitos/{nomepasta}")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Imovelcasan> getDebitos(@PathVariable("nomepasta") String nomepasta)  {
		if (nomepasta.equalsIgnoreCase("@")) {
			Conversor c = new Conversor();
			nomepasta = c.getDiaMesAno(new Date());
		}
		String caminhoDir="\\\\192.168.1.58\\documentos\\centralfinanceira\\BOLETOS DE CONDOMÍNIOS\\casan\\" + nomepasta + "\\";
		File dirFile = new File(caminhoDir);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		LerSite siteCasan = new LerSite();
		String d = dirFile.getAbsolutePath().toString();
		siteCasan.setDiretorio(d);
		listaImoveis = siteCasan.getBoletos(listaImoveis); 
		if (listaImoveis.size()>0) {
			for (int i=0;i<listaImoveis.size();i++) {
			if (listaImoveis.get(i).getListaFatura()!=null) {
				if (listaImoveis.get(i).getListaFatura().size()>0) {
					List<Fatura> novaLita = new ArrayList<Fatura>();
					for (int f=0;f<listaImoveis.get(i).getListaFatura().size();f++) {
						Fatura fatura = listaImoveis.get(i).getListaFatura().get(f);
						try {
							fatura = siteCasan.lerPDF(fatura);
							novaLita.add(fatura);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					listaImoveis.get(i).setListaFatura(novaLita);
				}
			}
		}
			ExportarExcel ex = new ExportarExcel();
			try {
				ex.exportarResultadoCasan(listaImoveis, caminhoDir);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listaImoveis;
	}
	
	@GetMapping("/consulta/debitos")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Imovelcasan> getDebitos()  {
		LerSite siteCasan = new LerSite();
		listaImoveis = siteCasan.getBoletos(listaImoveis); 
		if (listaImoveis.size()>0) {
			if (listaImoveis.get(0).getListaFatura()!=null) {
				if (listaImoveis.get(0).getListaFatura().size()>0) {
					List<Fatura> novaLita = new ArrayList<Fatura>();
					for (int i=0;i<listaImoveis.get(0).getListaFatura().size();i++) {
						Fatura fatura = listaImoveis.get(0).getListaFatura().get(i);
						try {
							fatura = siteCasan.lerPDF(fatura);
							novaLita.add(fatura);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					listaImoveis.get(0).setListaFatura(novaLita);
				}
			}
		}
		return listaImoveis;
	}
	
	@GetMapping("/consulta/certidao")
	@ResponseStatus(HttpStatus.CREATED)
	public Imovelcasan getCertidao()  {
		LerCasanDocumentos siteCasan = new LerCasanDocumentos();
		Imovelcasan imovel = listaImoveis.get(0);
		imovel = siteCasan.getCertidaoNegativa(imovel);
		return imovel;
	}
	
	@GetMapping("/consulta/quitacao")
	@ResponseStatus(HttpStatus.CREATED)
	public Imovelcasan getQuitacao()  {
		LerCasanDocumentos siteCasan = new LerCasanDocumentos();
		Imovelcasan imovel = listaImoveis.get(0);
		imovel = siteCasan.getQuitacaoAnual(imovel);
		return imovel;
	}
	
	public void validarSituacaoImovel() {
	    for( int i=0;i<this.listaImoveis.size();i++) {
	      if(this.listaImoveis.get(i).getMatricula()==null) {
	        this.listaImoveis.get(i).setSituacao("EM BRANCO");
	      } else if(this.listaImoveis.get(i).getMatricula().length()==0) {
	        this.listaImoveis.get(i).setSituacao("MATRIULA EM BRANCO");
	      } else if(this.listaImoveis.get(i).getCpfcasan()==null) {
	        this.listaImoveis.get(i).setSituacao("CPF EM BRANCO");
	      } else if(this.listaImoveis.get(i).getCpfcasan().length()==0) {
	        this.listaImoveis.get(i).setSituacao("CPF EM BRANCO");
	      } else if(this.listaImoveis.get(i).getMatricula()=="RATEIO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      }else if(this.listaImoveis.get(i).getMatricula()=="COND.") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="APARTAMENTO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="APTO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula().length()<6) {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="KITNET") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	     // } else if(this.listaImoveis.get(i).getMatricula().search("X")) {
	       // this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="CONDOMINIO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="DESLIGADA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="EDIFICIO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="EVENTO FIXO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="GALPÃO / DEPÓSITO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="IM. NOVO NÃO TEM") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="IMÓVEL NOVO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="INCLUSO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="INCLUSO NO ALUGUEL") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="ÁGUA DE PONTEIRA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="TERRENO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="TAXA MINIMA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SEM MEDIDOR") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SEM HID") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SEM") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SEM MAT") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SALA EM CONDOMÍNIO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SALA COMERCIAL") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="SALA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="PRÉDIO S/ MATRÍCULA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="PROP VAI ENVIAR") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="PREDIO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="POÇO ARTESIANO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="POÇO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="PONTEIRA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="PEDIR") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="PAGA DIRETO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="OBS") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="NOVO S/COND") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="NAO TEM") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="LOJA") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula() =="ISENTO") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getMatricula()=="INDIVIDUAL") {
	        this.listaImoveis.get(i).setSituacao("MATRICULA INVÁLIDA");
	      } else if(this.listaImoveis.get(i).getCpfcasan().length()<11) {
	        this.listaImoveis.get(i).setSituacao("CPF INVÁLIDA");
	      } else {
	        this.listaImoveis.get(i).setSituacao("OK");
	      }
	    }

	  }
	
	
	@PostMapping("/gerarlista")
	@ResponseStatus(HttpStatus.CREATED)
	public void lerXLSX(@RequestParam(name = "file") MultipartFile file) throws IOException, InvalidFormatException {
		listaImoveis = new ArrayList<Imovelcasan>();
		String url = "c:\\logs\\" + file.getOriginalFilename();
		File fileToSave = new File(url);
		fileToSave.createNewFile();
		FileOutputStream fos = new FileOutputStream(fileToSave);
		fos.write(file.getBytes());
		fos.close();
		BufferedReader br = null;
		InputStream is = file.getInputStream();
		OPCPackage pkg = OPCPackage.open(fileToSave);
		XSSFWorkbook workbook = new XSSFWorkbook(pkg);
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator linhas = sheet.rowIterator();
		linhas.next();
		while (linhas.hasNext()) {
			XSSFRow linha = (XSSFRow) linhas.next();
			Iterator celulas = linha.cellIterator();
			Imovelcasan casan = new Imovelcasan();
			casan.setCpfcasan("");
			casan.setCpflocatario("");
			casan.setDescricaosituacao("");
			casan.setLocatario("");
			casan.setMatricula(null);
			casan.setNomeproprietario("");
			casan.setProprietariocasan("");
			casan.setSituacao("OK");
			casan.setSituacaolink("");
			casan.setUsuarioatual("");
			
			while (celulas.hasNext()) {
				XSSFCell cel = (XSSFCell) celulas.next();
				int z = cel.getColumnIndex();
				switch (z) {
				case 0:
					if (cel.getCellTypeEnum() == CellType.STRING) {
						String dados = cel.getStringCellValue();
						casan.setCodigoimovel(Integer.parseInt(dados));
					} else if (cel.getCellTypeEnum() == CellType.NUMERIC) {
						int dados = (int) cel.getNumericCellValue();
						casan.setCodigoimovel(dados);
					}
				case 1:
					if (cel.getCellTypeEnum() == CellType.STRING) {
						String dados = cel.getStringCellValue();
						dados = dados.replace("*", "");
						casan.setMatricula(dados);
					} else if (cel.getCellTypeEnum() == CellType.NUMERIC) {
						int dados = (int) cel.getNumericCellValue();
						casan.setMatricula(String.valueOf(dados));
					}
				case 2:
					if (cel.getCellTypeEnum() == CellType.STRING) {
						String dados = cel.getStringCellValue();
						casan.setCpfcasan(dados);
					} else if (cel.getCellTypeEnum() == CellType.NUMERIC) {
						int dados = (int) cel.getNumericCellValue();
						casan.setCpfcasan(String.valueOf(dados));
					}
				}
					
			}
			
			listaImoveis.add(casan);
		}
	}
}
