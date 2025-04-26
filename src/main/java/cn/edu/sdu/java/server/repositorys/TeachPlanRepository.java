package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.TeachPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeachPlanRepository extends JpaRepository<TeachPlan, Integer> {

    List<TeachPlan> findByCourse_CourseId(Integer courseCourseId);

    // Filter by a teacher
    List<TeachPlan> findByTeacher_id(Integer teacherID);

    // Filter by course, semester, year, teacher
    @Query("from TeachPlan where (?1=0 or course.courseId=?1) and (?2=0 or teacher.personId=?2) and (?3=0 or semester=?3) and (?4=0 or year=?4)")
    List<TeachPlan> filterByCourseTeacherSemesterYear(Integer courseId, Integer teacherId, Integer semester, Integer year);


    List<TeachPlan> findByCourseAndTeachPlanCode(Course course, Integer teachPlanCode);
}
