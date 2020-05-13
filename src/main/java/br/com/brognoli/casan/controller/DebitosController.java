package br.com.brognoli.casan.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.brognoli.bean.Diretorios;
import br.com.brognoli.casan.bean.LerSite;
import br.com.brognoli.casan.model.Imovelcasan;
import br.com.brognoli.casan.repository.ImoveisRepository;

@CrossOrigin
@RestController
@RequestMapping("/casan")
public class DebitosController {
	
	private List<Imovelcasan> listaImoveis;
	@Autowired
	private ImoveisRepository imoveisRepository;

	@PostMapping("/gerarpdf")
	@ResponseStatus(HttpStatus.CREATED)
	public void getDeibtos(@Valid @RequestBody List<Imovelcasan> lista) {
		LerSite siteCasan = new LerSite();
		listaImoveis = siteCasan.getBoletos(lista);
		
	}
	
	@GetMapping("/gerarexcel")
	@ResponseStatus(HttpStatus.CREATED)
	public void getExcelDebitos() throws Exception {
		LerSite siteCasan = new LerSite();
		try {
			listaImoveis = siteCasan.gerarExcelPDF(listaImoveis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@GetMapping("/gerarresultado")
	@ResponseStatus(HttpStatus.CREATED)
	public void getExcelResultados() throws Exception {
		LerSite siteCasan = new LerSite();
		try {
			siteCasan.exportarExcelResultado(listaImoveis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
