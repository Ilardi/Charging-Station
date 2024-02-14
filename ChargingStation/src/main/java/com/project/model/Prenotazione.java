package com.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Prenotazioni")
public class Prenotazione {
    @Id
    private String id;

    private Integer idColonnina;
    private String emailUtente;
    private Date inizio;
    private Date fine;
    private Integer colonnina;
    private Integer connessione;
    
    
	public Prenotazione(String id, Integer idColonnina, String emailUtente, Date inizio, Date fine, Integer colonnina, Integer connessione) {
		super();
		this.id = id;
		this.idColonnina = idColonnina;
		this.emailUtente = emailUtente;
		this.inizio = inizio;
		this.fine = fine;
		this.colonnina = colonnina;
		this.connessione = connessione;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIdColonnina() {
		return idColonnina;
	}

	public void setIdColonnina(Integer idColonnina) {
		this.idColonnina = idColonnina;
	}

	public String getemailUtente() {
		return emailUtente;
	}

	public void setemailUtente(String emailUtente) {
		this.emailUtente = emailUtente;
	}

	public Date getinizio() {
		return inizio;
	}

	public void setinizio(Date inizio) {
		this.inizio = inizio;
	}

	public Date getfine() {
		return fine;
	}

	public void setfine(Date fine) {
		this.fine = fine;
	}

	public Integer getColonnina() {
		return colonnina;
	}

	public void setColonnina(Integer colonnina) {
		this.colonnina = colonnina;
	}

	public Integer getConnessione() {
		return connessione;
	}

	public void setConnessione(Integer connessione) {
		this.connessione = connessione;
	}

	
    
}