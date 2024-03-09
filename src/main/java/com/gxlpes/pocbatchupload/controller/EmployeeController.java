package com.gxlpes.pocbatchupload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    @Qualifier("myJobLauncher")
    private JobLauncher jobLauncher;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    private final Job job;

    public EmployeeController(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @PostMapping("/start-job")
    public String startJob(@RequestParam("file") MultipartFile file) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        LOGGER.info("Starting job");

        try {
            // Create a temporary directory if it doesn't exist
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "batch_upload_temp");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // Save the file to the temporary directory
            Path tempFile = Files.createTempFile(tempDir.toPath(), "temp_", file.getOriginalFilename());
            file.transferTo(tempFile);

            System.out.println(tempFile);

            JobParameters params = new JobParametersBuilder()
                    .addString("jobId", String.format("%.3f", System.currentTimeMillis() / 1000.0))
                    .addDate("currentTime", new Date())
                    .addString("excelPath", tempFile.toString())
                    .toJobParameters();

            jobLauncher.run(job, params);

            LOGGER.info("Stopping job");

//            // Optionally, delete the temporary file after the job is completed
//            Files.deleteIfExists(tempFile);

            return "starting the job";
        } catch (Exception e) {
            LOGGER.error("Error starting job", e);
            return "error starting the job";
        }
    }

}
