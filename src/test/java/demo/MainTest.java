package demo;

import com.litequery.example.Employee;
import com.litequery.api.LiteQuery;
import com.litequery.output.ReportTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.litequery.function.Columns.col;
import static com.litequery.function.Aggregations.sum;
import static com.litequery.function.Aggregations.count;
import static com.litequery.function.LogicCaculate.*;
import static org.junit.jupiter.api.Assertions.*;

/*
    @Author: Eton.Lin
    @Description: LiteQuery Demo Test Cases
    @Date: 2025/12/8 下午 11:43
*/
public class MainTest {

    private List<Employee> employees;

    @BeforeEach
    public void setUp() {
        employees = List.of(
                new Employee(1, 10, 60000),
                new Employee(2, 10, 55000),
                new Employee(3, 20, 70000),
                new Employee(4, 20, 75000)
        );
    }

    @Test
    public void testBasicGroupByAndSum() {
        ReportTable result = LiteQuery.from(employees)
                .where(e -> e.getSalary() > 50000,e-> e.getId() !=1)
                .groupBy(Employee::getDepartmentId)
                .select(
                        col("DeptId", Employee::getDepartmentId),
                        sum(Employee::getSalary).as("TotalSalary")
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        Map<Integer, Map<String, Object>> byDept = new HashMap<>();
        for (Map<String, Object> row : result.getRows()) {
            byDept.put(((Number) row.get("DeptId")).intValue(), row);
        }

        assertTrue(byDept.containsKey(10));
        assertTrue(byDept.containsKey(20));
        assertEquals(55000.0, ((Number) byDept.get(10).get("TotalSalary")).doubleValue(), 0.0001);
        assertEquals(145000.0, ((Number) byDept.get(20).get("TotalSalary")).doubleValue(), 0.0001);
    }

    @Test
    public void testWithCount() {
        ReportTable result = LiteQuery.from(employees)
                .groupBy(Employee::getDepartmentId)
                .select(
                        col("DeptId", Employee::getDepartmentId),
                        sum(Employee::getSalary).as("TotalSalary"),
                        count().as("EmployeeCount")
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify each group's count without relying on order
        for (Map<String, Object> row : result.getRows()) {
            assertEquals(2, ((Number) row.get("EmployeeCount")).intValue());
        }
    }

    @Test
    public void testWhereFilter() {
        ReportTable result = LiteQuery.from(employees)
                .where(e -> e.getSalary() >= 70000)
                .select(
                        col("Id", Employee::getId),
                        col("Salary", Employee::getSalary)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Validate salary threshold
        for (Map<String, Object> row : result.getRows()) {
            int salary = ((Number) row.get("Salary")).intValue();
            assertTrue(salary >= 70000);
        }
    }

    @Test
    public void testNoGroupByAggregation() {
        ReportTable result = LiteQuery.from(employees)
                .select(
                        sum(Employee::getSalary).as("CompanyTotal"),
                        count().as("TotalEmployees")
                )
                .execute();

        assertNotNull(result);
        assertEquals(1, result.getRows().size());

        Map<String, Object> row = result.getRows().getFirst();
        assertEquals(260000.0, ((Number) row.get("CompanyTotal")).doubleValue(), 0.0001);
        assertEquals(4, ((Number) row.get("TotalEmployees")).intValue());
    }

    @Test
    public void testColumnRenaming() {
        ReportTable result = LiteQuery.from(employees)
                .groupBy(Employee::getDepartmentId)
                .select(
                        col("部門", Employee::getDepartmentId),
                        sum(Employee::getSalary).as("總薪資")
                )
                .execute();

        assertNotNull(result);
        // Ensure each row contains renamed columns
        for (Map<String, Object> row : result.getRows()) {
            assertTrue(row.containsKey("部門"));
            assertTrue(row.containsKey("總薪資"));
        }
    }

    @Test
    public void testNotInLogic() {
        // Test NOT IN: exclude employees with ID 1 and 3
        ReportTable result = LiteQuery.from(employees)
                .where(notIn(Employee::getId, 1, 3))
                .select(
                        col("Id", Employee::getId),
                        col("Salary", Employee::getSalary)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify that only employees with ID 2 and 4 are included
        for (Map<String, Object> row : result.getRows()) {
            int id = ((Number) row.get("Id")).intValue();
            assertTrue(id == 2 || id == 4);
        }
    }

    @Test
    public void testInLogic() {
        // Test IN: include only employees in department 10
        ReportTable result = LiteQuery.from(employees)
                .where(in(Employee::getDepartmentId, 10))
                .select(
                        col("Id", Employee::getId),
                        col("DeptId", Employee::getDepartmentId)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify all employees are in department 10
        for (Map<String, Object> row : result.getRows()) {
            assertEquals(10, ((Number) row.get("DeptId")).intValue());
        }
    }

    @Test
    public void testAndLogic() {
        // Test AND: salary > 60000 AND department = 20
        ReportTable result = LiteQuery.from(employees)
                .where(and(
                        e -> e.getSalary() > 60000,
                        e -> e.getDepartmentId() == 20
                ))
                .select(
                        col("Id", Employee::getId),
                        col("Salary", Employee::getSalary),
                        col("DeptId", Employee::getDepartmentId)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify all meet both conditions
        for (Map<String, Object> row : result.getRows()) {
            assertTrue(((Number) row.get("Salary")).intValue() > 60000);
            assertEquals(20, ((Number) row.get("DeptId")).intValue());
        }
    }

    @Test
    public void testOrLogic() {
        // Test OR: salary >= 75000 OR id = 1
        ReportTable result = LiteQuery.from(employees)
                .where(or(
                        e -> e.getSalary() >= 75000,
                        e -> e.getId() == 1
                ))
                .select(
                        col("Id", Employee::getId),
                        col("Salary", Employee::getSalary)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify each employee meets at least one condition
        for (Map<String, Object> row : result.getRows()) {
            int id = ((Number) row.get("Id")).intValue();
            int salary = ((Number) row.get("Salary")).intValue();
            assertTrue(salary >= 75000 || id == 1);
        }
    }

    @Test
    public void testComplexLogicCombination() {
        // Test complex combination: (salary > 60000 AND NOT IN department 10) OR id = 2
        ReportTable result = LiteQuery.from(employees)
                .where(or(
                        and(
                                e -> e.getSalary() > 60000,
                                notIn(Employee::getDepartmentId, 10)
                        ),
                        e -> e.getId() == 2
                ))
                .select(
                        col("Id", Employee::getId),
                        col("Salary", Employee::getSalary),
                        col("DeptId", Employee::getDepartmentId)
                )
                .execute();

        assertNotNull(result);
        // Should include: employee 2 (id=2), employee 3 (salary=70000, dept=20), employee 4 (salary=75000, dept=20)
        assertEquals(3, result.getRows().size());
    }
}
