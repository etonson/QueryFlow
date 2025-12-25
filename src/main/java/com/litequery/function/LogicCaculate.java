package com.litequery.function;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @Author: Eton.Lin
 * @Description: 用於述詞操作的邏輯計算工具
 * @Date: 2025/12/15 下午 02:49
*/
public class LogicCaculate {

    /**
     * 使用 AND 邏輯組合多個述詞（交集）
     * @param predicates 要組合的述詞
     * @param <T> 述詞輸入的類型
     * @return 組合的述詞，只有當所有述詞都返回真時才返回真
     */
    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return e -> true;
        }
        return Arrays.stream(predicates)
                .reduce(Predicate::and)
                .orElse(e -> true);
    }

    /**
     * 使用 OR 邏輯組合多個述詞（並集）
     * @param predicates 要組合的述詞
     * @param <T> 述詞輸入的類型
     * @return 組合的述詞，如果任何述詞返回真則返回真
     */
    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return e -> false;
        }
        return Arrays.stream(predicates)
                .reduce(Predicate::or)
                .orElse(e -> false);
    }

    /**
     * 建立 NOT IN 述詞 - 檢查欄位值是否不在給定的集合中
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值集合
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值不在集合中則返回真
     */
    public static <T, V> Predicate<T> notIn(Function<T, V> fieldExtractor, Collection<V> values) {
        if (values == null || values.isEmpty()) {
            return e -> true;
        }
        return item -> !values.contains(fieldExtractor.apply(item));
    }

    /**
     * 使用可變參數建立 NOT IN 述詞
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值不在給定的值中則返回真
     */
    @SafeVarargs
    public static <T, V> Predicate<T> notIn(Function<T, V> fieldExtractor, V... values) {
        if (values == null || values.length == 0) {
            return e -> true;
        }
        return notIn(fieldExtractor, Arrays.asList(values));
    }

    /**
     * 建立 IN 述詞 - 檢查欄位值是否在給定的集合中
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值集合
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值在集合中則返回真
     */
    public static <T, V> Predicate<T> in(Function<T, V> fieldExtractor, Collection<V> values) {
        if (values == null || values.isEmpty()) {
            return e -> false;
        }
        return item -> values.contains(fieldExtractor.apply(item));
    }

    /**
     * 使用可變參數建立 IN 述詞
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值在給定的值中則返回真
     */
    @SafeVarargs
    public static <T, V> Predicate<T> in(Function<T, V> fieldExtractor, V... values) {
        if (values == null || values.length == 0) {
            return e -> false;
        }
        return in(fieldExtractor, Arrays.asList(values));
    }
}

