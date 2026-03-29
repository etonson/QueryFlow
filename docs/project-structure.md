# 專案結構 (Project Structure)

本頁面說明了 QueryFlow 內部的組織結構與元件互動方式。

## 套件組織 (Package Organization)

所有的程式碼均位於 `com.litequery` 套件下：

-   `api/`: 入口點類別。
    -   `LiteQuery.java`: 使用者與函式庫互動的主要起點。
-   `builder/`: 查詢建構核心邏輯。
    -   `QueryBuilder.java`: 負責儲存查詢狀態並在 `.execute()` 時執行最終邏輯。
-   `function/`: 各種輔助函數。
    -   `Columns.java`: 建立投影欄位的輔助類別。
    -   `Aggregations.java`: 各種彙總運算函數。
    -   `LogicCaculate.java`: 用於 `.where()` 的邏輯運算子。
-   `model/`: 內部資料模型。
    -   `SelectColumn.java`: 抽象化 `select` 欄位行為。
-   `output/`: 查詢結果封裝。
    -   `ReportTable.java`: 用於儲存與呈現表格形式的結果。

## 查詢執行流程

1.  **來源輸入**: 使用 `LiteQuery.from(list)` 初始化。
2.  **配置**: 使用 `.where()`, `.groupBy()`, `.select()` 配置查詢參數。
3.  **執行**:
    -   過濾 (Filter): 根據 `where` 條件篩選資料。
    -   分組 (Group): 將篩選後的資料按 key 分組。
    -   映射 (Map): 遍歷每個分組，計算 `select` 中定義的投影或彙總欄位。
    -   建構: 將結果封裝進 `ReportTable`。
