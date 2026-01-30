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

    // ========== 通配符匹配測試 ==========

    private List<Employee> employeesWithNames;

    private void setUpEmployeesWithNames() {
        employeesWithNames = List.of(
                new Employee(1, 10, 60000, "Alice"),
                new Employee(2, 10, 55000, "Bob"),
                new Employee(3, 20, 70000, "Charlie"),
                new Employee(4, 20, 75000, "David"),
                new Employee(5, 30, 80000, "Alex"),
                new Employee(6, 30, 65000, "Anna")
        );
    }

    @Test
    public void testInWithPrefixWildcard() {
        setUpEmployeesWithNames();
        // Test IN with prefix wildcard: names starting with "A"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(in(Employee::getName, "A*"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(3, result.getRows().size());

        // Verify all names start with "A"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertTrue(name.startsWith("A"), "Name should start with 'A': " + name);
        }
    }

    @Test
    public void testInWithSuffixWildcard() {
        setUpEmployeesWithNames();
        // Test IN with suffix wildcard: names ending with "e"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(in(Employee::getName, "*e"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify all names end with "e"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertTrue(name.endsWith("e"), "Name should end with 'e': " + name);
        }
    }

    @Test
    public void testInWithContainsWildcard() {
        setUpEmployeesWithNames();
        // Test IN with contains wildcard: names containing "li"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(in(Employee::getName, "*li*"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify all names contain "li"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertTrue(name.contains("li"), "Name should contain 'li': " + name);
        }
    }

    @Test
    public void testNotInWithPrefixWildcard() {
        setUpEmployeesWithNames();
        // Test NOT IN with prefix wildcard: names NOT starting with "A"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(notIn(Employee::getName, "A*"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(3, result.getRows().size());

        // Verify no names start with "A"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertFalse(name.startsWith("A"), "Name should NOT start with 'A': " + name);
        }
    }

    @Test
    public void testNotInWithSuffixWildcard() {
        setUpEmployeesWithNames();
        // Test NOT IN with suffix wildcard: names NOT ending with "e"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(notIn(Employee::getName, "*e"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(4, result.getRows().size());

        // Verify no names end with "e"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertFalse(name.endsWith("e"), "Name should NOT end with 'e': " + name);
        }
    }

    @Test
    public void testEqWithPrefixWildcard() {
        setUpEmployeesWithNames();
        // Test eq with prefix wildcard: name starts with "Al"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(eq(Employee::getName, "Al*"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());

        // Verify all names start with "Al"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertTrue(name.startsWith("Al"), "Name should start with 'Al': " + name);
        }
    }

    @Test
    public void testEqWithExactMatch() {
        setUpEmployeesWithNames();
        // Test eq with exact match
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(eq(Employee::getName, "Alice"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(1, result.getRows().size());
        assertEquals("Alice", result.getRows().getFirst().get("Name"));
    }

    @Test
    public void testNeqWithPrefixWildcard() {
        setUpEmployeesWithNames();
        // Test neq with prefix wildcard: name does NOT start with "A"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(neq(Employee::getName, "A*"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(3, result.getRows().size());

        // Verify no names start with "A"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertFalse(name.startsWith("A"), "Name should NOT start with 'A': " + name);
        }
    }

    @Test
    public void testInWithMultipleWildcardPatterns() {
        setUpEmployeesWithNames();
        // Test IN with multiple patterns: names starting with "A" OR ending with "b"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(in(Employee::getName, "A*", "*b"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(4, result.getRows().size());

        // Verify all names start with "A" or end with "b"
        for (Map<String, Object> row : result.getRows()) {
            String name = (String) row.get("Name");
            assertTrue(name.startsWith("A") || name.endsWith("b"),
                    "Name should start with 'A' or end with 'b': " + name);
        }
    }

    @Test
    public void testInWithMixedWildcardAndExact() {
        setUpEmployeesWithNames();
        // Test IN with mixed patterns: "Bob" exact OR names starting with "C"
        ReportTable result = LiteQuery.from(employeesWithNames)
                .where(in(Employee::getName, "Bob", "C*"))
                .select(
                        col("Id", Employee::getId),
                        col("Name", Employee::getName)
                )
                .execute();

        assertNotNull(result);
        assertEquals(2, result.getRows().size());
    }
}
