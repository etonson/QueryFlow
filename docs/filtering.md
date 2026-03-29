# 過濾邏輯 (Filtering)

QueryFlow 提供了強大的過濾功能，讓您能靈活地篩選資料。

## 基礎過濾

`.where(Predicate<T> predicate)` 接受一個 Lambda 運算式作為過濾條件。只有滿足條件的元素才會進入後續的分組或投影步驟。

```java
// 單一條件
.where(e -> e.getSalary() > 50000)

// 多個條件 (隱含 AND 邏輯)
.where(e -> e.getSalary() > 50000, e -> e.getId() != 1)
```

## 邏輯運算子 (Logic Operators)

為了構建更複雜的查詢，您可以導入 `LogicCaculate` 中的靜態方法：

```java
import static com.litequery.function.LogicCaculate.*;
```

| 方法 | 說明 | 範例 |
|------|------|------|
| `and(predicates...)` | AND 邏輯（所有條件都為真） | `and(e -> e.getSalary() > 50000, e -> e.getDeptId() == 10)` |
| `or(predicates...)` | OR 邏輯（任一條件為真） | `or(e -> e.getSalary() > 80000, e -> e.getId() == 1)` |
| `in(extractor, values...)` | 欄位值在給定值中 | `in(Employee::getDeptId, 10, 20)` |
| `notIn(extractor, values...)` | 欄位值不在給定值中 | `notIn(Employee::getId, 1, 3)` |
| `eq(extractor, value)` | 欄位值等於給定值 | `eq(Employee::getName, "Alice")` |
| `neq(extractor, value)` | 欄位值不等於給定值 | `neq(Employee::getName, "Bob")` |

## 通配符匹配 (Wildcard Matching)

`in`、`notIn`、`eq`、`neq` 對於 **String 類型** 的欄位支援通配符模式：

| 模式 | 說明 | 範例 | 匹配項 |
|------|------|------|--------|
| `A*` | 前綴匹配 | `eq(Employee::getName, "A*")` | Alice, Alex, Anna |
| `*e` | 後綴匹配 | `in(Employee::getName, "*e")` | Alice, Charlie |
| `*li*` | 包含匹配 | `eq(Employee::getName, "*li*")` | Alice, Charlie |
| `Alice` | 精確匹配 | `eq(Employee::getName, "Alice")` | Alice |

### 通配符使用範例：

```java
// 查找名字以 "A" 開頭的員工
LiteQuery.from(employees)
    .where(in(Employee::getName, "A*"))
    .execute();

// 混合使用：名字以 "A" 開頭 或 以 "b" 結尾
LiteQuery.from(employees)
    .where(in(Employee::getName, "A*", "*b"))
    .execute();
```

### 複雜邏輯組合：

```java
// (薪水 > 60000 且 部門不是 10) 或 ID 是 2
.where(or(
    and(
        e -> e.getSalary() > 60000,
        notIn(Employee::getDepartmentId, 10)
    ),
    e -> e.getId() == 2
))
```
