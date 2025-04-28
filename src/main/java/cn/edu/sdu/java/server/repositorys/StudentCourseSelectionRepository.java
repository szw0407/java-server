package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentCourseSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生选课数据访问接口
 */
@Repository
public interface StudentCourseSelectionRepository extends JpaRepository<StudentCourseSelection, Integer> {
    
    // 查询学生是否已经选择了某个教学班级
    Optional<StudentCourseSelection> findByStudent_PersonIdAndTeachPlan_TeachPlanId(Integer studentId, Integer teachPlanId);
    
    // 查询学生选择的所有课程
    List<StudentCourseSelection> findByStudent_PersonId(Integer studentId);
    
    // 查询某个学期学生选择的所有课程
    @Query("from StudentCourseSelection scs where scs.student.personId = ?1 and scs.teachPlan.year = ?2 and scs.teachPlan.semester = ?3 and scs.status <> 'WITHDRAWN'")
    List<StudentCourseSelection> findByStudentAndYearAndSemester(Integer studentId, Integer year, Integer semester);
    
    // 查询某个教学班级的所有选课学生
    List<StudentCourseSelection> findByTeachPlan_TeachPlanIdAndStatusNot(Integer teachPlanId, String status);
    
    // 查询学生是否已选择同一课程的其他教学班级
    @Query("from StudentCourseSelection scs where scs.student.personId = ?1 and scs.teachPlan.course.courseId = ?2 and scs.teachPlan.year = ?3 and scs.teachPlan.semester = ?4 and scs.status <> 'WITHDRAWN'")
    List<StudentCourseSelection> findBySameCourseDifferentClass(Integer studentId, Integer courseId, Integer year, Integer semester);
    
    // 统计某个教学班已选人数
    @Query("select count(scs) from StudentCourseSelection scs where scs.teachPlan.teachPlanId = ?1 and scs.status <> 'WITHDRAWN'")
    Integer countStudentsByTeachPlanId(Integer teachPlanId);
}