package org.paingan.scheduler.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.paingan.scheduler.model.JobSchedule;
import org.paingan.scheduler.service.JobScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobListResource {

	private final Logger log = LoggerFactory.getLogger(JobListResource.class);

	@Autowired
	private JobScheduleService jobScheduleService;

	private static int currentPage = 1;
	private static int pageSize = 5;

	@RequestMapping(value = { "/listJobs" })
	public String listBooks(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		page.ifPresent(p -> currentPage = p);
		size.ifPresent(s -> pageSize = s);

		// Page<JobSchedule> page = jobScheduleService.findAll( pageable );

		Page<JobSchedule> jobsPage = jobScheduleService.findAll(PageRequest.of(currentPage - 1, pageSize));

		model.addAttribute("jobsPage", jobsPage);

		int totalPages = jobsPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "listJobs.html";
	}

}
