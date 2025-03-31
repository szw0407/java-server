package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "teacher", uniqueConstraints = {})
public class Teacher {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name = "personId")
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String title;

    @Size(max = 50)
    private String degree;

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public @Size(max = 20) String getTitle() {
        return title;
    }

    public void setTitle(@Size(max = 20) String title) {
        this.title = title;
    }

    public @Size(max = 50) String getDegree() {
        return degree;
    }

    public void setDegree(@Size(max = 50) String degree) {
        this.degree = degree;
    }
}
