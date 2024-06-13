package project.workshop.model.services;

import project.workshop.model.dao.DaoFactory;
import project.workshop.model.dao.EmployeeDao;
import project.workshop.model.entities.Employee;

import java.util.List;

public class EmployeeService {

    private EmployeeDao dao = DaoFactory.createEmployeeDao();

    public List<Employee> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Employee employee) {
        if (employee.getId() == null) {
            dao.insert(employee);
        } else {
            dao.update(employee);
        }
    }

    public void delete(Employee employee) {
        dao.deleteById(employee.getId());
    }
}
