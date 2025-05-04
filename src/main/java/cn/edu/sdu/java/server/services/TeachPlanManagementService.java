package cn.edu.sdu.java.server.services;
/*
 * 这个service的目的是让学生选课
 * 这个文件安排的功能包括
 * 批量给学生排课（比如说按照班级）
 * 学生自主选课
 * 安排授课老师和教学计划
 *
 * 请务必写对课程的教学计划，先在教学计划里面保证课程的成立，然后在学生的成绩表格里面成绩留空，教学计划和课程号写正确
 */


import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.models.TeacherTeachPlanRole;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeachPlanManagementService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherTeachPlanRoleRepository teacherTeachPlanRoleRepository;

    public TeachPlanManagementService(
            CourseRepository courseRepository, 
            ScoreRepository scoreRepository, 
            TeachPlanRepository teachPlanRepository, 
            StudentRepository studentRepository, 
            TeacherRepository teacherRepository,
            TeacherTeachPlanRoleRepository teacherTeachPlanRoleRepository) {
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.teacherTeachPlanRoleRepository = teacherTeachPlanRoleRepository;
    }

    // managing course plans

    /**
     * 创建教学计划，并分配教师
     * 修改为使用新的TeacherTeachPlanRole实现多教师关联
     * @param courseId 课程ID
     * @param year 学年
     * @param semester 学期
     * @param teacherIds 教师ID列表
     * @param teachPlanCode 教学计划代码（可选）
     * @return 创建结果
     */
    public DataResponse createTeachPlanForClass(Integer courseId, Integer year, Integer semester, List<Integer> teacherIds, Integer teachPlanCode) {
        // check if the course exists
        var course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return CommonMethod.getReturnData("课程不存在");
        }
        var teachPlans = teachPlanRepository.filterByCourseTeacherSemesterYear(courseId, 0, semester, year);
        if (teachPlanCode != null) {
            // whether the one in plans.
            for (TeachPlan tp : teachPlans) {
                if (Objects.equals(tp.getTeachPlanCode(), teachPlanCode)) {
                    // 如果找到了相同代码的教学计划，则删除其现有的教师角色关联
                    List<TeacherTeachPlanRole> existingRoles = teacherTeachPlanRoleRepository.findByTeachPlan_TeachPlanId(tp.getTeachPlanId());
                    if (!existingRoles.isEmpty()) {
                        teacherTeachPlanRoleRepository.deleteAll(existingRoles);
                    }
                }
            }
        } else {
            // no specific teach plan code, but we need to create one with no conflict with existing ones
            teachPlanCode = 0;
            for (TeachPlan tp : teachPlans) {
                if (tp.getTeachPlanCode() > teachPlanCode) {
                    teachPlanCode = tp.getTeachPlanCode();
                }
            }
            teachPlanCode++;  // this is even larger than the maximum code. I think there should not be bugs
        }

        // 设置默认的学年学期（如果未提供）
        if (year == null || semester == null) {
            var calendar = Calendar.getInstance();

            if (year == null) {
                year = calendar.get(Calendar.YEAR);
            }
            if (semester == null) {
                semester = switch (calendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                         Calendar.JUNE -> 2;
                    case Calendar.JULY, Calendar.AUGUST -> 3;
                    default -> 1;
                };  // Just the case in SDU i suppose
            }
        }
        
        // 创建一个教学计划（不再为每个教师创建单独的教学计划）
        TeachPlan teachPlan = new TeachPlan();
        teachPlan.setCourse(course.get());
        teachPlan.setTeachPlanCode(teachPlanCode);
        teachPlan.setYear(year);
        teachPlan.setSemester(semester);
        teachPlan.setClassName(course.get().getName() + "-" + teachPlanCode);
        
        // 保存教学计划
        teachPlanRepository.save(teachPlan);
        
        // 添加教师角色关联
        List<Teacher> teachers = teacherRepository.findAllById(teacherIds);
        List<TeacherTeachPlanRole> teacherRoles = new ArrayList<>();
        Date now = new Date();
        
        // 为每个教师创建教师-教学计划-角色关联
        for (int i = 0; i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            String role = (i == 0) ? "主讲" : "助教"; // 默认第一个教师为主讲，其余为助教
            
            TeacherTeachPlanRole teacherRole = new TeacherTeachPlanRole();
            teacherRole.setTeachPlan(teachPlan);
            teacherRole.setTeacher(teacher);
            teacherRole.setRole(role);
            teacherRole.setCreateTime(now);
            teacherRole.setUpdateTime(now);
            
            teacherRoles.add(teacherRole);
        }
        
        // 保存教师角色关联
        if (!teacherRoles.isEmpty()) {
            teacherTeachPlanRoleRepository.saveAll(teacherRoles);
        }
        
        // 返回创建的教学计划信息
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("teachPlanId", teachPlan.getTeachPlanId());
        resultData.put("courseId", course.get().getCourseId());
        resultData.put("courseName", course.get().getName());
        resultData.put("teachPlanCode", teachPlanCode);
        resultData.put("year", year);
        resultData.put("semester", semester);
        resultData.put("teacherCount", teachers.size());
        
        return CommonMethod.getReturnData(resultData);
    }
    
    /**
     * 获取教师的所有教学计划
     * @param teacherId 教师ID
     * @param year 学年（可选）
     * @param semester 学期（可选）
     * @return 教学计划列表
     */
    public DataResponse getTeacherTeachPlans(Integer teacherId, Integer year, Integer semester) {
        // 检查教师是否存在
        if (!teacherRepository.existsById(teacherId)) {
            return CommonMethod.getReturnMessageError("教师不存在");
        }
        
        List<TeacherTeachPlanRole> roles;
        if (year != null && semester != null) {
            roles = teacherTeachPlanRoleRepository.findByTeacherAndYearAndSemester(teacherId, year, semester);
        } else {
            roles = teacherTeachPlanRoleRepository.findByTeacher_PersonId(teacherId);
        }
        
        List<Map<String, Object>> teachPlanList = new ArrayList<>();
        
        for (TeacherTeachPlanRole role : roles) {
            Map<String, Object> teachPlanInfo = new HashMap<>();
            TeachPlan teachPlan = role.getTeachPlan();
            
            teachPlanInfo.put("teachPlanId", teachPlan.getTeachPlanId());
            teachPlanInfo.put("courseId", teachPlan.getCourse().getCourseId());
            teachPlanInfo.put("courseName", teachPlan.getCourse().getName());
            teachPlanInfo.put("teachPlanCode", teachPlan.getTeachPlanCode());
            teachPlanInfo.put("year", teachPlan.getYear());
            teachPlanInfo.put("semester", teachPlan.getSemester());
            teachPlanInfo.put("role", role.getRole());
            teachPlanInfo.put("className", teachPlan.getClassName());
            
            teachPlanList.add(teachPlanInfo);
        }
        
        return CommonMethod.getReturnData(teachPlanList);
    }
}