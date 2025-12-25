package com.litequery.function;

import com.litequery.model.SelectColumn;
import java.util.List;
import java.util.function.Function;

/**
 * @Author: Eton.Lin
 * @Description: 聚合函數（求和、計數等）
 * @Date: 2025/12/8 下午 11:41
*/
public class Aggregations {
    public static <T> SelectColumn<T> sum(Function<T, Number> getter) {
        return new SelectColumn<>() {
            @Override
            public String getName() {
                return "sum";
            }

            @Override
            public Object getValue(List<? extends T> groupItems) {
                if (groupItems == null || groupItems.isEmpty()) {
                    return 0.0;
                }
                return groupItems.stream()
                        .mapToDouble(e -> getter.apply(e).doubleValue())
                        .sum();
            }

            @Override
            public boolean isAggregation() {
                return true;
            }
        };
    }

    public static <T> SelectColumn<T> count() {
        return new SelectColumn<>() {
            @Override
            public String getName() {
                return "count";
            }

            @Override
            public Object getValue(List<? extends T> groupItems) {
                if (groupItems == null) {
                    return 0;
                }
                return groupItems.size();
            }

            @Override
            public boolean isAggregation() {
                return true;
            }
        };
    }
}

