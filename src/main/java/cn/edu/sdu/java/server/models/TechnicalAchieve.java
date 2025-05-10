package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "technical_achieve",uniqueConstraints = {})

public class TechnicalAchieve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "person_id")
    @JsonIgnore
    private Student student;

    @Size(max = 20)
    @Column(name = "subject")
    private String subject;

    @Size(max = 100)
    @Column(name = "description")
    private String description;

    @Size(max = 20)
    @Column(name = "achievement")
    private String achievement;


}
