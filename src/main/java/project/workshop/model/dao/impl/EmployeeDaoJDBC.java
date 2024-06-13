package project.workshop.model.dao.impl;

import project.workshop.db.DB;
import project.workshop.db.DbException;
import project.workshop.model.dao.EmployeeDao;
import project.workshop.model.entities.Department;
import project.workshop.model.entities.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDaoJDBC implements EmployeeDao {

    private Connection conn;

    public EmployeeDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Employee employee) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO employee "
                            + "(Name, Email, BirthDate, BaseSalary,DepartmentId) "
                            + "VALUES "
                            + "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setDate(3, new java.sql.Date(employee.getBirthDate().getTime()));
            stmt.setDouble(4, employee.getBaseSalary());
            stmt.setInt(5, employee.getDepartment().getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    employee.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void update(Employee employee) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "UPDATE employee "
                            + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                            + "WHERE Id = ?");
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setDate(3, new Date(employee.getBirthDate().getTime()));
            stmt.setDouble(4, employee.getBaseSalary());
            stmt.setInt(5, employee.getDepartment().getId());
            stmt.setInt(6, employee.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM employee WHERE Id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public Employee findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT employee.*,department.Name as DepName "
                            + "FROM employee INNER JOIN department "
                            + "ON employee.DepartmentId = department.Id "
                            + "WHERE employee.Id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Department department = instantiateDepartment(rs);
                Employee employee = instantiateEmployee(rs, department);
                return employee;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    private Employee instantiateEmployee(ResultSet rs, Department department) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("Id"));
        employee.setName(rs.getString("Name"));
        employee.setEmail(rs.getString("Email"));
        employee.setBaseSalary(rs.getDouble("BaseSalary"));
        employee.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
        employee.setDepartment(department);
        return employee;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("DepartmentId"));
        department.setName(rs.getString("DepName"));
        return department;
    }

    @Override
    public List<Employee> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT employee.*,department.Name as DepName "
                            + "FROM employee INNER JOIN department "
                            + "ON employee.DepartmentId = department.Id "
                            + "ORDER BY Name");
            rs = ps.executeQuery();
            List<Employee> employees = new ArrayList<>();
            Map<Integer, Department> departments = new HashMap<>();
            while (rs.next()) {
                Department dep = departments.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    departments.put(rs.getInt("DepartmentId"), dep);
                }

                Department department = instantiateDepartment(rs);
                Employee employee = instantiateEmployee(rs, department);
                employees.add(employee);
            }
            return employees;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Employee> findByDepartment(Department department) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT employee.*,department.Name as DepName "
                            + "FROM employee INNER JOIN department "
                            + "ON employee.DepartmentId = department.Id "
                            + "WHERE DepartmentId = ? "
                            + "ORDER BY Name");
            ps.setInt(1, department.getId());
            rs = ps.executeQuery();
            List<Employee> employees = new ArrayList<>();
            Map<Integer, Department> departments = new HashMap<>();
            while (rs.next()) {
                Department dep = departments.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    departments.put(rs.getInt("DepartmentId"), dep);
                }

                department = instantiateDepartment(rs);
                Employee employee = instantiateEmployee(rs, department);
                employees.add(employee);
            }
            return employees;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }
}
