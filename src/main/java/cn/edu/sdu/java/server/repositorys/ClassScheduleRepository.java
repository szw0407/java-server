package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程时间安排数据访问接口
 */
@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer> {
    
    // 根据教学计划ID查询所有课程安排
    List<ClassSchedule> findByTeachPlan_TeachPlanId(Integer teachPlanId);
    
    // 根据教学计划ID和上课日期查询课程安排
    List<ClassSchedule> findByTeachPlan_TeachPlanIdAndDayOfWeek(Integer teachPlanId, Integer dayOfWeek);
    
    // 查询特定课程、特定学年学期的所有课程安排
    @Query("from ClassSchedule cs where cs.teachPlan.course.courseId = ?1 and cs.teachPlan.year = ?2 and cs.teachPlan.semester = ?3")
    List<ClassSchedule> findByCourseAndYearAndSemester(Integer courseId, Integer year, Integer semester);
    
    // 查询可能冲突的教室安排（同一天、同一时间段、同一教室）
    @Query("from ClassSchedule cs where cs.dayOfWeek = ?1 and cs.building = ?2 and cs.roomNumber = ?3 " +
           "and ((cs.startPeriod <= ?4 and cs.endPeriod >= ?4) or (cs.startPeriod <= ?5 and cs.endPeriod >= ?5) " +
           "or (cs.startPeriod >= ?4 and cs.endPeriod <= ?5))")
    List<ClassSchedule> findConflictingRoomSchedules(Integer dayOfWeek, String building, String roomNumber, 
                                                   Integer startPeriod, Integer endPeriod);
    
    // 查询教师在特定时间段的所有课程安排
    @Query("from ClassSchedule cs where cs.teachPlan.teachPlanId in " +
           "(select tpr.teachPlan.teachPlanId from TeacherTeachPlanRole tpr where tpr.teacher.personId = ?1) " +
           "and cs.dayOfWeek = ?2 and ((cs.startPeriod <= ?3 and cs.endPeriod >= ?3) " +
           "or (cs.startPeriod <= ?4 and cs.endPeriod >= ?4) or (cs.startPeriod >= ?3 and cs.endPeriod <= ?4))")
    List<ClassSchedule> findTeacherScheduleConflicts(Integer teacherId, Integer dayOfWeek, 
                                                    Integer startPeriod, Integer endPeriod);
}