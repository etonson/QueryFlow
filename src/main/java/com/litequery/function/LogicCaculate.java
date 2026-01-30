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
     * 支援通配符模式：
     * - "A*" 表示以 A 開頭（前綴匹配）
     * - "*A" 表示以 A 結尾（後綴匹配）
     * - "*A*" 表示包含 A（包含匹配）
     * - "A" 表示精確匹配
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值集合（支援 String 類型的通配符模式）
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值不匹配集合中任一值則返回真
     */
    public static <T, V> Predicate<T> notIn(Function<T, V> fieldExtractor, Collection<V> values) {
        if (values == null || values.isEmpty()) {
            return e -> true;
        }
        return item -> {
            V fieldValue = fieldExtractor.apply(item);
            if (fieldValue == null) {
                return true;
            }
            for (V value : values) {
                if (matchesPattern(fieldValue, value)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * 使用可變參數建立 NOT IN 述詞
     * 支援通配符模式：
     * - "A*" 表示以 A 開頭（前綴匹配）
     * - "*A" 表示以 A 結尾（後綴匹配）
     * - "*A*" 表示包含 A（包含匹配）
     * - "A" 表示精確匹配
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值（支援 String 類型的通配符模式）
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值不匹配給定的值中任一項則返回真
     */
    @SafeVarargs
    public static <T, V> Predicate<T> notIn(Function<T, V> fieldExtractor, V... values) {
        if (values == null || values.length == 0) {
            return e -> true;
        }
        return notIn(fieldExtractor, Arrays.asList(values));
    }

    /**
     * 建立等於述詞 - 檢查欄位值是否等於給定的值
     * 支援通配符模式：
     * - "A*" 表示以 A 開頭（前綴匹配）
     * - "*A" 表示以 A 結尾（後綴匹配）
     * - "*A*" 表示包含 A（包含匹配）
     * - "A" 表示精確匹配
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param value 要比較的值（支援 String 類型的通配符模式）
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值匹配給定的值則返回真
     */
    public static <T, V> Predicate<T> eq(Function<T, V> fieldExtractor, V value) {
        if (value == null) {
            return item -> fieldExtractor.apply(item) == null;
        }
        return item -> {
            V fieldValue = fieldExtractor.apply(item);
            return matchesPattern(fieldValue, value);
        };
    }

    /**
     * 建立不等於述詞 - 檢查欄位值是否不等於給定的值
     * 支援通配符模式：
     * - "A*" 表示不以 A 開頭
     * - "*A" 表示不以 A 結尾
     * - "*A*" 表示不包含 A
     * - "A" 表示不等於 A
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param value 要比較的值（支援 String 類型的通配符模式）
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值不匹配給定的值則返回真
     */
    public static <T, V> Predicate<T> neq(Function<T, V> fieldExtractor, V value) {
        return eq(fieldExtractor, value).negate();
    }

    /**
     * 建立 IN 述詞 - 檢查欄位值是否在給定的集合中
     * 支援通配符模式：
     * - "A*" 表示以 A 開頭（前綴匹配）
     * - "*A" 表示以 A 結尾（後綴匹配）
     * - "*A*" 表示包含 A（包含匹配）
     * - "A" 表示精確匹配
     * @param fieldExtractor 從物件提取欄位值的函數
     * @param values 要檢查的值集合（支援 String 類型的通配符模式）
     * @param <T> 物件的類型
     * @param <V> 欄位值的類型
     * @return 述詞，如果欄位值匹配集合中任一值則返回真
     */
    public static <T, V> Predicate<T> in(Function<T, V> fieldExtractor, Collection<V> values) {
        if (values == null || values.isEmpty()) {
            return e -> false;
        }
        return item -> {
            V fieldValue = fieldExtractor.apply(item);
            if (fieldValue == null) {
                return false;
            }
            for (V value : values) {
                if (matchesPattern(fieldValue, value)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 檢查欄位值是否匹配模式（支援通配符）
     * @param fieldValue 欄位值
     * @param pattern 模式（可能包含 * 通配符）
     * @param <V> 值的類型
     * @return 如果匹配則返回真
     */
    private static <V> boolean matchesPattern(V fieldValue, V pattern) {
        if (pattern == null) {
            return fieldValue == null;
        }
        if (fieldValue == null) {
            return false;
        }

        // 如果是 String 類型，支援通配符匹配
        if (pattern instanceof String && fieldValue instanceof String) {
            String patternStr = (String) pattern;
            String valueStr = (String) fieldValue;

            boolean startsWithWildcard = patternStr.startsWith("*");
            boolean endsWithWildcard = patternStr.endsWith("*");

            if (startsWithWildcard && endsWithWildcard && patternStr.length() > 1) {
                // *A* 模式：包含匹配
                String middle = patternStr.substring(1, patternStr.length() - 1);
                return valueStr.contains(middle);
            } else if (startsWithWildcard) {
                // *A 模式：後綴匹配
                String suffix = patternStr.substring(1);
                return valueStr.endsWith(suffix);
            } else if (endsWithWildcard) {
                // A* 模式：前綴匹配
                String prefix = patternStr.substring(0, patternStr.length() - 1);
                return valueStr.startsWith(prefix);
            }
        }

        // 非 String 類型或無通配符：精確匹配
        return fieldValue.equals(pattern);
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

