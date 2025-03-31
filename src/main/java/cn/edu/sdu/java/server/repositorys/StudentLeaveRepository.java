
package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentLeaveRepository extends JpaRepository<StudentLeave, Integer> {
    List<StudentLeave> findByStudentPersonId(Integer personId);
}