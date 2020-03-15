package br.com.brognoli.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "cobrancaarquivo")
public class Cobrancaarquivo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcobrancaarquivo")
    private Integer idcobrancaarquivo;
	@Column(name = "nomearquivo")
    private String nomearquivo;
	@Column(name = "datagravacao")
    @Temporal(TemporalType.DATE)
    private Date datagravacao;
	@JoinColumn(name = "cobranca_idcobranca", referencedColumnName = "idcobranca")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Cobranca cobranca;
	public Integer getIdcobrancaarquivo() {
		return idcobrancaarquivo;
	}
	public void setIdcobrancaarquivo(Integer idcobrancaarquivo) {
		this.idcobrancaarquivo = idcobrancaarquivo;
	}
	public String getNomearquivo() {
		return nomearquivo;
	}
	public void setNomearquivo(String nomearquivo) {
		this.nomearquivo = nomearquivo;
	}
	public Date getDatagravacao() {
		return datagravacao;
	}
	public void setDatagravacao(Date datagravacao) {
		this.datagravacao = datagravacao;
	}
	public Cobranca getCobranca() {
		return cobranca;
	}
	public void setCobranca(Cobranca cobranca) {
		this.cobranca = cobranca;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idcobrancaarquivo == null) ? 0 : idcobrancaarquivo.hashCode());
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
		Cobrancaarquivo other = (Cobrancaarquivo) obj;
		if (idcobrancaarquivo == null) {
			if (other.idcobrancaarquivo != null)
				return false;
		} else if (!idcobrancaarquivo.equals(other.idcobrancaarquivo))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Cobrancaarquivo [idcobrancaarquivo=" + idcobrancaarquivo + ", nomearquivo=" + nomearquivo + "]";
	}
	
	

}
