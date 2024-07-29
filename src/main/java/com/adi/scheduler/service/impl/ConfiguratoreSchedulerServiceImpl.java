package com.adi.scheduler.service.impl;

import com.adi.scheduler.entity.ConfigurazioneScheduler;
import com.adi.scheduler.repository.ConfigurazioneSchedulerRepository;
import com.adi.scheduler.service.ConfiguratoreSchedulerService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementazione del servizio ConfiguratoreSchedulerService.
 * Questa classe fornisce la logica concreta per gestire le configurazioni degli scheduler
 * interagendo con il repository ConfigurazioneSchedulerRepository.
 */
@Service
public class ConfiguratoreSchedulerServiceImpl implements ConfiguratoreSchedulerService {

    private final ConfigurazioneSchedulerRepository repository;

    /**
     * Costruttore per l'iniezione delle dipendenze.
     *
     * @param repository Il repository per accedere ai dati delle configurazioni degli scheduler.
     */
    public ConfiguratoreSchedulerServiceImpl(final ConfigurazioneSchedulerRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     * Recupera la configurazione di uno scheduler specifico dal repository.
     */
    @Override
    public Optional<ConfigurazioneScheduler> getConfigurazioneScheduler(String nomeTask) {
        return repository.findById(nomeTask);
    }

    /**
     * {@inheritDoc}
     * Salva o aggiorna la configurazione di uno scheduler nel repository.
     */
    @Override
    public void salva(ConfigurazioneScheduler configurazioneScheduler) {
        repository.save(configurazioneScheduler);
    }
}
