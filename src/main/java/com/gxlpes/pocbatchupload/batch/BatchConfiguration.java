package com.gxlpes.pocbatchupload.batch;

import com.gxlpes.pocbatchupload.batch.listeners.JobCompletionListener;
import com.gxlpes.pocbatchupload.batch.processors.EmployeeItemProcessor;
import com.gxlpes.pocbatchupload.batch.validators.EmployeeJobParametersValidator;
import com.gxlpes.pocbatchupload.domain.Employee;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class BatchConfiguration {

    private final EntityManagerFactory emf;

    public BatchConfiguration(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Bean
    public ItemReader<Employee> itemReader() {
        return new EmployeeItemReader();
    }


    @Bean
    public JobParametersValidator jobParametersValidator() {
        return new EmployeeJobParametersValidator();
    }

    @Bean
    public JobParametersValidator compositeJobParametersValidator() {
        CompositeJobParametersValidator bean = new CompositeJobParametersValidator();
        bean.setValidators(Collections.singletonList(jobParametersValidator()));
        return bean;
    }

    @Bean
    public ItemProcessor<Employee, Employee> itemProcessor() {
        return new EmployeeItemProcessor();
    }

    @Bean(name = "myJobLauncher")
    public JobLauncher simpleJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean(name = "itemWriter")
    public JpaItemWriter<Employee> writer(DataSource dataSource) {
        JpaItemWriter<Employee> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public Step employeeStep(ItemWriter<Employee> itemWriter, JobRepository jobRepository, JpaTransactionManager transactionManager, TaskExecutor taskExecutor) {
        return new StepBuilder("employeeStep", jobRepository).<Employee, Employee>chunk(50, transactionManager).reader(itemReader()).processor(itemProcessor()).writer(itemWriter).taskExecutor(taskExecutor) // Set the task executor
                .build();
    }

    @Bean
    public Job employeeJob(JobCompletionListener listener, Step employeeStep, JobRepository jobRepository) {
        return new JobBuilder("employeeJob", jobRepository).incrementer(new RunIdIncrementer()).listener(listener).flow(employeeStep).end().validator(compositeJobParametersValidator()).build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10); // core pool size as desired
        taskExecutor.setMaxPoolSize(20); // the maximum pool size as desired
        taskExecutor.setQueueCapacity(30); // the queue capacity as desired
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}