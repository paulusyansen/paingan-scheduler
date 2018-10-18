package org.paingan.scheduler.job;

import javax.annotation.Resource;

import org.paingan.scheduler.model.Member;
import org.paingan.scheduler.service.MemberService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class SampleJob extends BaseJob {
	
	private static Logger log = LoggerFactory.getLogger( SampleJob.class );
	
	@Resource
	private MemberService employeeService;

	@Override
	public void execute(JobExecutionContext context) {
		if( CollectionUtils.isEmpty(this.employeeService.findAll()) ) {
		
			log.debug("Starting Job -------");
			
			Member e = new Member();
			e.setName("Ronaldo");
			
			this.employeeService.save(e);
		}
		
		this.employeeService.findAll().forEach( em-> {
			log.debug( "ID : " + em.getId() + ", Name : " + em.getName());
		});
		
		log.debug("Stopping Job -------");
		
	}

}
