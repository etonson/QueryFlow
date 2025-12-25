package com.litequery.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: Eton.Lin
 * @Description: 用於儲存查詢結果的表格
 * @Date: 2025/12/8 下午 11:37
*/
public class ReportTable {
    private final List<Map<String, Object>> rows = new ArrayList<>();

    public void addRow(Map<String, Object> row) {
        rows.add(row);
    }

    public List<Map<String, Object>> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public void print() {
        if (rows.isEmpty()) {
            System.out.println("(Empty result set)");
            return;
        }

        // 從第一行取得所有欄位名稱
        Map<String, Object> firstRow = rows.getFirst();
        List<String> columnNames = new ArrayList<>(firstRow.keySet());

        // 計算欄位寬度
        Map<String, Integer> columnWidths = new java.util.HashMap<>();
        for (String colName : columnNames) {
            int maxWidth = colName.length();
            for (Map<String, Object> row : rows) {
                Object value = row.get(colName);
                int valueWidth = (value == null ? "null" : value.toString()).length();
                maxWidth = Math.max(maxWidth, valueWidth);
            }
            columnWidths.put(colName, maxWidth + 2); // 加上邊距
        }

        // 列印標題分隔線
        printSeparator(columnNames, columnWidths);

        // 列印標題
        System.out.print("| ");
        for (String colName : columnNames) {
            System.out.printf("%-" + columnWidths.get(colName) + "s | ", colName);
        }
        System.out.println();

        // 列印標題分隔線
        printSeparator(columnNames, columnWidths);

        // 列印行數據
        for (Map<String, Object> row : rows) {
            System.out.print("| ");
            for (String colName : columnNames) {
                Object value = row.get(colName);
                String valueStr = (value == null ? "null" : value.toString());
                System.out.printf("%-" + columnWidths.get(colName) + "s | ", valueStr);
            }
            System.out.println();
        }

        // 列印底部分隔線
        printSeparator(columnNames, columnWidths);
        System.out.println("(" + rows.size() + " 列)");
    }

    private void printSeparator(List<String> columnNames, Map<String, Integer> columnWidths) {
        System.out.print("+");
        for (String colName : columnNames) {
            System.out.print("-".repeat(columnWidths.get(colName) + 2) + "+");
        }
        System.out.println();
    }
}

