package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.TeachPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * ClassSchedule 数据操作接口，主要实现教学班级的查询操作
 */
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer> {
    
    /**
     * 根据学期和年份查询课程表
     */
    List<ClassSchedule> findBySemesterAndYear(String semester, String year);
    
    /**
     * 根据课程ID查询课程表
     */
    List<ClassSchedule> findByCourse_CourseId(Integer courseId);
    
    /**
     * 查询当前最新的学期课程表
     */
    @Query("from ClassSchedule where semester = ?1 and year = ?2")
    List<ClassSchedule> findCurrentSemesterClasses(String semester, String year);
    
    /**
     * 根据班号查找教学班级
     */
    Optional<ClassSchedule> findByClassNumberAndSemesterAndYearAndCourse_CourseId(Integer classNumber, String semester, String year, Integer courseId);

    List<ClassSchedule> findByCourse_NumAndYearAndSemester(String courseNum, String year, String semester);

    Optional<ClassSchedule> findByClassScheduleId(Integer id);
}
