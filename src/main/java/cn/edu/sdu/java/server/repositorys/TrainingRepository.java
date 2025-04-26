package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.InnovationProject;
import cn.edu.sdu.java.server.models.Training;
import cn.edu.sdu.java.server.models.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training,Integer>{
    @Query(value="from Training where id =?1" )
    Optional<Training> findById(Integer id);
    @Query(value="from Training where ?1=0 or student.personId=?1" )
    List<Training> findByStudentId(Integer studentId);
}
