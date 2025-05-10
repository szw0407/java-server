package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.TeachPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeachPlanRepository extends JpaRepository<TeachPlan, Integer> {
    
    /**
     * 根据教师ID查询教学计划
     */
    List<TeachPlan> findByTeacherPersonId(Integer teacherId);
    
    /**
     * 根据教学班级ID查询教学计划
     */
    List<TeachPlan> findByClassScheduleClassScheduleId(Integer classScheduleId);
    
    /**
     * 查询教师在特定学期的教学计划
     */
    @Query("from TeachPlan tp where tp.teacher.personId = ?1 and tp.classSchedule.semester = ?2 and tp.classSchedule.year = ?3")
    List<TeachPlan> findTeacherSemesterPlans(Integer teacherId, String semester, String year);
    
    /**
     * 查询特定教学班级是否已分配给特定教师
     */
    Optional<TeachPlan> findByTeacherPersonIdAndClassScheduleClassScheduleId(Integer teacherId, Integer classScheduleId);
}
