package br.com.brognoli.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Conversor {
	
	public Date ConvercaoStringData(String data) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dataFormatada = null;
        try {
            dataFormatada = df.parse(data);
        } catch (ParseException ex) {
            Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataFormatada;
    }
	
	public Date SomarDiasData(Date data, int dias) {
        Calendar c = new GregorianCalendar();
        c.setTime(data);
        if (dias != 0) {
            c.add(Calendar.DAY_OF_MONTH, dias);
        }
        return (c.getTime());
    }
	
	public String ConvercaoData(Date data) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dataFormatada = df.format(data);
        return dataFormatada;
    }
	
	
	public String ConvercaoDataBR(Date data) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = df.format(data);
        return dataFormatada;
    }
	
	
		
	public String getMesAno(Date data) {
		String sData = ConvercaoDataBR(data);
		String mes = sData.substring(3,5);
		String ano = sData.substring(6,10);
		String mesAno = String.valueOf(ano) + "_" + String.valueOf(mes);
		return mesAno;
	}

}


/*
 * spring.datasource.url=jdbc:mysql://tmmysql.cxjytqucztmb.us-east-1.rds.
 * amazonaws.com:3306/tecseg?Timezone=true&serverTimezone=UTC
 * spring.datasource.username=master spring.datasource.password=Travel2018#
 * spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect
 */
