package com.gxlpes.pocbatchupload.batch.mappers;

import com.gxlpes.pocbatchupload.domain.Employee;
import com.gxlpes.pocbatchupload.support.poi.RowMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class EmployeeItemRowMapper implements RowMapper<Employee> {

    @Override
    public Employee transformerRow(Row row) {
        Employee employee = new Employee();

        employee.setFirstName(getStringValue(row.getCell(0)));
        employee.setLastName(getStringValue(row.getCell(1)));
        employee.setNumber(getStringValue(row.getCell(2)));
        employee.setEmail(getStringValue(row.getCell(3)));
        employee.setDepartment(getStringValue(row.getCell(4)));
        employee.setSalary(getNumericValue(row.getCell(5)));

        return employee;
    }

    // Utility method to handle different cell types for string values
    private String getStringValue(Cell cell) {
        return cell != null ? cell.toString() : null;
    }

    // Utility method to handle different cell types for numeric values
    private Double getNumericValue(Cell cell) {
        return cell != null ? Double.parseDouble(cell.toString()) : null;
    }
}
