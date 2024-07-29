package com.adi.scheduler;

import com.adi.scheduler.entity.ConfigurazioneScheduler;
import com.adi.scheduler.service.ConfiguratoreSchedulerService;
import com.adi.scheduler.service.SchedulerMethodExcecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Gestore di schedulazione dinamica dei task.
 * Permette di configurare, avviare, fermare e modificare task schedulati a runtime.
 */
@Component
public class SchedulazioneDinamicaTask implements SchedulingConfigurer {

    private static final long DEFAULT_RATE = 60000; // Rate di default in millisecondi (1 minuto)
    private final ConfiguratoreSchedulerService configuratoreSchedulerService;
    private final SchedulerMethodExcecutor schedulerMethodExcecutor;
    private final ConcurrentHashMap<String, TaskInfo> taskMap = new ConcurrentHashMap<>();
    private final AtomicReference<ScheduledTaskRegistrar> registrarRef = new AtomicReference<>();

    /**
     * Costruttore per l'iniezione delle dipendenze.
     */
    @Autowired
    public SchedulazioneDinamicaTask(final ConfiguratoreSchedulerService configuratoreSchedulerService,
                                     final SchedulerMethodExcecutor schedulerMethodExcecutor) {
        this.configuratoreSchedulerService = configuratoreSchedulerService;
        this.schedulerMethodExcecutor = schedulerMethodExcecutor;
    }

    /**
     * Configura il ScheduledTaskRegistrar per la gestione dei task.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        registrarRef.set(taskRegistrar);
        // Qui puoi inizializzare task di default se necessario
    }

    /**
     * Schedula un task specifico.
     * @param nomeConfigurazione Nome del task da schedulare
     */
    public void schedulaTask(String nomeConfigurazione) {
        TaskInfo taskInfo = taskMap.computeIfAbsent(nomeConfigurazione, k -> new TaskInfo());

        if (!taskInfo.isRunning.get()) {
            System.out.println("Il task '" + nomeConfigurazione + "' è stato fermato. Usa /start/nome_task per riavviarlo.");
            return;
        }

        long fixedRate = estraiRateo(nomeConfigurazione);

        if (fixedRate == -1L) {
            System.out.println("Il task '" + nomeConfigurazione + "' non è stato trovato.");
            return;
        }

        taskInfo.currentRateRef.set(fixedRate);

        // Schedula il task con il rate specificato
        ScheduledFuture<?> future = Objects.requireNonNull(registrarRef.get().getScheduler()).schedule(
                () -> eseguiTask(nomeConfigurazione),
                new PeriodicTrigger(Duration.ofMillis(fixedRate))
        );

        // Cancella il vecchio future se esistente e imposta il nuovo
        Optional.ofNullable(taskInfo.futureRef.getAndSet(future))
                .ifPresent(oldFuture -> oldFuture.cancel(false));
    }

    /**
     * Esegue un task specifico e gestisce eventuali aggiornamenti del rate.
     * @param nomeConfigurazione Nome del task da eseguire
     */
    private void eseguiTask(String nomeConfigurazione) {
        TaskInfo taskInfo = taskMap.get(nomeConfigurazione);
        if (taskInfo == null || !taskInfo.isRunning.get()) {
            return;
        }

        System.out.println("Task '" + nomeConfigurazione + "' eseguito: " + System.currentTimeMillis());

        // Esegue il metodo associato al task
        schedulerMethodExcecutor.executeMethod(nomeConfigurazione);

        long rateoConfigurato = estraiRateo(nomeConfigurazione);

        // Aggiorna la configurazione nel database
        ConfigurazioneScheduler configurazioneScheduler =
                configuratoreSchedulerService.getConfigurazioneScheduler(nomeConfigurazione).orElse(null);

        if (configurazioneScheduler != null) {
            configurazioneScheduler.setFixedRate(rateoConfigurato);
            configurazioneScheduler.setInEsecuzione(true);
            configuratoreSchedulerService.salva(configurazioneScheduler);
        }

        if (rateoConfigurato == -1L) {
            System.out.println("Il task '" + nomeConfigurazione + "' non è stato trovato.");
            return;
        }

        long rateoAttuale = taskInfo.currentRateRef.get();

        System.out.println("Task '" + nomeConfigurazione + "' - Rate configurato: " + rateoConfigurato + ", Rate corrente: " + rateoAttuale);

        // Se il rate è cambiato significativamente, rischedula il task
        if (Math.abs(rateoConfigurato - rateoAttuale) > (rateoAttuale * 0.1)) {
            System.out.println("Rischedulando il task '" + nomeConfigurazione + "' a causa di un cambiamento significativo del rate");
            aggiornaSchedulerImmediatamente(nomeConfigurazione, rateoConfigurato);
        }
    }

