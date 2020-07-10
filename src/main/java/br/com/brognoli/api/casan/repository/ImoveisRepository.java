package br.com.brognoli.api.casan.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.brognoli.api.casan.model.Imovelcasan;



@Repository
public interface ImoveisRepository extends JpaRepository<Imovelcasan, Integer>{
	

	@Query(
		value = "Select * from imovelcasan ",
		nativeQuery = true)
	List<Imovelcasan> findAll();
	

}
