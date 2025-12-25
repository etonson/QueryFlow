package com.litequery.function;

import com.litequery.model.SelectColumn;
import java.util.List;
import java.util.function.Function;

/**
 * @Author: Eton.Lin
 * @Description: 欄位選擇工具
 * @Date: 2025/12/8 下午 11:41
*/
public class Columns {
    public static <T> SelectColumn<T> col(String name, Function<? super T, ?> getter) {
        return new SelectColumn<>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Object getValue(List<? extends T> groupItems) {
                if (groupItems == null || groupItems.isEmpty()) {
                    return null;
                }
                return getter.apply(groupItems.getFirst());
            }
        };
    }
}

