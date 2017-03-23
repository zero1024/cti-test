package com.cti.web;

import com.cti.repository.PrintJobsRepository;
import com.cti.repository.model.PrintJob;
import com.cti.repository.model.PrintType;
import com.cti.web.dto.JobsJsonResponse;
import com.cti.web.dto.JobsXmlRequest;
import com.cti.web.dto.StatisticsJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class PrintJobsController {

    private final PrintJobsRepository printJobsRepository;

    @Autowired
    public PrintJobsController(PrintJobsRepository printJobsRepository) {
        this.printJobsRepository = printJobsRepository;
    }

    @RequestMapping(value = "/jobs", method = POST, consumes = APPLICATION_XML_VALUE, produces = APPLICATION_JSON_VALUE)
    public JobsJsonResponse jobs(@RequestBody @Valid JobsXmlRequest printJobs) {

        JobsJsonResponse res = new JobsJsonResponse();
        Date now = new Date();

        List<PrintJob> jobs = printJobs.getJobs().stream().map(dto -> {
            PrintJob printJob = new PrintJob();
            printJob.setJobId(dto.getId());
            printJob.setAmount(dto.getAmount());
            printJob.setDevice(dto.getDevice());
            printJob.setUser(dto.getUser());
            printJob.setType(dto.getType());
            printJob.setTime(now.getTime());
            res.addAmount(dto.getUser(), dto.getAmount());
            return printJob;
        }).collect(toList());

        printJobsRepository.save(jobs);
        return res;
    }

    @RequestMapping(value = "/statistics", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<StatisticsJsonResponse> statistics(
            String user,
            PrintType type,
            String device,
            @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date timeFrom,
            @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date timeTo) {

        List<PrintJob> res = printJobsRepository.find(user, type, device, timeFrom, timeTo);

        return res.stream().map(job -> {
            StatisticsJsonResponse dto = new StatisticsJsonResponse();
            dto.setTime(job.getTimeAsDate());
            dto.setType(job.getType());
            dto.setUser(job.getUser());
            dto.setDevice(job.getDevice());
            dto.setJobId(job.getJobId());
            dto.setAmount(job.getAmount());
            return dto;
        }).collect(toList());

    }


}
