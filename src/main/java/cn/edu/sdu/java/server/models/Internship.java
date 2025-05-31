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
@Table(name = "internship",uniqueConstraints = {})
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 独立自增主键


    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "student_id", referencedColumnName = "person_id")
    @JsonIgnore
    private Student student;

    @Column(name = "start_time")
    private String startTime;  //

    @Column(name = "end_time")
    private String endTime;

    @Size(max = 100)
    @Column(name = "position")
    private String position; //

    @Size(max = 100)
    @Column(name = "company")
    private String company;

    @Size(max = 500)
    @Column(name = "description")
    private String description;


}
