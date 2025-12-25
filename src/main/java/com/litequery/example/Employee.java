package com.litequery.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Eton.Lin
 * @Description: 示例用的 Employee 模型
 * @Date: 2025/12/8 下午 11:43
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private int id;
    private int departmentId;
    private int salary;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                departmentId == employee.departmentId &&
                salary == employee.salary;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, departmentId, salary);
    }
}

