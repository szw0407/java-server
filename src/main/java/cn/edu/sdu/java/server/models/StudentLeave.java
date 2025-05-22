package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "student_leave")
public class StudentLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 主键ID

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false) // 外键关联到 Student 表
    private Student student; // 学生实体

    @Column(nullable = false, length = 50)
    private String studentName; // 学生姓名

    @Column(nullable = false, length = 50)
    private String college; // 学院

    @Column(nullable = false)
    private Date startDate; // 请假开始时间

    @Column(nullable = false)
    private Date endDate;

    @Column(length = 500)
    private String reason; // 请假原因

    @Column
    private Integer approverId; // 审批人ID

    @Column
    private Boolean isApproved; // 是否审批
}