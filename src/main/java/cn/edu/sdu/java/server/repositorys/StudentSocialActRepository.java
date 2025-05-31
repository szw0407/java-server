package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentSocialActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSocialActRepository extends JpaRepository<StudentSocialActivity, Integer> {
    // 这里可以添加自定义查询方法
    // 例如：List<StudentSocialAct> findBySomeField(String someField);

    List<StudentSocialActivity> findByname(String name);
    List<StudentSocialActivity> findByStudentPersonId(Integer personId);


    List<StudentSocialActivity> findByStudentPersonNum(String studentId);
}
