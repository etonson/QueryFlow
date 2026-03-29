# 分組與彙總 (Grouping & Aggregations)

QueryFlow 支援強大的資料分組與彙總計算，讓您可以快速從原始資料中提煉出報表。

## 1. 資料分組 (.groupBy)

使用 `.groupBy()` 根據物件的某個屬性對資料進行分組。

```java
.groupBy(Employee::getDepartmentId)
```

## 2. 欄位定義 (.select)

`.select()` 方法定義了最終報表包含哪些欄位。它可以接受兩種對象：

### 欄位投影 (`col`)

用於從分組中提取特定值。通常是分組鍵本身。

```java
import static com.litequery.function.Columns.col;

// ...
col("部門ID", Employee::getDepartmentId)
```

### 彙總計算 (`sum`, `count`)

用於對整個分組進行計算。

```java
import static com.litequery.function.Aggregations.*;

// ...
sum(Employee::getSalary)
count()
```

- **`sum`**: 計算指定數值欄位的總和。
- **`count`**: 計算該分組內的物件總數。

## 3. 別名指定 (.as)

每個定義在 `select` 中的欄位都可以使用 `.as()` 來指定報表顯示的名稱。

```java
sum(Employee::getSalary).as("總薪資")
```

## 完整範例：按部門彙總薪資

```java
ReportTable result = LiteQuery.from(employees)
    .groupBy(Employee::getDepartmentId)
    .select(
        col("Dept", Employee::getDepartmentId),
        sum(Employee::getSalary).as("TotalSalary"),
        count().as("EmpCount")
    )
    .execute();
```

## 無分組的彙總 (全表彙總)

如果您沒有呼叫 `.groupBy()`，`select` 中的彙總函數將對所有通過 `where` 過濾後的資料進行計算，並產生一條結果記錄。

```java
ReportTable result = LiteQuery.from(employees)
    .select(
        sum(Employee::getSalary).as("CompanyTotal"),
        count().as("TotalEmployees")
    )
    .execute();
```
