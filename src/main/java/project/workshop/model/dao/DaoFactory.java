package project.workshop.model.dao;

import project.workshop.db.DB;
import project.workshop.model.dao.impl.DepartmentDaoJDBC;
import project.workshop.model.dao.impl.EmployeeDaoJDBC;

public class DaoFactory {

    public static EmployeeDao createEmployeeDao() {
        return new EmployeeDaoJDBC(DB.getConnection());
    }

    public static DepartmentDao createDepartmentDao() {
        return new DepartmentDaoJDBC(DB.getConnection());
    }
}