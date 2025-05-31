package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentHonor;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentHonorRepository extends JpaRepository<StudentHonor, Integer> {
    // 通过关联的Student对象查询
    List<StudentHonor> findByStudent(Student student);
    
    // 通过学生ID查询（使用关联关系）
    List<StudentHonor> findByStudentPersonId(Integer personId);
    
    // 保持向后兼容的方法
    @Query("FROM StudentHonor h WHERE h.student.personId = ?1")
    List<StudentHonor> findByStudentId(Integer studentId);
    
    // 支持根据学生姓名或荣誉标题进行模糊查询
    @Query("FROM StudentHonor h WHERE ?1 = '' OR h.title LIKE %?1% OR h.description LIKE %?1% OR h.student.person.name LIKE %?1%")
    List<StudentHonor> findByNumName(String numName);
}