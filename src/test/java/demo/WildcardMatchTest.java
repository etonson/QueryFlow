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
    @Description: LiteQuery Wildcard Matching Test Cases
    @Date: 2025/12/8 下午 11:43
*/
public class WildcardMatchTest {

    private List<Employee> employeesWithNames;

    @BeforeEach
    public void setUp() {
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
