package com.cti.repository;

import com.cti.repository.model.PrintJob;
import com.cti.repository.model.PrintType;

import java.util.Date;
import java.util.List;

public interface PrintJobsRepository  {

    void save(List<PrintJob> jobs);

    List<PrintJob> find(String user, PrintType type, String device, Date timeFrom, Date timeTo);
}
