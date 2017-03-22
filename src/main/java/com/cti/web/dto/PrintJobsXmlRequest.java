package com.cti.web.dto;

import com.cti.repository.model.PrintType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "jobs")
public class PrintJobsXmlRequest {

    private List<PrintJob> jobs;

    public List<PrintJob> getJobs() {
        return jobs;
    }

    @XmlElements({@XmlElement(name = "job", type = PrintJob.class)})
    public void setJobs(List<PrintJob> jobs) {
        this.jobs = jobs;
    }

    public static class PrintJob {

        private Integer id;

        private PrintType type;
        private String user;
        private String device;
        private Integer amount;

        public Integer getId() {
            return id;
        }

        @XmlAttribute
        public void setId(Integer id) {
            this.id = id;
        }

        public PrintType getType() {
            return type;
        }

        @XmlElement
        public void setType(PrintType type) {
            this.type = type;
        }

        public String getUser() {
            return user;
        }

        @XmlElement
        public void setUser(String user) {
            this.user = user;
        }

        public String getDevice() {
            return device;
        }

        @XmlElement
        public void setDevice(String device) {
            this.device = device;
        }

        public Integer getAmount() {
            return amount;
        }

        @XmlElement
        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }


}
