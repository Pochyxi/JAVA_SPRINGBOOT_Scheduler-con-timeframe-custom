package com.adi.scheduler.service.impl;

import com.adi.scheduler.service.SchedulerMethodExcecutor;
import org.springframework.stereotype.Service;

/**
 * Implementazione del SchedulerMethodExcecutor.
 * Questa classe è responsabile dell'esecuzione effettiva dei metodi associati ai task schedulati.
 */
@Service
public class SchedulerMethodExcecutorImpl implements SchedulerMethodExcecutor {

    /**
     * Esegue il metodo associato a un task specifico.
     *
     * @param nomeTask Il nome del task da eseguire.
     */
    @Override
    public void executeMethod(String nomeTask) {
        switch (nomeTask) {
            case "monitoraggio":
                eseguiTaskMonitoraggio();
                break;
            case "test":
                eseguiTaskTest();
                break;
            default:
                gestisciTaskSconosciuto(nomeTask);
        }
    }

    /**
     * Esegue il task di monitoraggio.
     */
    private void eseguiTaskMonitoraggio() {
        System.out.println("=========Eseguito task monitoraggio=========");
        // Qui andrebbe inserita la logica effettiva del task di monitoraggio
    }

    /**
     * Esegue il task di test.
     */
    private void eseguiTaskTest() {
        System.out.println("=========Eseguito task test=========");
        // Qui andrebbe inserita la logica effettiva del task di test
    }

    /**
     * Gestisce il caso di un task sconosciuto.
     *
     * @param nomeTask Il nome del task sconosciuto.
     */
    private void gestisciTaskSconosciuto(String nomeTask) {
        System.out.println("!!!!!!Metodo per il task '" + nomeTask + "' non trovato!!!!!");
        // Qui si potrebbe aggiungere un log più dettagliato o una gestione degli errori
    }
}
