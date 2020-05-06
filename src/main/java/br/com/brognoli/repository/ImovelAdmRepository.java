package br.com.brognoli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.brognoli.model.Imoveladm;

public interface ImovelAdmRepository extends JpaRepository<Imoveladm, Integer>{
	
	@Query("select i from Imoveladm i where i.imovel= :imovel ")
	Imoveladm getImovel(@Param("imovel") int imovel);
	

}
