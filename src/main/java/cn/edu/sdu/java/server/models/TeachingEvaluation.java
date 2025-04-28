package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 教学评价实体类
 * 记录学生对教师教学的评价信息
 */
@Getter
@Setter
@Entity
@Table(name = "teaching_evaluation",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"student_id", "teach_plan_id"})
       })
public class TeachingEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evaluationId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teach_plan_id")
    private TeachPlan teachPlan;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // 教学内容评分（1-5分）
    @Min(1)
    @Max(5)
    private Integer contentScore;

    // 教学方法评分（1-5分）
    @Min(1)
    @Max(5)
    private Integer methodScore;

    // 教学态度评分（1-5分）
    @Min(1)
    @Max(5)
    private Integer attitudeScore;

    // 总体评分（1-5分）
    @Min(1)
    @Max(5)
    private Integer overallScore;

    // 评价内容
    @Size(max = 500)
    @Column(length = 500)
    private String comment;

    // 是否匿名
    private Boolean isAnonymous;

    // 评价时间
    private Date evaluationTime;

    // 状态：SUBMITTED-已提交, APPROVED-已审核, REJECTED-已拒绝
    @Size(max = 20)
    @Column(length = 20)
    private String status;
}