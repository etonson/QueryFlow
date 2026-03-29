package demo;

import com.litequery.api.LiteQuery;
import com.litequery.output.ReportTable;
import demo.context.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.litequery.function.Columns.col;
import static com.litequery.function.LogicCaculate.*;
import static org.junit.jupiter.api.Assertions.*;

/*
    @Author: Eton.Lin
    @Description: LiteQuery Logic and Filtering Test Cases
    @Date: 2025/12/8 下午 11:43
*/
public class LogicFilterTest {

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
