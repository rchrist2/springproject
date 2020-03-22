package myproject.services;

import myproject.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public void updateEmployee(String name, String email, String address, String phone, int id){
        employeeRepository.updateEmployee(name, email, address, phone, id);
    }

    @Transactional
    public void updateEmployeeRole(int roleId, String email){
        employeeRepository.updateEmployeeRole(roleId, email);
    }

    @Transactional
    public void deleteEmployee(int empId){
        employeeRepository.deleteEmployee(empId);
    }
}
