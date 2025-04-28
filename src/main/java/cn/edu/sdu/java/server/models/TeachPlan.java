package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import java.util.List;
/*
 * 教学计划
 * 代表一个教学班级的实例，关联到特定的课程、学年和学期
 */
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "teach_plan",
        uniqueConstraints = {
        })
public class TeachPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teachPlanId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // 课程计划的鉴别码，用于区分开设相同课程的不同教学班
    private Integer teachPlanCode;

    // 移除单一教师关联，改为通过TeacherTeachPlanRole实现多教师
    // @ManyToOne
    // @JoinColumn(name = "person_id")
    // private Teacher teacher;
    
    // 教学班名称
    private String className;
    
    // 最大学生人数
    private Integer maxStudentCount;

    private Integer semester;
    private Integer year;

    private Float grade;   // 老师被评价的得分，这个功能再说，没时间就不做了

    @OneToMany(mappedBy = "teachPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Score> scores;
    
    // 添加与TeacherTeachPlanRole的一对多关系
    @OneToMany(mappedBy = "teachPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherTeachPlanRole> teacherRoles;
    
    // 添加与ClassSchedule的一对多关系，表示教学班级的上课安排
    @OneToMany(mappedBy = "teachPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassSchedule> schedules;
}
