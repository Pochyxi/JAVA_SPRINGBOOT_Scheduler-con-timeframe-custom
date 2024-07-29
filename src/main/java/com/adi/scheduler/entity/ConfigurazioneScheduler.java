package com.adi.scheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rappresenta la configurazione di uno scheduler nel sistema.
 * Questa entità viene utilizzata per gestire dinamicamente gli scheduler
 * memorizzando le loro configurazioni nel database.
 */
@Entity
@Table(name = "configurazione_scheduler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurazioneScheduler {

    /**
     * Identificatore univoco del task scheduler.
     * Viene utilizzato anche come nome del task.
     */
    @Id
    @Column(name = "id_task")
    private String nomeTask;

    /**
     * Intervallo di tempo fisso (in millisecondi) tra le esecuzioni del task.
     * Determina la frequenza con cui il task viene eseguito.
     */
    @Column(name = "fixed_rate")
    private long fixedRate;

    /**
     * Flag che indica se il task è attualmente in esecuzione.
     * Permette di controllare lo stato di attività del task.
     */
    @Column(name = "in_esecuzione")
    private boolean inEsecuzione = false;
}
