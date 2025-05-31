package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "honor")
public class StudentHonor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // 建立与Student实体的外键关联
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "person_id")
    @JsonIgnore
    private Student student;
    
    // 保留原有的studentId字段用于向后兼容，但不再直接使用
    @Column(name = "student_id", insertable = false, updatable = false)
    private Integer studentId;
    
    private String title;
    private String description;
    
    // 添加便捷方法来获取学生ID
    public Integer getStudentId() {
        return student != null ? student.getPersonId() : studentId;
    }
    
    // 添加便捷方法来设置学生
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
        // 注意：这里需要在Service层设置对应的Student对象
    }
}