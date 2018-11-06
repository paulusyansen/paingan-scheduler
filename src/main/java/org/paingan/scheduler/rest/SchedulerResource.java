package org.paingan.scheduler.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.paingan.scheduler.model.JobSchedule;
import org.paingan.scheduler.service.JobScheduleService;
import org.paingan.scheduler.util.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;


@RestController	
@RequestMapping(value="/api")
public class SchedulerResource {
	
	private final Logger log = LoggerFactory.getLogger(SchedulerResource.class);
	
	@Autowired
	private JobScheduleService jobScheduleService;
	
	
	@PostMapping( path="/create", 
				  consumes = { MediaType.APPLICATION_JSON_VALUE },
				  produces = MediaType.APPLICATION_JSON_VALUE)	
	public ResponseBody create(@RequestBody JobSchedule scheduler) {
		JobSchedule saved = this.jobScheduleService.save( scheduler );
		
		return ResponseBody.success("scheduler is created and already start", saved);
		
	}
	
	@GetMapping( path="/findone/{id}")	
	public ResponseBody findOne(@PathVariable("id") Long id) {
		Optional<JobSchedule> result= jobScheduleService.findOne(id);
		return ResponseBody.success("data found", result.get() );
		
	}
	
	@GetMapping( path="/start/{id}")	
	public ResponseBody start(@PathVariable("id") Long id) {
		
		Optional<JobSchedule> option= jobScheduleService.findOne(id);
		
		if(option.get() != null) {
			JobSchedule jobSchedule = option.get();
			
			jobScheduleService.resumeJob(jobSchedule.getJobName(), jobSchedule.getJobGroup());
			
			return ResponseBody.success("scheduler is started");
		} else {
			return ResponseBody.success( String.format("Cannot find scheduler with id : %d", id) );
		}
	}
	
	@GetMapping( path="/stop/{id}")	
	public ResponseBody stop(@PathVariable("id") Long id) {
		
		Optional<JobSchedule> option= jobScheduleService.findOne(id);
		
		if(option.get() != null) {
			JobSchedule jobSchedule = option.get();
			jobScheduleService.pauseJob(jobSchedule.getJobName(), jobSchedule.getJobGroup());
			
			return ResponseBody.success("scheduler is stopped");
		} else {
			return ResponseBody.success( String.format("Cannot find scheduler with id : %d", id) );
		}
	}
	
	@GetMapping( path="/delete/{id}")	
	public ResponseBody delete(@PathVariable("id") Long id) {
		Optional<JobSchedule> option= jobScheduleService.findOne(id);
		
		if(option.get() != null) {
			JobSchedule jobSchedule = option.get();
			jobScheduleService.deleteJob(jobSchedule.getId());
			
			return ResponseBody.success("scheduler is deleted");
		} else {
			return ResponseBody.success( String.format("Cannot find scheduler with id : %d", id) );
		}
	}
	
	@GetMapping("/getAllJobStatus")

    public ResponseBody getAllJobStatus() {
        log.debug("REST request to stop all job");
        List<Map<String, Object>> list = jobScheduleService.getAllJobs();
        return ResponseBody.success("data found", list);
    }
	
	
	
	@GetMapping( path="/list")	
	public ResponseBody list(Pageable pageable) {
		
		Page<JobSchedule> page = jobScheduleService.findAll( pageable );
		
		if( CollectionUtils.isEmpty( page.getContent() ) ) 
			return ResponseBody.success("no data found", new HashMap<String, Object>());
		
		//get a predefined instance
		CronDefinition cronDefinition =	
		CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
	
		//create a parser based on provided definition
		CronParser parser = new CronParser(cronDefinition);
	
		//create a descriptor for a specific Locale
		CronDescriptor descriptor = CronDescriptor.instance(Locale.getDefault());
		
		page.getContent()
			.stream()
			.forEach( s ->  { 
					s.setCronHumanExpression( descriptor.describe( parser.parse( s.getCronExpression() ) ) );
				try {
					s.setJobActive( !"PAUSED".equals(jobScheduleService.getJobState(s.getJobName(), s.getJobGroup())) );
					
					log.debug("jobActive {}", s.isJobActive());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", page.getTotalPages());
		resp.put("rows", page.getContent() );
	
		return ResponseBody.success("data found", page);
		
	}
	


}
