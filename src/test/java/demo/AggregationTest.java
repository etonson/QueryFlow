package demo;

import com.litequery.api.LiteQuery;
import com.litequery.output.ReportTable;
import demo.context.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.litequery.function.Columns.col;
import static com.litequery.function.Aggregations.sum;
import static com.litequery.function.Aggregations.count;
import static org.junit.jupiter.api.Assertions.*;

/*
    @Author: Eton.Lin
    @Description: LiteQuery Aggregation and Basic Query Test Cases
    @Date: 2025/12/8 下午 11:43
*/
public class AggregationTest {

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
                .where(e -> e.getSalary() > 50000, e -> e.getId() != 1)
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
}
