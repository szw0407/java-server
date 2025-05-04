package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.TeachPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachPlanRepository extends JpaRepository<TeachPlan, Integer> {
    List<TeachPlan> findByTeachingClassTeachingClassId(Integer teachingClassId);

    List<TeachPlan> findByTeacherPersonId(Integer teacherPID);

    List<TeachPlan> findByTeachingClassTeachingClassIdAndTeacherPersonId(Integer teachingClassId, Integer teacherId);

    List<TeachPlan> findTeachPlansByTeachingClassTeachingClassId(Integer teachingClassId);
}
