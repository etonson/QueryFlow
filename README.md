# QueryFlow 專案說明

## 概覽

QueryFlow 是一個輕量級、流暢的 Java 記憶體中查詢函式庫。它讓您可以使用類似 SQL 的語法，以鏈式呼叫 (method-chaining) 的方式對 Java 的 `List` 集合進行篩選、分組、投影和彙總計算，最終產生結構化的報表結果。

本專案的設計初衷是為了在不引入大型依賴（如資料庫或其他複雜的資料處理框架）的情況下，提供一個簡單直觀的 API 來處理常見的記憶體資料整理任務。

## 核心功能

- **流式 API 設計**：語法連貫易讀，接近自然語言。
- **條件過濾 (Where)**：支援使用 Lambda 運算式進行複雜的資料篩選。
- **分組 (Group By)**：可根據物件的特定屬性對集合進行分組。
- **彙總計算 (Aggregations)**：內建 `sum` (加總) 和 `count` (計數) 等常用彙總函式。
- **欄位投影 (Select)**：可自由選擇要輸出的欄位，並支援重新命名。
- **型別安全**：利用 Java 泛型和 Lambda 運算式，在編譯時期提供型別檢查。
- **無外部依賴**：專案本身是一個純粹的 Java 實作，非常輕量。

## 快速入門

以下是一個完整的範例，展示如何使用 QueryFlow 查詢員工資料。

### 1. 準備資料

首先，假設我們有一個 `Employee` 類別和一個包含多個員工物件的 `List`：

```java
// 員工類別
public class Employee {
    private int id;
    private int departmentId;
    private double salary;

    // getters, setters, constructor...
}

// 員工資料列表
List<Employee> employees = List.of(
    new Employee(1, 10, 60000),
    new Employee(2, 10, 55000),
    new Employee(3, 20, 70000),
    new Employee(4, 20, 75000)
);
```

### 2. 編寫查詢

現在，我們想找出薪水超過 `50000` 的員工，並按部門分組，計算每個部門的總薪資。

```java
import static com.example.litequery.Columns.col;
import static com.example.litequery.Aggregations.sum;

// ...

ReportTable result = LiteQuery.from(employees)
    .where(e -> e.getSalary() > 50000)
    .groupBy(Employee::getDepartmentId)
    .select(
        col("DeptId", Employee::getDepartmentId),
        sum(Employee::getSalary).as("TotalSalary")
    )
    .execute();

// 輸出結果
result.print();

```

### 3. 執行與結果

執行 `.execute()` 後會返回一個 `ReportTable` 物件，其中包含了查詢結果。上述查詢的結果會類似於：

| DeptId | TotalSalary |
|--------|-------------|
| 10     | 115000.0    |
| 20     | 145000.0    |

## API 詳解

### `LiteQuery.from(List<T> source)`
查詢的起點。傳入一個 `List` 作為資料來源。

### `.where(Predicate<T> predicate)`
過濾資料，相當於 SQL 的 `WHERE` 子句。只有滿足條件的元素才會進入下一步。

#### 邏輯運算子

QueryFlow 提供了多種邏輯運算子來構建複雜的查詢條件：

| 方法 | 說明 | 範例 |
|------|------|------|
| `and(predicates...)` | AND 邏輯（所有條件都為真） | `and(e -> e.getSalary() > 50000, e -> e.getDeptId() == 10)` |
| `or(predicates...)` | OR 邏輯（任一條件為真） | `or(e -> e.getSalary() > 80000, e -> e.getId() == 1)` |
| `in(extractor, values...)` | 欄位值在給定值中 | `in(Employee::getDeptId, 10, 20)` |
| `notIn(extractor, values...)` | 欄位值不在給定值中 | `notIn(Employee::getId, 1, 3)` |
| `eq(extractor, value)` | 欄位值等於給定值 | `eq(Employee::getName, "Alice")` |
| `neq(extractor, value)` | 欄位值不等於給定值 | `neq(Employee::getName, "Bob")` |

#### 通配符匹配（Wildcard Matching）

`in`、`notIn`、`eq`、`neq` 方法對於 **String 類型** 的欄位支援通配符模式：

