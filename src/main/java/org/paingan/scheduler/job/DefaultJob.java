package org.paingan.scheduler.job;

import org.paingan.scheduler.util.SpringUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class DefaultJob implements Job {
	
	private static Logger logger = LoggerFactory.getLogger( DefaultJob.class );
		
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Running DefaultJob");
		String jobClass = context.getJobDetail().getJobDataMap().getString("jobClass");	

		try {	
			
			BaseJob b = (BaseJob) Class.forName( jobClass ).newInstance();
			SpringUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean( b );
			b.execute(context);
			
		}catch(Exception e) {
			logger.error( "Error", e);
		}
		
	}

}
