package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentHonor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentHonorRepository extends JpaRepository<StudentHonor, Integer> {
    List<StudentHonor> findByStudentId(Integer studentId);
}