| 模式 | 說明 | 範例 |
|------|------|------|
| `A*` | 前綴匹配（以 A 開頭） | `eq(Employee::getName, "A*")` 匹配 Alice、Alex、Anna |
| `*A` | 後綴匹配（以 A 結尾） | `in(Employee::getName, "*a")` 匹配 Anna |
| `*A*` | 包含匹配（包含 A） | `eq(Employee::getName, "*li*")` 匹配 Alice、Charlie |
| `A` | 精確匹配 | `eq(Employee::getName, "Alice")` 只匹配 Alice |

**通配符使用範例：**

```java
// 查找名字以 "A" 開頭的員工
LiteQuery.from(employees)
    .where(in(Employee::getName, "A*"))
    .select(col("Name", Employee::getName))
    .execute();

// 查找名字以 "son" 結尾的員工
LiteQuery.from(employees)
    .where(eq(Employee::getName, "*son"))
    .select(col("Name", Employee::getName))
    .execute();

// 查找名字包含 "li" 的員工
LiteQuery.from(employees)
    .where(in(Employee::getName, "*li*"))
    .select(col("Name", Employee::getName))
    .execute();

// 查找名字不以 "A" 開頭的員工
LiteQuery.from(employees)
    .where(notIn(Employee::getName, "A*"))
    .select(col("Name", Employee::getName))
    .execute();

// 混合使用：名字以 "A" 開頭 或 以 "b" 結尾
LiteQuery.from(employees)
    .where(in(Employee::getName, "A*", "*b"))
    .select(col("Name", Employee::getName))
    .execute();
```

### `.groupBy(Function<T, ?> keySelector)`
分組資料，相當于 SQL 的 `GROUP BY` 子句。傳入一個函式來指定分組的鍵。

### `.select(SelectColumn<?>... columns)`
定義輸出的欄位，相當於 SQL 的 `SELECT` 子句。`select` 方法可以接受兩種不同類型的欄位：

1.  **欄位投影 (`Columns.col`)**
    -   用於從分組中提取一個值作為欄位。通常是分組的鍵，或是分組內所有元素都相同的一個屬性。
    -   語法：`col("欄位名", T::getSomeProperty)`

2.  **彙總計算 (`Aggregations.sum`, `Aggregations.count`)**
    -   用於對整個分組進行計算。
    -   `sum`: 計算數值屬性的總和。語法：`sum(T::getNumericProperty)`
    -   `count`: 計算分組內的元素數量。語法：`count()`

### `.as(String newName)`
為 `select` 中定義的欄位指定一個別名。

### `.execute()`
終端操作。執行整個查詢鏈，並將結果封裝成 `ReportTable` 物件返回。

## 更多範例

### 範例一：條件過濾與簡單投影 (無分組)

查詢薪水大於等於 `70000` 的員工，並只顯示其 ID 和薪水。

```java
ReportTable result = LiteQuery.from(employees)
    .where(e -> e.getSalary() >= 70000)
    .select(
        col("Id", Employee::getId),
        col("Salary", Employee::getSalary)
    )
    .execute();
```
**結果**:
| Id | Salary |
|----|--------|
| 3  | 70000  |
| 4  | 75000  |

### 範例二：全表彙總 (無分組)

計算所有員工的總薪資和總人數。

```java
ReportTable result = LiteQuery.from(employees)
    .select(
        sum(Employee::getSalary).as("CompanyTotal"),
        count().as("TotalEmployees")
    )
    .execute();
```
**結果**:
| CompanyTotal | TotalEmployees |
|--------------|----------------|
| 260000.0     | 4              |


## 專案結構

-   `LiteQuery.java`: 使用者與函式庫互動的主要入口。
-   `QueryBuilder.java`: 核心的鏈式查詢建構器，負責組織查詢邏輯。
-   `SelectColumn.java`: 定義欄位行為的介面（包含投影和彙總）。
-   `Columns.java`: 提供 `col` 方法，用於建立投影欄位。
-   `Aggregations.java`: 提供 `sum`, `count` 等彙總函式。
-   `ReportTable.java`: 用於儲存和展示最終查詢結果的資料結構。

## 作者

-   Eton.Lin
