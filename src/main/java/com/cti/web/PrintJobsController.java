package com.cti.web;

import com.cti.repository.PrintJobsRepository;
import com.cti.repository.model.PrintJob;
import com.cti.repository.model.PrintType;
import com.cti.web.dto.PrintJobJsonResponse;
import com.cti.web.dto.PrintJobsXmlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class PrintJobsController {


    @Autowired
    private PrintJobsRepository printJobsRepository;

    @RequestMapping(value = "/jobs", method = POST, consumes = "application/xml", produces = "application/json")
    public Map<String, Integer> jobs(@RequestBody PrintJobsXmlRequest printJobs) {

        Map<String, Integer> amountByUser = new HashMap<>();
        Date time = new Date();

        List<PrintJob> jobs = printJobs.getJobs().stream().map(dto -> {
            PrintJob printJob = new PrintJob();
            printJob.setJobId(dto.getId());
            printJob.setAmount(dto.getAmount());
            printJob.setDevice(dto.getDevice());
            printJob.setUser(dto.getUser());
            printJob.setType(dto.getType());
            printJob.setTime(time);
            amountByUser.computeIfPresent(dto.getUser(), (k, v) -> v += dto.getAmount());
            amountByUser.computeIfAbsent(dto.getUser(), k -> dto.getAmount());

            return printJob;
        }).collect(Collectors.toList());
        printJobsRepository.save(jobs);
        return amountByUser;
    }

    @RequestMapping(value = "/statistics", method = GET, produces = "application/json")
    public List<PrintJobJsonResponse> statistics(
            String user,
            PrintType type,
            String device,
            Date timeFrom,
            Date timeTo
    ) {
        List<PrintJob> res = printJobsRepository.find(user, type, device, timeFrom, timeTo);

        return res.stream().map(job -> {
            PrintJobJsonResponse dto = new PrintJobJsonResponse();
            dto.setTime(job.getTime());
            dto.setType(job.getType());
            dto.setUser(job.getUser());
            dto.setDevice(job.getDevice());
            dto.setJobId(job.getJobId());
            dto.setAmount(job.getAmount());
            return dto;
        }).collect(Collectors.toList());

    }


}
