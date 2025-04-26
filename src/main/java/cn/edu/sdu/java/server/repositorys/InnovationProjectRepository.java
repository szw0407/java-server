package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.InnovationProject;
import cn.edu.sdu.java.server.models.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InnovationProjectRepository extends JpaRepository<InnovationProject,Integer>{
    @Query(value="from InnovationProject where id =?1" )
    Optional<InnovationProject> findById(Integer id);
    @Query(value="from InnovationProject where ?1=0 or student.personId=?1" )
    List<InnovationProject> findByStudentId(Integer studentId);
}
