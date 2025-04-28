package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.CourseEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程评价数据访问接口
 */
@Repository
public interface CourseEvaluationRepository extends JpaRepository<CourseEvaluation, Integer> {
    
    // 查询学生对特定教学班的评价
    Optional<CourseEvaluation> findByStudent_PersonIdAndTeachPlan_TeachPlanId(Integer studentId, Integer teachPlanId);
    
    // 查询教学班的所有评价
    List<CourseEvaluation> findByTeachPlan_TeachPlanIdAndStatusOrderByEvaluationTimeDesc(Integer teachPlanId, String status);
    
    // 查询学生提交的所有评价
    List<CourseEvaluation> findByStudent_PersonIdAndStatus(Integer studentId, String status);
    
    // 计算教学班的评价平均分
    @Query("SELECT AVG(e.rating) FROM CourseEvaluation e WHERE e.teachPlan.teachPlanId = ?1 AND e.status = 'SUBMITTED'")
    Double calculateAverageRating(Integer teachPlanId);
    
    // 检查学生是否已评价该教学班
    boolean existsByStudent_PersonIdAndTeachPlan_TeachPlanIdAndStatus(Integer studentId, Integer teachPlanId, String status);
    
    // 查询教学班的评价统计
    @Query("SELECT COUNT(e), AVG(e.rating), AVG(e.contentRating), AVG(e.methodRating), AVG(e.attitudeRating) " +
           "FROM CourseEvaluation e WHERE e.teachPlan.teachPlanId = ?1 AND e.status = 'SUBMITTED'")
    Object[] getEvaluationStatistics(Integer teachPlanId);
}