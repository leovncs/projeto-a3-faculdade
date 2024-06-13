package project.workshop.model.services;

import project.workshop.model.dao.DaoFactory;
import project.workshop.model.dao.DepartmentDao;
import project.workshop.model.dao.EmployeeDao;
import project.workshop.model.entities.Department;
import project.workshop.model.entities.Employee;

import java.util.List;

public class DepartmentService {

    private DepartmentDao dao = DaoFactory.createDepartmentDao();

    public List<Department> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Department department) {
        if (department.getId() == null) {
            dao.insert(department);
        } else {
            dao.update(department);
        }
    }

    public void delete(Department department) {
        dao.deleteById(department.getId());
    }
}
