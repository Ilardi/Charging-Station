package com.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.project.model.Prenotazione;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PrenotazioneRepository extends MongoRepository<Prenotazione, String> {
    List<Prenotazione> findByidColonnina(Integer idColonnina);
    List<Prenotazione> findByEmailUtente(String EmailUtente);
    @Query("{'idColonnina': ?0, 'colonnina': ?3, 'connessione': ?4, $or: [{'Inizio': {$lt: ?1}, 'Fine': {$gt: ?1}}, {'Inizio': {$lt: ?2}, 'Fine': {$gt: ?2}}, {'Inizio': {$gte: ?1}, 'Fine': {$lte: ?2}}]}")
    List<Prenotazione> findOverlappingReservations(
    		Integer idColonnina, Date newStartDate, Date newEndDate, Integer colonnina, Integer connessione);
    void deleteByidColonnina(Integer idColonnina);

    default List<String> findEmailUtenteByidColonnina(Integer idColonnina) {
        List<Prenotazione> prenotazioni = findByidColonnina(idColonnina);     
        List<String> emailUtenti = prenotazioni.stream()
                .map(Prenotazione::getemailUtente)
                .collect(Collectors.toList());
        return emailUtenti;
    }

    default String findEmailUtenteById(String prenotazioneId) {
        Optional<Prenotazione> prenotazioneOptional = findById(prenotazioneId);
        return prenotazioneOptional.map(Prenotazione::getemailUtente).orElse(null);
    }    
    
}