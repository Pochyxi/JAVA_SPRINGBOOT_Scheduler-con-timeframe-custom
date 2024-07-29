package com.adi.scheduler.service;

/**
 * Interfaccia per l'esecuzione di metodi schedulati.
 * Questa interfaccia definisce un contratto per l'esecuzione di metodi associati a task schedulati.
 */
public interface SchedulerMethodExcecutor {

    /**
     * Esegue il metodo associato a un task specifico.
     *
     * @param nomeTask Il nome del task per il quale eseguire il metodo associato.
     *                 Questo nome dovrebbe corrispondere a un task configurato nel sistema di schedulazione.
     */
    void executeMethod(String nomeTask);
}
