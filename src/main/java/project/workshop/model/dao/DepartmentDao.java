package project.workshop.model.dao;

import project.workshop.model.entities.Department;
import project.workshop.model.entities.Employee;

import java.util.List;

public interface DepartmentDao {

    void insert(Department department);
    void update(Department department);
    void deleteById(Integer id);
    Department findById(Integer id);
    List<Department> findAll();
}