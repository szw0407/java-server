package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 教师教学计划角色关联表
 * 用于表示教师在某个教学计划(教学班级)中的角色
 * 实现教师和教学计划之间的多对多关系
 */
@Getter
@Setter
@Entity
@Table(name = "teacher_teachplan_role",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"teacher_id", "teach_plan_id"})
        })
public class TeacherTeachPlanRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "teach_plan_id")
    private TeachPlan teachPlan;

    // 教师在该教学计划中的角色, 例如: 主讲、TA、助教等
    @Size(max = 50)
    private String role;

    // 创建时间
    private java.util.Date createTime;

    // 更新时间
    private java.util.Date updateTime;
}