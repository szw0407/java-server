package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

@Getter
@Setter
@Entity
@Table(name = "training",
        uniqueConstraints = {})
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "person_id")
    @JsonIgnore
    private Student student;

    @Column(name = "time")
    private String time;

    @Size(max = 100)
    @Column(name = "location")
    private String location;

    @Size(max = 20)
    @Column(name = "theme")
    private String theme;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

}
