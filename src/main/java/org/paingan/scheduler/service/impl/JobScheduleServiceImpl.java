package org.paingan.scheduler.service.impl;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.paingan.scheduler.job.DefaultJob;
import org.paingan.scheduler.model.JobSchedule;
import org.paingan.scheduler.repository.JobScheduleRepository;
import org.paingan.scheduler.service.JobScheduleService;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;


@Service
@Transactional
public class JobScheduleServiceImpl implements JobScheduleService{
	private final Logger log = LoggerFactory.getLogger(JobScheduleServiceImpl.class);
	
	@Autowired
	private JobScheduleRepository jobScheduleRepository;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	
//	@Resource
//	private QuartzUtil quartzUtil;

	@Override
	public JobSchedule save(JobSchedule jobSchedule) {
		log.debug("Request to save JobSchedule : {}", jobSchedule.toString());
		
		boolean isExist = jobSchedule.getId() != null;
		
		JobSchedule result = jobScheduleRepository.saveAndFlush(jobSchedule);
		
		if(isExist && result != null)  {
			rescheduleCronJob(jobSchedule);
		} else {
			try {
				createScheduler(jobSchedule);
			} catch (SchedulerException e) {
				log.error("Request to save JobSchedule ERROR: {}",e.getMessage());
			}
		}
		
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<JobSchedule> findOne(Long id) {
		log.debug("Request to get JobSchedule : {}", id);
		return jobScheduleRepository.findById(id);
	}
	
	public Page<JobSchedule> getListJobSchedule(Pageable pageable) {
		return jobScheduleRepository.findAll( pageable );
	}
	

    private List<JobSchedule> buildJobsSchedule() {
    	
    	List<JobSchedule> jobsList = jobScheduleRepository.findAll();

    	for (JobSchedule jobSchedule : jobsList) {
			
    		jobSchedule.setJobStatus(this.getJobState(jobSchedule.getJobName(), jobSchedule.getJobGroup()));
    		
    		CronDefinition cronDefinition =	CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    		CronParser parser = new CronParser(cronDefinition);
    		CronDescriptor descriptor = CronDescriptor.instance(Locale.getDefault());
    		
    		boolean isValid = CronExpression.isValidExpression(jobSchedule.getCronExpression());
    		jobSchedule.setCronHumanExpression( isValid ? descriptor.describe( parser.parse( jobSchedule.getCronExpression() ) ) : "INVALID");
		}

        return jobsList;
    }
	
	@Override
	@Transactional(readOnly = true)
	public Page<JobSchedule> findAll(Pageable pageable) {
		log.debug("Request to get all JobSchedules");
		
		int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
		
        List<JobSchedule> jobList = buildJobsSchedule();
		
        List<JobSchedule> list;
        
        if (jobList.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, jobList.size() );
            list = jobList.subList(startItem, toIndex);
        }
        
        Page<JobSchedule> jobPage
        = new PageImpl<JobSchedule>(list, PageRequest.of(currentPage, pageSize), jobList.size());
        
        return jobPage;
	}

	@Override
	public void deleteJob(Long id) {
		log.debug("Request to delete JobSchedule : {}", id);
        Optional<JobSchedule> jobSchedule = findOne(id);
        
        if(jobSchedule != null) {
        	try {
				deleteJob(jobSchedule.get());
				
				jobScheduleRepository.deleteById(id);
			} catch (SchedulerException e) {
				log.error("Request to delete JobSchedule ERROR: {}", e.getMessage());
			}
        }
        
        
	}

//	@Override
//	public void stopAllJob() {
//		quartzUtil.stopAllJob();
//	}
//
//	@Override
//	public void resumeAllJob() {
//		quartzUtil.resumeAllJob();
//	}
//
//	@Override
//	public void pauseJob(String jobName, String groupName) {
//		quartzUtil.pauseJob(jobName, groupName);
//	}
//
//	@Override
//	public void resumeJob(String jobName, String groupName) {
//		quartzUtil.resumeJob(jobName, groupName);
//	}

