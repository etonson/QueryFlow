# 優化建議 (Optimization Suggestions)

為了讓 QueryFlow 成為更成熟、高效且易於擴展的函式庫，以下整理了未來可以進行優化的五大方向。

## 1. 核心查詢功能補完 (SQL 完整性)

目前已具備基礎功能，但可增加以下 SQL 常見特性以應對複雜報表：

-   **排序 (`OrderBy`)**：支援單一或多個欄位的升冪/降冪排序。
-   **分頁與限制 (`Limit` & `Offset`)**：處理大型資料集時的分頁需求。
-   **更多彙總函數**：增加 `avg` (平均值)、`min` (最小值)、`max` (最大值)。
-   **唯一值 (`Distinct`)**：過濾結果中重複的資料列。

## 2. 效能優化 (處理大數據量)

當資料量達到十萬筆以上時，效能將成為關鍵：

-   **平行處理 (Parallel Processing)**：利用 Java 8 的 `parallelStream()` 進行並行過濾與計算。
-   **延遲執行優化 (Query Optimization)**：在 `.execute()` 前進行條件合併（如 Predicate 組合優化），減少遍歷次數。

## 3. 型別安全與開發者體驗 (Type Safety)

改善目前結果需要頻繁轉型 (Casting) 的問題：

-   **型別化結果對映**：支援將 `ReportTable` 自動轉為指定的 POJO 或 Java 17 `Record`。
    ```java
    List<MyDTO> results = LiteQuery.from(list).execute().as(MyDTO.class);
    ```
-   **自定義彙總器**：開放介面讓使用者實作自定義的彙總邏輯（例如計算標準差）。

## 4. 健壯性與錯誤處理 (Robustness)

-   **Null 安全處理**：在欄位提取為 `null` 時提供預設處理機制（如 `sum` 忽略 null）。
-   **語法驗證**：若在 `select` 中引用了非分組鍵且非彙總的欄位，拋出具體的異常提示（類似 SQL 語法錯誤）。

## 5. 輸出與整合 (Integration)

-   **多樣化輸出格式**：除了控制台列印，支援輸出為 `JSON`、`CSV` 或 `Markdown` 格式。
-   **Stream 支援**：允許直接從 `java.util.stream.Stream` 開始查詢，增強與現有流式 API 的整合性。

---

## 優先級建議

1.  **基礎功能補完**：排序 (`OrderBy`) 與 平均值 (`Avg`)。
2.  **整合性優化**：JSON 輸出格式。
3.  **體驗優化**：型別化結果對映。
