package br.com.brognoli;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
public class RobobrogApiApplication {

	@PostConstruct
    public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
	
	public static void main(String[] args) {
		SpringApplication.run(RobobrogApiApplication.class, args);
	}

}
