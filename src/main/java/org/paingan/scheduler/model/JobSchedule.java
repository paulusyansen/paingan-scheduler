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
	
	@Column(name="job_name")
	private String jobName;
	
	@Column(name="job_group")
	private String jobGroup;
	
	@Column(name="job_template")
	private String jobTemplate;
	
	@Column(name="cron_exp")
	private String cronExpression;
	
	@Column(name="cron_human_exp")
	private String cronHumanExpression;
	
	@Column(name = "job_note")
	private String jobNote;
	
	@Column(name="job_active")
	private boolean jobActive;
	
	@Column(name="job_state")
	private boolean jobState;


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

	@Override
	public String toString() {
		return "JobSchedule [id=" + id + ", jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobTemplate="
				+ jobTemplate + ", cronExpression=" + cronExpression + ", cronHumanExpression=" + cronHumanExpression
				+ ", jobNote=" + jobNote + ", jobActive=" + jobActive + ", jobState=" + jobState + "]";
	}

	
}
