package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.TeachPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeachPlanRepository extends JpaRepository<TeachPlan, Integer> {

    List<TeachPlan> findByCourse_CourseId(Integer courseCourseId);

    // 删除不再适用的方法
    // List<TeachPlan> findByTeacher_id(Integer teacherID);

    // 修改查询，移除教师关联条件
    @Query("from TeachPlan where (?1=0 or course.courseId=?1) and (?3=0 or semester=?3) and (?4=0 or year=?4)")
    List<TeachPlan> filterByCourseTeacherSemesterYear(Integer courseId, Integer teacherId, Integer semester, Integer year);

    // 添加新方法，根据学年和学期查询教学计划
    @Query("from TeachPlan where (?1=0 or year=?1) and (?2=0 or semester=?2)")
    List<TeachPlan> findByYearAndSemester(Integer year, Integer semester);
    
    // 添加新方法，根据课程、学年、学期和教学班代码查询教学计划
    @Query("from TeachPlan where course.courseId=?1 and year=?2 and semester=?3 and teachPlanCode=?4")
    List<TeachPlan> findByCourseAndYearAndSemesterAndTeachPlanCode(Integer courseId, Integer year, Integer semester, Integer teachPlanCode);

    List<TeachPlan> findByCourseAndTeachPlanCode(Course course, Integer teachPlanCode);
}
