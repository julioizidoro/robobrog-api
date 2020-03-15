package br.com.brognoli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.brognoli.model.Cobrancaarquivo;

public interface CobrancaArquivoRepository  extends JpaRepository<Cobrancaarquivo, Integer> {
	
	@Query("select a from Cobrancaarquivo a where a.cobranca.idcobranca= :idCobranca")
	Cobrancaarquivo getArquivo(@Param("idCobranca") int idCobranca);

}
