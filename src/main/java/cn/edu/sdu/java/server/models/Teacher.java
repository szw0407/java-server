package cn.edu.sdu.java.server.models;


/*
 * Teacher 数据模型，对应数据库中的teacher表
 * Integer personId 人员表 person 主键 person_id
 * String degree 学位
 * String title 职称
 * Date enterTime 入职时间
 * Integer studentNum 学生人数
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
@Table(	name = "teacher",
        uniqueConstraints = {
        })
public class Teacher  {
    @Id
        private Integer personId;

        @OneToOne
        @JoinColumn(name="person_id")
        @JsonIgnore
        private Person person;

        @Size(max = 50)
        private String degree;

        @Size(max = 50)
//        @NotBlank
        private String title;


        private Date enterTime;

        private Integer studentNum;


}
