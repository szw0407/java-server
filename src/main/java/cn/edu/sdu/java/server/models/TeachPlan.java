package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import java.util.List;
/*
 * 教学计划
 *
 *
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

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Teacher teacher;

    private Integer semester;
    private Integer year;

    private Float grade;   // 老师被评价的得分，这个功能再说，没时间就不做了

    @OneToMany(mappedBy = "teachPlan", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Score> scores;

}
