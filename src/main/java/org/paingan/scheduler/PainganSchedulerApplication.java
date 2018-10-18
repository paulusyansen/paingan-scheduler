package org.paingan.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAutoConfiguration
public class PainganSchedulerApplication {
	
//	@Value("classpath:dbscript/insert.sql")
//	private Resource dataScript;
//	
//	@javax.annotation.Resource
//	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(PainganSchedulerApplication.class, args);
	}
	
//	@Bean
//	public Scheduler getScheduler() throws SchedulerException {
//		
//		SchedulerFactory sf = new StdSchedulerFactory();
//		Scheduler sched = sf.getScheduler();
//		sched.start();
//		
//		return sched;
//		
//	}
//	
//	@EventListener({ContextRefreshedEvent.class})
//    void contextRefreshedEvent() {
//		
//		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
////		populator.addScript( dataScript );
//		populator.setContinueOnError(false);
//		
//		DatabasePopulatorUtils.execute( populator, dataSource );
//		
//    }
	
}
