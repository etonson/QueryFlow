# API 參考 (API Reference)

本頁面整理了 QueryFlow 所有公開 API 的快速參考。

## 查詢入口

| 方法 | 說明 |
|------|------|
| `LiteQuery.from(List<T> source)` | 查詢的起點，傳入資料集合。 |

## QueryBuilder 鏈式操作

| 方法 | 說明 |
|------|------|
| `.where(Predicate<T>... predicates)` | 過濾資料。多個條件視為 AND 關係。 |
| `.groupBy(Function<T, ?> keySelector)` | 分組資料，傳入分組鍵提取器。 |
| `.select(SelectColumn<?>... columns)` | 定義輸出的欄位（包含投影與彙總）。 |
| `.execute()` | 終端操作。執行查詢並回傳 `ReportTable`。 |

## 輔助方法

### 欄位投影 (`Columns`)

| 方法 | 說明 |
|------|------|
| `col(String name, Function<T, ?> extractor)` | 定義投影欄位及其提取函數。 |

### 彙總計算 (`Aggregations`)

| 方法 | 說明 |
|------|------|
| `sum(Function<T, ? extends Number> extractor)` | 加總指定欄位。 |
| `count()` | 計算分組內的數量。 |
| `.as(String name)` | 為欄位指定別名。 |

### 邏輯運算 (`LogicCaculate`)

| 方法 | 說明 |
|------|------|
| `and(Predicate<T>... predicates)` | 所有條件皆成立。 |
| `or(Predicate<T>... predicates)` | 任一條件成立。 |
| `in(Function<T, ?> extractor, Object... values)` | 欄位值在清單中（支援通配符）。 |
| `notIn(Function<T, ?> extractor, Object... values)` | 欄位值不在清單中（支援通配符）。 |
| `eq(Function<T, ?> extractor, Object value)` | 欄位值等於給定值（支援通配符）。 |
| `neq(Function<T, ?> extractor, Object value)` | 欄位值不等於給定值（支援通配符）。 |

## 輸出結果 (`ReportTable`)

| 方法 | 說明 |
|------|------|
| `getRows()` | 取得所有查詢結果列（`List<Map<String, Object>>`）。 |
| `print()` | 在控制台以表格形式列印結果。 |
