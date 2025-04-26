package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.AcademicCompetition;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AcademicCompetitionRepository extends JpaRepository<AcademicCompetition,Integer>{
    //上述<AcademicCompetition,Integer>的<Integer>是主键类型
    @Query("FROM AcademicCompetition WHERE student.personId = ?1 ") //精确查询
    Optional<AcademicCompetition> findByStudentId(Integer studentId);

    @Query(value = "FROM AcademicCompetition  WHERE  id = ?1")
    Optional<AcademicCompetition> findById(Integer id); //通过id查询

    @Query(value = "FROM AcademicCompetition  WHERE ?1 = '' or subject LIKE %?1% OR achievement LIKE %?1%")
    List<AcademicCompetition> findAcademicByNumName(String numName);//通过相关的字查询,模糊查询


}//PersonName和numName区别？