package com.gxlpes.pocbatchupload.repository;

import com.gxlpes.pocbatchupload.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Object> findByNumber(String number);
}
