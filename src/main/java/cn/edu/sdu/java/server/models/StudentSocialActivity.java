package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "student_social_activity")
public class StudentSocialActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动生成主键
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "personId", nullable = false) // 外键关联，不能为空
    @JsonIgnore
    private Student student;

    @Size(max = 50)
    @Column(nullable = false) // 活动名称不能为空
    private String name;

    @Size(max = 50)
    @Column(nullable = false) // 活动类型不能为空
    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime; // 活动开始时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime; // 活动结束时间

    @Size(max = 50)
    private String location; // 活动地点

    @Size(max = 50)
    private String description; // 活动描述

    @Size(max = 50)
    private String role; // 活动角色
}