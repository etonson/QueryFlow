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
}

