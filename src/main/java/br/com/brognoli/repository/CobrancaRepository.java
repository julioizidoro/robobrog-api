package br.com.brognoli.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.brognoli.model.Cobranca;


public interface CobrancaRepository extends JpaRepository<Cobranca, Integer>{
	
	@Query("select c from Cobranca c where c.id_unidade= :idunidade "
			+ " and c.data_ref_rateio= :datarateio ")
		Cobranca getCobranca(@Param("idunidade") String idunidade, @Param("datarateio") String datarateio);
	
	@Query("select c from Cobranca c where c.administradora= :administradora ")
		List<Cobranca> listarCobranca(@Param("administradora") String administradora);
	
	@Query("select c from Cobranca c where c.administradora= :administradora and c.condominio= :cond")
	List<Cobranca> listarCobranca(@Param("administradora") String administradora, @Param("cond") String cond);
	
}
