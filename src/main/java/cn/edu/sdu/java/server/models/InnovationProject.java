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
@Table(name = "innovation_project",
        uniqueConstraints = {})
public class InnovationProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 独立自增主键

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "personId")
    @JsonIgnore
    private Student student;


    @Size(max = 100)
    @Column(name = "type")
    private String type;

    @Column(name = "time")
    private String time;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

}
