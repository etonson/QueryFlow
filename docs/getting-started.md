# 快速入門 (Getting Started)

本指南將帶領您在幾分鐘內完成您的第一個 QueryFlow 查詢。

## 1. 準備資料模型與集合

假設我們有一個 `Employee` 類別：

```java
public class Employee {
    private int id;
    private int departmentId;
    private int salary;
    private String name;

    // constructor, getters, setters...
}
```

以及一個包含多個員工物件的 `List`：

```java
List<Employee> employees = List.of(
    new Employee(1, 10, 60000, "Alice"),
    new Employee(2, 10, 55000, "Bob"),
    new Employee(3, 20, 70000, "Charlie"),
    new Employee(4, 20, 75000, "David")
);
```

## 2. 撰寫您的第一個查詢

要使用 QueryFlow，您需要導入必要的靜態方法。

```java
import com.litequery.api.LiteQuery;
import com.litequery.output.ReportTable;
import static com.litequery.function.Columns.col;
import static com.litequery.function.Aggregations.sum;

// ...

// 查詢薪水大於 50000 的員工，按部門分組，計算總薪資
ReportTable result = LiteQuery.from(employees)
    .where(e -> e.getSalary() > 50000)
    .groupBy(Employee::getDepartmentId)
    .select(
        col("DeptId", Employee::getDepartmentId),
        sum(Employee::getSalary).as("TotalSalary")
    )
    .execute();
```

## 3. 輸出結果

`.execute()` 會回傳一個 `ReportTable`，您可以輕鬆地列印或遍歷結果：

```java
result.print();
```

**預期輸出**:

| DeptId | TotalSalary |
|--------|-------------|
| 10     | 115000.0    |
| 20     | 145000.0    |
