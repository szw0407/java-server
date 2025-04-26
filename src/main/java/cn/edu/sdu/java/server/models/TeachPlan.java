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

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Teacher teacher;

    private Integer semester;
    private Integer year;

    private Float grade;   // 老师被评价的得分

    @OneToMany(mappedBy = "teachPlan", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Score> scores;

}
