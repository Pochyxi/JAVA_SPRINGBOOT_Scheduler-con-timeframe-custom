package com.adi.scheduler.controller;

import com.adi.scheduler.SchedulazioneDinamicaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Controller REST per la gestione degli scheduler dinamici.
 * Fornisce endpoints per avviare, fermare e aggiornare i task schedulati.
 */
@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private final SchedulazioneDinamicaTask schedulazioneDinamicaTask;

    /**
     * Costruttore per l'iniezione delle dipendenze.
     *
     * @param schedulazioneDinamicaTask Il servizio di schedulazione dinamica dei task.
     */
    @Autowired
    public SchedulerController(SchedulazioneDinamicaTask schedulazioneDinamicaTask) {
        this.schedulazioneDinamicaTask = schedulazioneDinamicaTask;
    }

    /**
     * Aggiorna immediatamente il rate di esecuzione di un task specifico.
     *
     * @param rate Il nuovo rate di esecuzione in millisecondi.
     * @param nomeConfigurazione Il nome del task da aggiornare.
     */
    @RequestMapping("/aggiorna/{nomeConfigurazione}/{rate}")
    public void start(@PathVariable("rate") long rate, @PathVariable("nomeConfigurazione") String nomeConfigurazione) {
        schedulazioneDinamicaTask.aggiornaSchedulerImmediatamente(nomeConfigurazione, rate);
    }

    /**
     * Ferma l'esecuzione di un task specifico o di tutti i task.
     *
     * @param nomeConfigurazione Il nome del task da fermare, o "all" per fermare tutti i task.
     */
    @RequestMapping("/stop/{nomeConfigurazione}")
    public void stop(@PathVariable("nomeConfigurazione") String nomeConfigurazione) {
        if (nomeConfigurazione.equals("all"))
            schedulazioneDinamicaTask.stoppaTuttiITask();
        else
            schedulazioneDinamicaTask.stoppaTask(nomeConfigurazione);
    }

    /**
     * Riavvia un task specifico.
     *
     * @param nomeConfigurazione Il nome del task da riavviare.
     */
    @RequestMapping("/start/{nomeConfigurazione}")
    public void riavvia(@PathVariable("nomeConfigurazione") String nomeConfigurazione) {
        schedulazioneDinamicaTask.riavviaTask(nomeConfigurazione);
    }
}
