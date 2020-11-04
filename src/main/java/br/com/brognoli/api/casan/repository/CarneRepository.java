package br.com.brognoli.api.casan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.brognoli.api.model.Carne;

public interface CarneRepository extends JpaRepository<Carne, Integer>{
	

	@Query(
		value = "Select * from carne ",
		nativeQuery = true)
	List<Carne> findAll();
	

}
