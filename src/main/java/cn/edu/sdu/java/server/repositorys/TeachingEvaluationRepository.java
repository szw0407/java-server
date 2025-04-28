package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.TeachingEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教学评价数据访问接口
 */
@Repository
public interface TeachingEvaluationRepository extends JpaRepository<TeachingEvaluation, Integer> {
    
    // 查询学生是否已经评价了某个教学班级
    Optional<TeachingEvaluation> findByStudent_PersonIdAndTeachPlan_TeachPlanId(Integer studentId, Integer teachPlanId);
    
    // 查询学生对特定教师的评价
    Optional<TeachingEvaluation> findByStudent_PersonIdAndTeacher_PersonId(Integer studentId, Integer teacherId);
    
    // 查询特定教师的所有评价
    List<TeachingEvaluation> findByTeacher_PersonId(Integer teacherId);
    
    // 查询特定教学班级的所有评价
    List<TeachingEvaluation> findByTeachPlan_TeachPlanId(Integer teachPlanId);
    
    // 计算教师的平均评分
    @Query("SELECT AVG(e.overallScore) FROM TeachingEvaluation e WHERE e.teacher.personId = ?1")
    Double calculateAverageScore(Integer teacherId);
    
    // 计算教学班级的平均评分
    @Query("SELECT AVG(e.overallScore) FROM TeachingEvaluation e WHERE e.teachPlan.teachPlanId = ?1")
    Double calculateAverageScoreByTeachPlan(Integer teachPlanId);
    
    // 查询某学期某课程的所有评价
    @Query("FROM TeachingEvaluation e WHERE e.teachPlan.course.courseId = ?1 AND e.teachPlan.year = ?2 AND e.teachPlan.semester = ?3")
    List<TeachingEvaluation> findByCourseAndYearAndSemester(Integer courseId, Integer year, Integer semester);
}