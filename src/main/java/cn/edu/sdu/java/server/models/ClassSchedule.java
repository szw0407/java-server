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
@Table(	name = "teaching_class",
        uniqueConstraints = {
        })
public class ClassSchedule {

    /*
    * 教学班级
    *
    * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classScheduleId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    private Integer classNumber;

    private String semester;
    private String year;

    // 上课的时间和地点
    private String classTime;
    private String classLocation;

    // 上课的老师
    @ManyToMany(mappedBy = "teachingClass")
    @JoinTable(name = "teach_plan",
            joinColumns = @JoinColumn(name = "teach_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> teachers;

    // 选课的学生
    @OneToMany(mappedBy = "teachingClass")
    @JoinTable(
            name = "score",
            joinColumns = @JoinColumn(name = "teach_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")

    )
    private List<Student> students;

}
