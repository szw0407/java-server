package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 课程评价实体类
 * 记录学生对课程的评价信息
 */
@Getter
@Setter
@Entity
@Table(name = "course_evaluation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"person_id", "teach_plan_id"})
        })
public class CourseEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evaluationId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teach_plan_id")
    private TeachPlan teachPlan;

    // 评分（1-5分）
    @Min(1)
    @Max(5)
    private Integer rating;

    // 评价内容
    @Size(max = 1000)
    @Column(length = 1000)
    private String comment;

    // 评价时间
    private Date evaluationTime;

    // 评价状态（DRAFT-草稿, SUBMITTED-已提交, APPROVED-已审核）
    @Column(length = 20)
    private String status;
}