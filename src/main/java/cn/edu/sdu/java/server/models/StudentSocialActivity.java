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
    private Integer id;

//    private Integer studentId;
    @ManyToOne
    @JoinColumn(name = "personId")
    @JsonIgnore
    private Student student;

    @Size(max = 50)
    private String name;//活动名称
    @Size(max = 50)
    private String type;//活动类型

    private Date startTime;//活动开始时间

    private Date endTime;//活动结束时间

    @Size(max = 50)
    private String location;//活动地点
    @Size(max = 50)
    private String description;//活动描述
    @Size(max = 50)
    private String role;//活动角色

}
