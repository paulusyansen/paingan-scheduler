package org.paingan.scheduler.repository;

import java.util.List;

import org.paingan.scheduler.model.JobSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobScheduleRepository extends JpaRepository<JobSchedule, Long> {
	List<JobSchedule> findAll();
}
