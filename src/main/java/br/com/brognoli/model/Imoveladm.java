package br.com.brognoli.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "imoveladm")
public class Imoveladm {
	
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "imovel")
    private Integer imovel;
	@Column(name = "edificio")
    private String edificio;
	@Column(name = "codigoadm")
    private Integer codigoadm;
	@Column(name = "administradora")
    private String administradora;
	@Column(name = "admwinker")
    private String admwinker;
	
	
	public Imoveladm() {
	
	}


	public Integer getImovel() {
		return imovel;
	}


	public String getEdificio() {
		return edificio;
	}


	public Integer getCodigoadm() {
		return codigoadm;
	}


	public String getAdministradora() {
		return administradora;
	}


	public String getAdmwinker() {
		return admwinker;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imovel == null) ? 0 : imovel.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Imoveladm other = (Imoveladm) obj;
		if (imovel == null) {
			if (other.imovel != null)
				return false;
		} else if (!imovel.equals(other.imovel))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Imoveladm [imovel=" + imovel + "]";
	}
	
	
	
	
	
	

}
