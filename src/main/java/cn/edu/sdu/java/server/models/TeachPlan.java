package cn.edu.sdu.java.server.models;
import jakarta.persistence.*;
/*
 * Score 成绩表实体类  保存成绩的的基本信息信息，
 * Integer scoreId 人员表 score 主键 score_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Course course 关联课程 course_id 关联课程的主键 course_id
 * Integer mark 成绩
 * Integer ranking 排名
 */
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(	name = "teach_plan",
        uniqueConstraints = {
        })
public class TeachPlan {

    /*
     * 教学班级
     *
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teachPlanId;

    // bind teach class
    @ManyToOne
    @JoinColumn(name = "teaching_class_id")
    private TeachingClass teachingClass;

    // bind teacher
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
