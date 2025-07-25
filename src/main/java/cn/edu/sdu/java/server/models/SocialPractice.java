package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

@Getter
@Setter
@Entity
@Table(name = "social_practice",
        uniqueConstraints = {
                // 学生同时间段只能有一个实践记录
                @UniqueConstraint(columnNames = {"student_id", "practice_time"})
        })
public class SocialPractice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 独立自增主键


    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "person_id")
    @JsonIgnore
    private Student student;


    @Column(name = "practice_time")
    private String practiceTime;  // 日期类型

    @Size(max = 100)
    @Column(name = "location")
    private String practiceLocation; // 实践地点

    @Size(max = 100)
    @Column(name = "organization")
    private String practiceOrganization; // 实践单位

    @Size(max = 500)
    @Column(name = "description")
    private String practiceDescription; // 实践描述


    @Column(name = "duration_days")
    private Integer durationDays; // 实践天数


}