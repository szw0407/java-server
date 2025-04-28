package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.TeacherTeachPlanRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeacherTeachPlanRoleRepository extends JpaRepository<TeacherTeachPlanRole, Integer> {
    
    // 根据教学计划ID查询关联的教师角色
    List<TeacherTeachPlanRole> findByTeachPlan_TeachPlanId(Integer teachPlanId);
    
    // 根据教师ID查询关联的教学计划角色
    List<TeacherTeachPlanRole> findByTeacher_PersonId(Integer personId);
    
    // 查询特定教师在特定教学计划中的角色
    TeacherTeachPlanRole findByTeacher_PersonIdAndTeachPlan_TeachPlanId(Integer personId, Integer teachPlanId);
    
    // 查询特定学年学期的教师教学任务
    @Query("from TeacherTeachPlanRole tpr where tpr.teacher.personId = ?1 and tpr.teachPlan.year = ?2 and tpr.teachPlan.semester = ?3")
    List<TeacherTeachPlanRole> findByTeacherAndYearAndSemester(Integer personId, Integer year, Integer semester);
}