package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentLeave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface StudentLeaveRepository extends JpaRepository<StudentLeave, Integer> {

    // 根据学生的 personId 查询请假记录
    List<StudentLeave> findByStudentPersonPersonId(Integer personId);

    // 根据审批人ID查询请假记录
    List<StudentLeave> findByApproverId(Integer approverId);

    // 根据学生姓名模糊查询请假记录（通过Person关联）
    @Query("from StudentLeave where :name = '' or student.person.name like %:name%")
    List<StudentLeave> findLeaveListByStudentName(@Param("name") String name);

    // 分页查询请假记录（通过Person关联）
    @Query(value = "from StudentLeave where :name = '' or student.person.name like %:name%",
            countQuery = "select count(id) from StudentLeave where :name = '' or student.person.name like %:name%")
    Page<StudentLeave> findLeavePageByStudentName(@Param("name") String name, Pageable pageable);

    // 根据学生的 personId 和审批状态查询请假记录
    List<StudentLeave> findByStudentPersonPersonIdAndIsApproved(Integer personId, Boolean isApproved);

    // 根据请假开始时间范围查询请假记录
    @Query("from StudentLeave where startDate >= ?1 and startDate <= ?2")
    List<StudentLeave> findByStartDateBetween(Date startDate, Date endDate);
}