	public void stopAllJob() {
    	try {
    		schedulerFactoryBean.getScheduler().pauseAll();
		} catch (SchedulerException e) {
			log.error("Exception while stopping all job {}", e);
		}
    }

	public void resumeAllJob() {
		try {
			schedulerFactoryBean.getScheduler().resumeAll();
		} catch (SchedulerException e) {
			log.error("Exception while resuming all job {}", e);
		}
	}
	
	private void createScheduler(JobSchedule jobSchedule) throws SchedulerException {
		JobDetail jobDetail = newJob( DefaultJob.class )
				.storeDurably(true)
			    .withIdentity( jobSchedule.getJobName(), jobSchedule.getJobGroup() )
			    .usingJobData("jobClass", jobSchedule.getJobTemplate())
			    .build();
		
		Trigger trigger = org.quartz.TriggerBuilder.newTrigger()
				.withIdentity( jobSchedule.getJobName()+"" , jobSchedule.getJobGroup()+"" )
				.withSchedule( cronSchedule( jobSchedule.getCronExpression() ).withMisfireHandlingInstructionFireAndProceed() )
				.startNow()	
				.build();
			
		TriggerKey existTg = org.quartz.TriggerKey.triggerKey( jobSchedule.getJobName()+"", jobSchedule.getJobGroup()+"");
		
		if( schedulerFactoryBean.getScheduler().checkExists( existTg ) ) {
			schedulerFactoryBean.getScheduler().unscheduleJob( existTg );
			schedulerFactoryBean.getScheduler().deleteJob( org.quartz.JobKey.jobKey( jobSchedule.getJobName(), jobSchedule.getJobGroup() ) );
		}

		schedulerFactoryBean.getScheduler().scheduleJob( jobDetail, trigger );
		//scheduler.scheduleJob( job, trigger );
		
	}
	
	
	private boolean rescheduleCronJob(JobSchedule jobSchedule) {
    	try {
    		Trigger trigger = newTrigger()
    		    	.withIdentity(jobSchedule.getJobName()+"", jobSchedule.getJobGroup()+"")
    		    	.startNow()
    		    	.withSchedule(cronSchedule(jobSchedule.getCronExpression()).withMisfireHandlingInstructionFireAndProceed())
    				.build();
    		Date rescheduleDate = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobSchedule.getJobName()+"", jobSchedule.getJobGroup()+""), trigger);
    		log.info("Reschedule job "+jobSchedule.getJobName()+" to: "+rescheduleDate);
    		return true;
		} catch (Exception e) {
			log.error("Exception while reschedule cron job {} from group {} error {}", jobSchedule.getJobName(), jobSchedule.getJobGroup(), e);
			return false;
		}
    }
	
	private void deleteJob( JobSchedule jobSchedule ) throws SchedulerException {
		log.debug("QuartzUtil deleteJob-{}-{}",jobSchedule.getJobName(), jobSchedule.getJobGroup());
		TriggerKey tg = org.quartz.TriggerKey.triggerKey( jobSchedule.getJobName()+"" , jobSchedule.getJobGroup()+"");
		JobKey key = org.quartz.JobKey.jobKey( jobSchedule.getJobName(), jobSchedule.getJobGroup() );
		
		if( schedulerFactoryBean.getScheduler().checkExists( tg ) ) {
			schedulerFactoryBean.getScheduler().unscheduleJob( tg );
			schedulerFactoryBean.getScheduler().deleteJob( key );
		}
		
	}
	
	public void pauseJob(String jobName, String jobGroup) {
    	try {
    		schedulerFactoryBean.getScheduler().pauseJob(JobKey.jobKey(jobName, jobGroup));
		} catch (Exception e) {
			log.error("Exception while pausing job {} from group {} error", jobName, jobGroup, e);
		}
    }
	
	public void resumeJob(String jobName, String jobGroup) {
		log.debug("QuartzUtil resumeJob-{}-{}",jobName, jobGroup);
    	try {
    		schedulerFactoryBean.getScheduler().resumeJob(JobKey.jobKey(jobName, jobGroup));
		} catch (Exception e) {
			log.error("Exception while resuming job {} from group {} error", jobName, jobGroup, e);
		}
    }
	
	
	
	
	
	public boolean getJobStatus( JobSchedule jobSchedule ) throws SchedulerException {
		
		TriggerKey tg = org.quartz.TriggerKey.triggerKey( jobSchedule.getJobName() + "" , jobSchedule.getJobGroup() + "");
		
		return schedulerFactoryBean.getScheduler().checkExists( tg );
	}
	
	public String getJobState(String jobName, String jobGroup) {
		log.debug("JobServiceImpl.getJobState()");
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);
			//Scheduler scheduler = scheduler;
			
			
			JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
					
			if(jobDetail !=null) {
			List<? extends Trigger> triggers = schedulerFactoryBean.getScheduler().getTriggersOfJob(jobDetail.getKey());
			if(triggers != null && triggers.size() > 0){
				for (Trigger trigger : triggers) {
					TriggerState triggerState = schedulerFactoryBean.getScheduler().getTriggerState(trigger.getKey());
					if (TriggerState.PAUSED.equals(triggerState)) {
						return "PAUSED";
					}else if (TriggerState.BLOCKED.equals(triggerState)) {
						return "BLOCKED";
					}else if (TriggerState.COMPLETE.equals(triggerState)) {
						return "COMPLETE";
					}else if (TriggerState.ERROR.equals(triggerState)) {
						return "ERROR";
					}else if (TriggerState.NONE.equals(triggerState)) {
						return "NONE";
					}else if (TriggerState.NORMAL.equals(triggerState)) {
						return "SCHEDULED";
					}
				}
			}
			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while checking job with name and group exist: {}", e);
		}
		return null;
	}
	
	public boolean isJobRunning(String jobName, String jobGroup) {
		log.debug("Request received to check if job is running");
		log.debug("Parameters received for checking job is running now : jobName : {} jobGroup : {}", jobName, jobGroup);
		try {
			List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
			if(currentJobs!=null){
				for (JobExecutionContext jobCtx : currentJobs) {
					String jobNameDB = jobCtx.getJobDetail().getKey().getName();
					String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
					if (jobName.equalsIgnoreCase(jobNameDB) && jobGroup.equalsIgnoreCase(groupNameDB) && !jobCtx.getFireTime().equals(jobCtx.getFireTime())) {
						return true;
					}
				}
			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while checking job with key: {} {} is running. error {}", jobName, jobGroup, e);
			return false;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllJobs() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();
					DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					DateTime scheduleTime = triggers.get(0).getStartTime() != null?new DateTime(triggers.get(0).getStartTime()):null;
					DateTime nextFireTime = triggers.get(0).getNextFireTime() != null?new DateTime(triggers.get(0).getNextFireTime()):null;
					DateTime lastFiredTime = triggers.get(0).getPreviousFireTime() != null?new DateTime(triggers.get(0).getPreviousFireTime()):null;	
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("jobName", jobName);
					map.put("groupName", jobGroup);
					map.put("scheduleTime", dtf.print(scheduleTime));
					map.put("lastFiredTime", dtf.print(lastFiredTime));
					map.put("nextFireTime", dtf.print(nextFireTime));
					if(isJobRunning(jobName, jobGroup)){
						map.put("jobStatus", "RUNNING");
					}else{
						String jobState = getJobState(jobName, jobGroup);
						map.put("jobStatus", jobState);
					}
					list.add(map);
					log.debug("Job details:");
					log.debug("Job Name:"+jobName + ", Group Name:"+ groupName + ", Schedule Time:"+scheduleTime);
				}
			}
		} catch (SchedulerException e) {
			log.error("SchedulerException while fetching all jobs. error message. error {}", e);
		}
		return list;
	}
}
