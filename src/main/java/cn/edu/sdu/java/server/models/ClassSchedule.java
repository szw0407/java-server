package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 教学班级课程表实体类
 * 记录教学班级的上课时间、地点信息
 */
@Getter
@Setter
@Entity
@Table(name = "class_schedule")
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @ManyToOne
    @JoinColumn(name = "teach_plan_id")
    private TeachPlan teachPlan;

    // 星期几上课（1-7表示周一到周日）
    private Integer dayOfWeek;

    // 开始节次（1-12表示第1节到第12节）
    private Integer startPeriod;

    // 结束节次
    private Integer endPeriod;

    // 上课地点
    @Size(max = 100)
    private String location;

    // 教学楼
    @Size(max = 50)
    private String building;

    // 教室编号
    @Size(max = 20)
    private String roomNumber;

    // 创建时间
    private java.util.Date createTime;

    // 更新时间
    private java.util.Date updateTime;

    // 备注
    @Size(max = 200)
    private String remark;
}