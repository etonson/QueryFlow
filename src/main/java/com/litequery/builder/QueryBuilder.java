package com.litequery.builder;

import com.litequery.function.LogicCaculate;
import com.litequery.model.SelectColumn;
import com.litequery.output.ReportTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *    @Author: Eton.Lin
 *    @Description: 用於流暢查詢構造的查詢建構器
 *    @Date: 2025/12/8 下午 11:42
*/
public class QueryBuilder<T> {

    private final List<T> source;
    private Predicate<T> wherePredicate = e -> true;
    private Function<T, ?> groupByKeySelector;
    private final List<SelectColumn<? super T>> selectColumns = new ArrayList<>();

    public QueryBuilder(List<T> source) {
        if (source == null) {
            throw new IllegalArgumentException("原始列表不能為空");
        }
        this.source = source;
    }

    public QueryBuilder<T> where(Predicate<T> predicate) {
        this.wherePredicate = predicate;
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<T> where(Predicate<T>... predicates) {
        this.wherePredicate = LogicCaculate.and(predicates);
        return this;
    }

    public QueryBuilder<T> groupBy(Function<T, ?> keySelector) {
        this.groupByKeySelector = keySelector;
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<T> select(SelectColumn<? super T>... columns) {
        this.selectColumns.addAll(Arrays.asList(columns));
        return this;
    }

    public ReportTable execute() {
        ReportTable table = new ReportTable();

        // 1. 過濾
        List<T> filtered = source.stream()
                .filter(wherePredicate)
                .collect(Collectors.toList());

        // 2. 分組
        if (groupByKeySelector != null) {
            Map<Object, List<T>> grouped = filtered.stream()
                    .collect(Collectors.groupingBy(groupByKeySelector));

            // 3. 選擇欄位 & 轉換為列
            for (Map.Entry<Object, List<T>> entry : grouped.entrySet()) {
                Map<String, Object> row = new LinkedHashMap<>();
                List<T> groupItems = entry.getValue();

                for (SelectColumn<? super T> column : selectColumns) {
                    row.put(column.getName(), column.getValue(groupItems));
                }

                table.addRow(row);
            }
        } else {
            // 無分組
            boolean hasAggregation = selectColumns.stream().anyMatch(SelectColumn::isAggregation);

            if (hasAggregation) {
                // 在整個過濾後的列表上進行聚合
                Map<String, Object> row = new LinkedHashMap<>();
                for (SelectColumn<? super T> column : selectColumns) {
                    row.put(column.getName(), column.getValue(filtered));
                }
                table.addRow(row);
            } else {
                // 為每個項目進行簡單的投影
                for (T item : filtered) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    List<T> singleItemGroup = List.of(item);
                    for (SelectColumn<? super T> column : selectColumns) {
                        row.put(column.getName(), column.getValue(singleItemGroup));
                    }
                    table.addRow(row);
                }
            }
        }

        return table;
    }
}

