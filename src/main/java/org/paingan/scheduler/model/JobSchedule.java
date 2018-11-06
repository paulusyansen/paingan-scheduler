package org.paingan.scheduler.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="scheduler")
public class JobSchedule implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="job_name", unique = true, nullable = false)
	private String jobName;
	
	@Column(name="job_group", nullable = false)
	private String jobGroup;
	
	@Column(name="job_template", nullable = false)
	private String jobTemplate;
	
	@Column(name="cron_exp", nullable = false)
	private String cronExpression;
	
	@Column(name="cron_human_exp")
	private String cronHumanExpression;
	
	@Column(name = "job_note")
	private String jobNote;
	
	@Column(name="job_active")
	private boolean jobActive;
	
	@Column(name="job_state")
	private boolean jobState;
	
	@Column(name="job_status")
	private String jobStatus;


	public JobSchedule() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JobSchedule(Long id, String jobName, String jobGroup, String jobTemplate, String cronExpression,
			String cronHumanExpression, String jobNote, boolean jobActive, boolean jobState, String jobStatus) {
		super();
		this.id = id;
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.jobTemplate = jobTemplate;
		this.cronExpression = cronExpression;
		this.cronHumanExpression = cronHumanExpression;
		this.jobNote = jobNote;
		this.jobActive = jobActive;
		this.jobState = jobState;
		this.jobStatus = jobStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobTemplate() {
		return jobTemplate;
	}

	public void setJobTemplate(String jobTemplate) {
		this.jobTemplate = jobTemplate;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getCronHumanExpression() {
		return cronHumanExpression;
	}

	public void setCronHumanExpression(String cronHumanExpression) {
		this.cronHumanExpression = cronHumanExpression;
	}

	public String getJobNote() {
		return jobNote;
	}

	public void setJobNote(String note) {
		this.jobNote = note;
	}


	public boolean isJobActive() {
		return jobActive;
	}

	public void setJobActive(boolean jobActive) {
		this.jobActive = jobActive;
	}
	
	public boolean isJobState() {
		return jobState;
	}

	public void setJobState(boolean jobState) {
		this.jobState = jobState;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@Override
	public String toString() {
		return "JobSchedule [id=" + id + ", jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobTemplate="
				+ jobTemplate + ", cronExpression=" + cronExpression + ", cronHumanExpression=" + cronHumanExpression
				+ ", jobNote=" + jobNote + ", jobActive=" + jobActive + ", jobState=" + jobState + ", jobStatus="
				+ jobStatus + "]";
	}
	
	
}
