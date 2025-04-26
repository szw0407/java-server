package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Internship;
import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface InternshipRepository extends JpaRepository<Internship,Integer>{
    //此处与下拉表单的联系？
    @Query(value="from Internship where id=?1" )
    Optional<Internship> findById(Integer id);
    @Query(value="from Internship where ?1=0 or student.personId=?1" )
    List<Internship> findByStudentId(Integer studentId);
    @Query(value="from Score where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
    List<Score> findByStudentCourse(Integer personId, Integer courseId);

    @Query(value="from Score where student.personId=?1 and (?2=0 or course.name like %?2%)" )
    List<Score> findByStudentCourse(Integer personId, String courseName);
}
