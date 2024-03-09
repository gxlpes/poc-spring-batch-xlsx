package com.gxlpes.pocbatchupload.batch;

import com.gxlpes.pocbatchupload.batch.mappers.EmployeeItemRowMapper;
import com.gxlpes.pocbatchupload.domain.Employee;
import com.gxlpes.pocbatchupload.support.poi.AbstractExcelPoi;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class EmployeeItemReader extends AbstractExcelPoi<Employee> implements ItemReader<Employee>, StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeItemReader.class);
    private Iterator<String> excelFilePathsIterator;
    private Iterator<Employee> employeeIterator;

    @Override
    public Employee read() {
        if (employeeIterator == null || !employeeIterator.hasNext()) {
            if (excelFilePathsIterator != null && excelFilePathsIterator.hasNext()) {
                String path = excelFilePathsIterator.next();
                List<Employee> employeeList = read(path, new EmployeeItemRowMapper());
                employeeIterator = employeeList.iterator();
            } else {
                return null; // No more data to read
            }
        }

        return employeeIterator.next();
    }

    @Override
    public void write(String filePath, List<Employee> aList) {
        throw new NotImplementedException("No need to implement this method in the context");
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
        String excelPaths = stepExecution.getJobExecution().getJobParameters().getString("excelPaths");
        System.out.println("testing ###### " + excelPaths);
        assert excelPaths != null;
        List<String> excelFilePaths = Arrays.asList(excelPaths.split(","));
        excelFilePathsIterator = excelFilePaths.iterator();
    }



    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
