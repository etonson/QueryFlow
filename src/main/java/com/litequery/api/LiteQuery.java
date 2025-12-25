package com.litequery.api;

import com.litequery.builder.QueryBuilder;
import java.util.List;

/**
 *   @Author: Eton.Lin
 *   @Description: LiteQuery DSL 的入口點
 *   @Date: 2025/12/8 下午 11:42
*/
public class LiteQuery {
    public static <T> QueryBuilder<T> from(List<T> list) {
        return new QueryBuilder<>(list);
    }
}


