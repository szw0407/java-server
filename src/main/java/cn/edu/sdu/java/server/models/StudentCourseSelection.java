package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 学生选课记录实体类
 * 记录学生选择的教学班级信息
 */
@Getter
@Setter
@Entity
@Table(name = "student_course_selection",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"person_id", "teach_plan_id"})
        })
public class StudentCourseSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer selectionId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teach_plan_id")
    private TeachPlan teachPlan;

    // 选课时间
    private Date selectionTime;

    // 选课状态：SELECTED-已选, CONFIRMED-确认, WITHDRAWN-已退选
    @Column(length = 20)
    private String status;

    // 是否获得学分
    private Boolean creditEarned;

    // 备注
    @Column(length = 200)
    private String remark;
}