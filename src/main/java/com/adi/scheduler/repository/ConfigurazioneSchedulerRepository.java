package com.adi.scheduler.repository;

import com.adi.scheduler.entity.ConfigurazioneScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurazioneSchedulerRepository extends JpaRepository<ConfigurazioneScheduler, String> {
}
