package br.com.brognoli.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.brognoli.model.Cobrancaarquivo;

public interface CobrancaArquivoRepository  extends JpaRepository<Cobrancaarquivo, Integer> {
	
	@Query("select a from Cobrancaarquivo a where a.cobranca.idcobranca= :idCobranca")
	Cobrancaarquivo getArquivo(@Param("idCobranca") int idCobranca);
	
	@Query("select a from Cobrancaarquivo a where a.cobranca.data_ref_rateio= :mesano and a.cobranca.administradora like CONCAT('%', :administradora, '%') order by a.cobranca.condominio, a.cobranca.unidade" )
	List<Cobrancaarquivo> getArquivos(@Param("administradora") String administradora, @Param("mesano") String mesano);
}
