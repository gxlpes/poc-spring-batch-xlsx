package com.gxlpes.pocbatchupload.batch.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {

        String jobId = jobExecution.getJobParameters().getString("jobId");
        String excelFilePath = jobExecution.getJobParameters().getString("excelPath");

        LocalDateTime start = jobExecution.getCreateTime();
        LocalDateTime end = jobExecution.getEndTime();

        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

            LOGGER.info("==========JOB FINISHED=======");
            LOGGER.info("JobId      : {}",jobId);
            LOGGER.info("Start Date: {}", start);
            LOGGER.info("End Date: {}", end);
            LOGGER.info("==============================");
        }

    }

}