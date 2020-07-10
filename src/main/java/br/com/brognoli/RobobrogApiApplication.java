package br.com.brognoli;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
