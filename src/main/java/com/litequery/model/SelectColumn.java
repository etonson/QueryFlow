package com.litequery.model;

import java.util.List;

/**
 * @Author: Eton.Lin
 * @Description: SelectColumn 介面，用於 select(...) 欄位定義
 * @Date: 2025/12/8 下午 11:39
*/
public interface SelectColumn<T> {
    String getName();
    Object getValue(List<? extends T> groupItems);
    default boolean isAggregation() { return false; }

    default SelectColumn<T> as(String newName) {
        SelectColumn<T> original = this;
        return new SelectColumn<>() {
            @Override
            public String getName() {
                return newName;
            }

            @Override
            public Object getValue(List<? extends T> groupItems) {
                return original.getValue(groupItems);
            }

            @Override
            public boolean isAggregation() {
                return original.isAggregation();
            }
        };
    }
}