    /**
     * Aggiorna immediatamente lo scheduler di un task con un nuovo rate.
     * @param nomeConfigurazione Nome del task da aggiornare
     * @param rate Nuovo rate per il task
     */
    public void aggiornaSchedulerImmediatamente(String nomeConfigurazione, long rate) {
        TaskInfo taskInfo = taskMap.computeIfAbsent(nomeConfigurazione, k -> new TaskInfo());
        if (!taskInfo.isRunning.get()) {
            System.out.println("Il task '" + nomeConfigurazione + "' è stato fermato. Usa /start/nome_task per " +
                    "riavviarlo.");
            return;
        }

        // Aggiorna la configurazione nel database
        ConfigurazioneScheduler configurazioneScheduler =
                configuratoreSchedulerService.getConfigurazioneScheduler(nomeConfigurazione).orElse(null);

        if (configurazioneScheduler != null) {
            configurazioneScheduler.setFixedRate(rate);
            configurazioneScheduler.setInEsecuzione(true);
            configuratoreSchedulerService.salva(configurazioneScheduler);
        }

        long nuovoRate = estraiRateo(nomeConfigurazione);

        if (nuovoRate == -1L) {
            System.out.println("Il task '" + nomeConfigurazione + "' non è stato trovato.");
            return;
        }

        // Ferma lo scheduler corrente
        Optional.ofNullable(taskInfo.futureRef.get()).ifPresent(future -> future.cancel(false));

        // Aggiorna il rate corrente
        taskInfo.currentRateRef.set(nuovoRate);

        // Rischedula immediatamente il task con il nuovo rate
        ScheduledFuture<?> nuovoFuture = Objects.requireNonNull(registrarRef.get().getScheduler()).schedule(
                () -> eseguiTask(nomeConfigurazione),
                new PeriodicTrigger(Duration.ofMillis(nuovoRate))
        );

        taskInfo.futureRef.set(nuovoFuture);

        System.out.println("Scheduler '" + nomeConfigurazione + "' aggiornato immediatamente con nuovo rate: " + nuovoRate);
    }

    /**
     * Estrae il rate di esecuzione per un task specifico dalla configurazione.
     * @param nomeConfigurazione Nome del task
     * @return Rate di esecuzione in millisecondi, o -1 se non trovato
     */
    private long estraiRateo(String nomeConfigurazione) {
        Optional<ConfigurazioneScheduler> configuratore = configuratoreSchedulerService.getConfigurazioneScheduler(nomeConfigurazione);
        return configuratore.map(ConfigurazioneScheduler::getFixedRate).orElse(-1L);
    }

    /**
     * Ferma l'esecuzione di un task specifico.
     * @param nomeConfigurazione Nome del task da fermare
     */
    public void stoppaTask(String nomeConfigurazione) {
        ConfigurazioneScheduler configurazioneScheduler = configuratoreSchedulerService.getConfigurazioneScheduler(nomeConfigurazione).orElse(null);

        if (configurazioneScheduler != null) {
            configurazioneScheduler.setInEsecuzione(false);
            configuratoreSchedulerService.salva(configurazioneScheduler);
        }

        TaskInfo taskInfo = taskMap.get(nomeConfigurazione);
        if (taskInfo != null) {
            taskInfo.isRunning.set(false);
            Optional.ofNullable(taskInfo.futureRef.getAndSet(null))
                    .ifPresent(future -> future.cancel(false));
            System.out.println("Task '" + nomeConfigurazione + "' fermato completamente.");
        } else {
            System.out.println("Task '" + nomeConfigurazione + "' non trovato.");
        }
    }

    /**
     * Riavvia un task specifico.
     * @param nomeConfigurazione Nome del task da riavviare
     */
    public void riavviaTask(String nomeConfigurazione) {
        TaskInfo taskInfo = taskMap.computeIfAbsent(nomeConfigurazione, k -> new TaskInfo());
        if (taskInfo.isRunning.compareAndSet(false, true) || taskInfo.futureRef.get() == null) {
            schedulaTask(nomeConfigurazione);
            System.out.println("Task '" + nomeConfigurazione + "' " + (taskInfo.futureRef.get() == null ? "avviato" : "riavviato") + ".");
        } else {
            System.out.println("Il task '" + nomeConfigurazione + "' è già in esecuzione.");
        }
    }

    /**
     * Ferma tutti i task attualmente in esecuzione.
     */
    public void stoppaTuttiITask() {
        taskMap.forEach((nomeConfigurazione, taskInfo) -> {
            stoppaTask(nomeConfigurazione);
        });
    }

    /**
     * Classe interna per gestire le informazioni di un singolo task.
     */
    private static class TaskInfo {
        final AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();
        final AtomicLong currentRateRef = new AtomicLong(DEFAULT_RATE);
        final AtomicBoolean isRunning = new AtomicBoolean(true);
    }
}
