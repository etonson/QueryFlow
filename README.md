# QueryFlow

QueryFlow 是一個輕量級、流暢的 Java 記憶體中查詢函式庫。它讓您可以使用類似 SQL 的語法，對 Java 的 `List` 集合進行篩選、分組、投影和彙總計算。

## 核心特點

-   **流暢 API (Fluent API)**: 模仿 SQL 語法，語法連貫易讀。
-   **條件過濾 (Where)**: 支援 Lambda 運算式與強大的邏輯運算子（包含通配符匹配）。
-   **分組與彙總 (Group By & Aggregations)**: 簡單直觀的分組與計算 (sum, count)。
-   **零依賴 (Zero Dependencies)**: 純 Java 實作，極其輕量。

## 快速預覽

```java
ReportTable result = LiteQuery.from(employees)
    .where(e -> e.getSalary() > 50000)
    .groupBy(Employee::getDepartmentId)
    .select(
        col("DeptId", Employee::getDepartmentId),
        sum(Employee::getSalary).as("TotalSalary"),
        count().as("EmployeeCount")
    )
    .execute();

result.print();
```

## 詳細文件 (Documentation)

請參閱 `docs/` 目錄下的詳細文件：

1.  **[專案簡介](docs/introduction.md)**: 核心理念與功能。
2.  **[快速入門](docs/getting-started.md)**: 您的第一個查詢範例。
3.  **[過濾邏輯](docs/filtering.md)**: `where`、邏輯運算子與通配符。
4.  **[分組與彙總](docs/aggregations.md)**: `groupBy`、`sum` 與 `count`。
5.  **[API 參考](docs/api-reference.md)**: 方法與參數說明清單。
6.  **[專案結構](docs/project-structure.md)**: 套件組織與執行流程。
7.  **[優化建議](docs/optimization-suggestions.md)**: 未來功能擴展與效能優化提案。

---

**作者**: Eton.Lin
