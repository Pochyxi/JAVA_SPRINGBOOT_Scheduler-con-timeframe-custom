package com.adi.scheduler.service;

import com.adi.scheduler.entity.ConfigurazioneScheduler;

import java.util.Optional;

/**
 * Interfaccia che definisce i servizi per la gestione delle configurazioni degli scheduler.
 * Fornisce metodi per recuperare e salvare le configurazioni degli scheduler.
 */
public interface ConfiguratoreSchedulerService {

    /**
     * Recupera la configurazione di uno scheduler specifico.
     *
     * @param nomeTask Il nome del task scheduler da recuperare.
     * @return Un Optional contenente la ConfigurazioneScheduler se trovata, altrimenti un Optional vuoto.
     */
    Optional<ConfigurazioneScheduler> getConfigurazioneScheduler(String nomeTask);

    /**
     * Salva o aggiorna la configurazione di uno scheduler.
     *
     * @param configurazioneScheduler L'oggetto ConfigurazioneScheduler da salvare o aggiornare.
     */
    void salva(ConfigurazioneScheduler configurazioneScheduler);
}
