package org.paingan.scheduler.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.paingan.scheduler.model.JobSchedule;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface JobScheduleService {
	
	JobSchedule save(JobSchedule jobSchedule);

	Optional<JobSchedule> findOne(Long id);
	
	Page<JobSchedule> findAll(Pageable pageable);
	
	List<Map<String, Object>> getAllJobs();

	void deleteJob(Long id);

	void stopAllJob();

	void resumeAllJob();

	void pauseJob(String jobName, String groupName);

	void resumeJob(String jobName, String groupName);
	
	boolean getJobStatus( JobSchedule jobSchedule ) throws SchedulerException;
	
	String getJobState(String jobName, String jobGroup);
	
	boolean isJobRunning(String jobName, String jobGroup);
}
