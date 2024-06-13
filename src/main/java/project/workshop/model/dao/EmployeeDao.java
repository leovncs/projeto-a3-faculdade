package project.workshop.model.dao;

import project.workshop.model.entities.Department;
import project.workshop.model.entities.Employee;

import java.util.List;

public interface EmployeeDao {

    void insert(Employee employee);
    void update(Employee employee);
    void deleteById(Integer id);
    Employee findById(Integer id);
    List<Employee> findAll();
    List<Employee> findByDepartment(Department department);
}