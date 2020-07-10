package br.com.brognoli.api.casan.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity(name= "IMOVELCASAN")
public class Imovelcasan implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	private int codigoimovel;
	private String matricula;
	private String nomeproprietario;
	private String cpfcasan;
	private String locatario;
	private String cpflocatario;
	private String situacaolink;
	@Transient
	private String proprietariocasan;
	@Transient
	private String usuarioatual;
	@Transient
	private String situacao;
	@Transient
	private String descricaosituacao;
	@Transient
	private List<Fatura> listaFatura;
	
	
	
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getNomeproprietario() {
		return nomeproprietario;
	}
	public void setNomeproprietario(String nomeproprietario) {
		this.nomeproprietario = nomeproprietario;
	}
	public String getCpfcasan() {
		return cpfcasan;
	}
	public void setCpfcasan(String cpfcasan) {
		this.cpfcasan = cpfcasan;
	}
	public String getLocatario() {
		return locatario;
	}
	public void setLocatario(String locatario) {
		this.locatario = locatario;
	}
	public String getCpflocatario() {
		return cpflocatario;
	}
	public void setCpflocatario(String cpflocatario) {
		this.cpflocatario = cpflocatario;
	}
	public String getProprietariocasan() {
		return proprietariocasan;
	}
	public void setProprietariocasan(String proprietariocasan) {
		this.proprietariocasan = proprietariocasan;
	}
	public String getUsuarioatual() {
		return usuarioatual;
	}
	public void setUsuarioatual(String usuarioatual) {
		this.usuarioatual = usuarioatual;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public List<Fatura> getListaFatura() {
		return listaFatura;
	}
	public void setListaFatura(List<Fatura> listaFatura) {
		this.listaFatura = listaFatura;
	}
	public int getCodigoimovel() {
		return codigoimovel;
	}
	public void setCodigoimovel(int codigoimovel) {
		this.codigoimovel = codigoimovel;
	}
	
	public String getDescricaosituacao() {
		return descricaosituacao;
	}
	public void setDescricaosituacao(String descricaosituacao) {
		this.descricaosituacao = descricaosituacao;
	}
	
	
	
	public String getSituacaolink() {
		return situacaolink;
	}
	public void setSituacaolink(String situacaolink) {
		this.situacaolink = situacaolink;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigoimovel;
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
		Imovelcasan other = (Imovelcasan) obj;
		if (codigoimovel != other.codigoimovel)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Imovelcasan [codigoimovel=" + codigoimovel + "]";
	}
	
	
	
	
	
}
