package br.com.brognoli.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


@Entity
@Table(name = "cobranca")
public class Cobranca implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcobranca")
    private Integer idcobranca;
	private String id_rateio;
	
	@Temporal(TemporalType.DATE)
    private Date data_vencimento;
    private String valor_total_cobranca;
    private String condominio;
    @Temporal(TemporalType.DATE)
    private Date data_pagamento;
    private String id_unidade;
    private String data_ref_rateio;
    private String serie_rateio;
    private String valor_fundo_reserva;
    @Temporal(TemporalType.DATE)
    private Date data_cancelamento;
    private String url_arquivo_cobranca;
    private String referencia;
    private String id;
    private String administradora;
    private String id_condominio;
    private String bairro;
    private String cidade;
    private String uf;
    private String cnpj;
    private String unidade;
    private String id_unidade_cobranca;
    private String condominio_ativo;
    private boolean segunda_via_habilitada;
    private String cobranca_disponivel;
    private String possui_segunda_via_habilitada;
    @Column(name = "hasrateio")
    private String hasRateio;
    @Column(name = "hascobranca")
    private String hasCobranca;
    @Column(name = "isvencido")
    private boolean isVencido;
    @Column(name = "datacadastrowinker")
    private String dataCadastroWinker;
    private String situacao;
    
    public Integer getIdcobranca() {
		return idcobranca;
	}
	public void setIdcobranca(Integer idcobranca) {
		this.idcobranca = idcobranca;
	}
	
	public String getId_rateio() {
		return id_rateio;
	}
	public void setId_rateio(String id_rateio) {
		this.id_rateio = id_rateio;
	}
	public Date getData_vencimento() {
		return data_vencimento;
	}
	public void setData_vencimento(Date data_vencimento) {
		this.data_vencimento = data_vencimento;
	}
	public String getValor_total_cobranca() {
		return valor_total_cobranca;
	}
	public void setValor_total_cobranca(String valor_total_cobranca) {
		this.valor_total_cobranca = valor_total_cobranca;
	}
	public String getCondominio() {
		return condominio;
	}
	public void setCondominio(String condominio) {
		this.condominio = condominio;
	}
	public Date getData_pagamento() {
		return data_pagamento;
	}
	public void setData_pagamento(Date data_pagamento) {
		this.data_pagamento = data_pagamento;
	}
	public String getId_unidade() {
		return id_unidade;
	}
	public void setId_unidade(String id_unidade) {
		this.id_unidade = id_unidade;
	}
	public String getData_ref_rateio() {
		return data_ref_rateio;
	}
	public void setData_ref_rateio(String data_ref_rateio) {
		this.data_ref_rateio = data_ref_rateio;
	}
	public String getSerie_rateio() {
		return serie_rateio;
	}
	public void setSerie_rateio(String serie_rateio) {
		this.serie_rateio = serie_rateio;
	}
	public String getValor_fundo_reserva() {
		return valor_fundo_reserva;
	}
	public void setValor_fundo_reserva(String valor_fundo_reserva) {
		this.valor_fundo_reserva = valor_fundo_reserva;
	}
	public Date getData_cancelamento() {
		return data_cancelamento;
	}
	public void setData_cancelamento(Date data_cancelamento) {
		this.data_cancelamento = data_cancelamento;
	}
	public String getUrl_arquivo_cobranca() {
		return url_arquivo_cobranca;
	}
	public void setUrl_arquivo_cobranca(String url_arquivo_cobranca) {
		this.url_arquivo_cobranca = url_arquivo_cobranca;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAdministradora() {
		return administradora;
	}
	public void setAdministradora(String administradora) {
		this.administradora = administradora;
	}
	public String getId_condominio() {
		return id_condominio;
	}
	public void setId_condominio(String id_condominio) {
		this.id_condominio = id_condominio;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getId_unidade_cobranca() {
		return id_unidade_cobranca;
	}
	public void setId_unidade_cobranca(String id_unidade_cobranca) {
		this.id_unidade_cobranca = id_unidade_cobranca;
	}
	public String getCondominio_ativo() {
		return condominio_ativo;
	}
	public void setCondominio_ativo(String condominio_ativo) {
		this.condominio_ativo = condominio_ativo;
	}
	public boolean isSegunda_via_habilitada() {
		return segunda_via_habilitada;
	}
	public void setSegunda_via_habilitada(boolean segunda_via_habilitada) {
		this.segunda_via_habilitada = segunda_via_habilitada;
	}
	public String getCobranca_disponivel() {
		return cobranca_disponivel;
	}
	public void setCobranca_disponivel(String cobranca_disponivel) {
		this.cobranca_disponivel = cobranca_disponivel;
	}
	public String getPossui_segunda_via_habilitada() {
		return possui_segunda_via_habilitada;
	}
	public void setPossui_segunda_via_habilitada(String possui_segunda_via_habilitada) {
		this.possui_segunda_via_habilitada = possui_segunda_via_habilitada;
	}
	public String getHasRateio() {
		return hasRateio;
	}
	public void setHasRateio(String hasRateio) {
		this.hasRateio = hasRateio;
	}
	public String getHasCobranca() {
		return hasCobranca;
	}
	public void setHasCobranca(String hasCobranca) {
		this.hasCobranca = hasCobranca;
	}
	public boolean isVencido() {
		return isVencido;
	}
	public void setVencido(boolean isVencido) {
		this.isVencido = isVencido;
	}
	public String getDataCadastroWinker() {
		return dataCadastroWinker;
	}
	public void setDataCadastroWinker(String dataCadastroWinker) {
		this.dataCadastroWinker = dataCadastroWinker;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idcobranca == null) ? 0 : idcobranca.hashCode());
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
		Cobranca other = (Cobranca) obj;
		if (idcobranca == null) {
			if (other.idcobranca != null)
				return false;
		} else if (!idcobranca.equals(other.idcobranca))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Cobranca [idcobranca=" + idcobranca + "]";
	}
    
    

}
