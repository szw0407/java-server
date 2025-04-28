package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.TeachPlan;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.models.TeacherTeachPlanRole;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.TeachPlanRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.repositorys.TeacherTeachPlanRoleRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程教师管理服务
 * 负责管理课程教学班级与教师的关系
 */
@Service
public class CourseTeacherManagementService {

    private final CourseRepository courseRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherTeachPlanRoleRepository teacherTeachPlanRoleRepository;
    private final SystemService systemService;

    public CourseTeacherManagementService(
            CourseRepository courseRepository,
            TeachPlanRepository teachPlanRepository,
            TeacherRepository teacherRepository,
            TeacherTeachPlanRoleRepository teacherTeachPlanRoleRepository,
            SystemService systemService) {
        this.courseRepository = courseRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.teacherRepository = teacherRepository;
        this.teacherTeachPlanRoleRepository = teacherTeachPlanRoleRepository;
        this.systemService = systemService;
    }

    /**
     * 创建教学班级
     * @param dataRequest 包含课程ID、学年、学期、班级名称、最大学生人数等信息
     * @return 创建结果
     */
    public DataResponse createTeachPlan(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer courseId = CommonMethod.getInteger(form, "courseId");
        Integer year = CommonMethod.getInteger(form, "year");
        Integer semester = CommonMethod.getInteger(form, "semester");
        String className = CommonMethod.getString(form, "className");
        Integer maxStudentCount = CommonMethod.getInteger(form, "maxStudentCount");

        // 检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }

        // 如果学年学期为空，使用当前学期
        if (year == null || semester == null) {
            Calendar calendar = Calendar.getInstance();
            if (year == null) {
                year = calendar.get(Calendar.YEAR);
            }
            if (semester == null) {
                semester = switch (calendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                         Calendar.JUNE -> 2;
                    case Calendar.JULY, Calendar.AUGUST -> 3;
                    default -> 1;
                };
            }
        }

        // 生成教学班代码
        Integer teachPlanCode = 1;
        List<TeachPlan> existingPlans = teachPlanRepository.findByCourse_CourseId(courseId);
        if (!existingPlans.isEmpty()) {
            Integer finalYear = year;
            Integer finalSemester = semester;
            Optional<Integer> maxCode = existingPlans.stream()
                    .filter(tp -> Objects.equals(tp.getYear(), finalYear) && Objects.equals(tp.getSemester(), finalSemester))
                    .map(TeachPlan::getTeachPlanCode)
                    .max(Integer::compareTo);
            if (maxCode.isPresent()) {
                teachPlanCode = maxCode.get() + 1;
            }
        }

        // 创建教学班
        TeachPlan teachPlan = new TeachPlan();
        teachPlan.setCourse(courseOpt.get());
        teachPlan.setYear(year);
        teachPlan.setSemester(semester);
        teachPlan.setTeachPlanCode(teachPlanCode);
        teachPlan.setClassName(className);
        teachPlan.setMaxStudentCount(maxStudentCount);
        
        teachPlanRepository.save(teachPlan);
        
        return CommonMethod.getReturnData(teachPlan.getTeachPlanId());
    }

    /**
     * 为教学班分配教师
     * @param dataRequest 包含教学班ID、教师ID列表及其角色
     * @return 分配结果
//     */
//    public DataResponse assignTeachersToTeachPlan(DataRequest dataRequest) {
//        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
////        List<Map<String, Object>> teacherAssignments = dataRequest.getListMap("teacherAssignments");
//
//        // 检查教学班是否存在
//        Optional<TeachPlan> teachPlanOpt = teachPlanRepository.findById(teachPlanId);
//        if (teachPlanOpt.isEmpty()) {
//            return CommonMethod.getReturnMessageError("教学班不存在");
//        }
//
//        TeachPlan teachPlan = teachPlanOpt.get();
//
//        // 删除现有的教师分配
//        List<TeacherTeachPlanRole> currentRoles = teacherTeachPlanRoleRepository.findByTeachPlan_TeachPlanId(teachPlanId);
//        teacherTeachPlanRoleRepository.deleteAll(currentRoles);
//
//        List<TeacherTeachPlanRole> newRoles = new ArrayList<>();
//        Date now = new Date();
//
//        // 添加新的教师分配
//        for (Map<String, Object> assignment : teacherAssignments) {
//            Integer teacherId = CommonMethod.getInteger(assignment, "teacherId");
//            String role = CommonMethod.getString(assignment, "role");
//
//            Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
//            if (teacherOpt.isEmpty()) {
//                continue; // 跳过不存在的教师
//            }
//
//            TeacherTeachPlanRole teacherRole = new TeacherTeachPlanRole();
//            teacherRole.setTeachPlan(teachPlan);
//            teacherRole.setTeacher(teacherOpt.get());
//            teacherRole.setRole(role);
//            teacherRole.setCreateTime(now);
//            teacherRole.setUpdateTime(now);
//
//            newRoles.add(teacherRole);
//        }
//
//        if (!newRoles.isEmpty()) {
//            teacherTeachPlanRoleRepository.saveAll(newRoles);
//        }
//
//        return CommonMethod.getReturnMessageOK("教师分配成功");
//    }

    /**
     * 获取教学班的所有教师信息
     * @param dataRequest 包含教学班ID
     * @return 教师信息列表
     */
    public DataResponse getTeachersByTeachPlan(DataRequest dataRequest) {
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        
        // 检查教学班是否存在
        if (!teachPlanRepository.existsById(teachPlanId)) {
            return CommonMethod.getReturnMessageError("教学班不存在");
        }
        
        List<TeacherTeachPlanRole> roles = teacherTeachPlanRoleRepository.findByTeachPlan_TeachPlanId(teachPlanId);
        List<Map<String, Object>> teacherInfoList = new ArrayList<>();
        
        for (TeacherTeachPlanRole role : roles) {
            Teacher teacher = role.getTeacher();
            Map<String, Object> teacherInfo = new HashMap<>();
            teacherInfo.put("teacherId", teacher.getPersonId());
            teacherInfo.put("name", teacher.getPerson().getName());
            teacherInfo.put("title", teacher.getTitle());
            teacherInfo.put("role", role.getRole());
            teacherInfo.put("dept", teacher.getPerson().getDept());
            teacherInfoList.add(teacherInfo);
        }
        
        return CommonMethod.getReturnData(teacherInfoList);
    }

    /**
     * 获取教师的所有教学任务
     * @param dataRequest 包含教师ID、学年和学期
     * @return 教学任务列表
     */
    public DataResponse getTeacherTeachingAssignments(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        
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
        
        List<Map<String, Object>> assignmentList = new ArrayList<>();
        
        for (TeacherTeachPlanRole role : roles) {
            TeachPlan teachPlan = role.getTeachPlan();
            Course course = teachPlan.getCourse();
            
            Map<String, Object> assignment = new HashMap<>();
            assignment.put("teachPlanId", teachPlan.getTeachPlanId());
            assignment.put("courseId", course.getCourseId());
            assignment.put("courseName", course.getName());
            assignment.put("courseNum", course.getNum());
            assignment.put("credit", course.getCredit());
            assignment.put("className", teachPlan.getClassName());
            assignment.put("year", teachPlan.getYear());
            assignment.put("semester", teachPlan.getSemester());
            assignment.put("role", role.getRole());
            assignment.put("teachPlanCode", teachPlan.getTeachPlanCode());
            
            assignmentList.add(assignment);
        }
        
        return CommonMethod.getReturnData(assignmentList);
    }

    /**
     * 获取当前学期所有教学班级信息
     * @param dataRequest 包含查询条件：学年、学期、课程ID
     * @return 教学班级信息列表
     */
    public DataResponse getTeachPlanList(DataRequest dataRequest) {
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        Integer courseId = dataRequest.getInteger("courseId");
        
        // 如果学年学期为空，使用当前学期
        if (year == null || semester == null) {
            Calendar calendar = Calendar.getInstance();
            if (year == null) {
                year = calendar.get(Calendar.YEAR);
            }
            if (semester == null) {
                semester = switch (calendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                         Calendar.JUNE -> 2;
                    case Calendar.JULY, Calendar.AUGUST -> 3;
                    default -> 1;
                };
            }
        }
        
        // 查询指定条件的教学班
        List<TeachPlan> teachPlans = teachPlanRepository.filterByCourseTeacherSemesterYear(
                courseId == null ? 0 : courseId, 
                0, // 不再按单个教师过滤
                semester, 
                year
        );
        
        List<Map<String, Object>> teachPlanInfoList = new ArrayList<>();
        
        for (TeachPlan teachPlan : teachPlans) {
            Course course = teachPlan.getCourse();
            
            Map<String, Object> teachPlanInfo = new HashMap<>();
            teachPlanInfo.put("teachPlanId", teachPlan.getTeachPlanId());
            teachPlanInfo.put("courseId", course.getCourseId());
            teachPlanInfo.put("courseName", course.getName());
            teachPlanInfo.put("courseNum", course.getNum());
            teachPlanInfo.put("credit", course.getCredit());
            teachPlanInfo.put("year", teachPlan.getYear());
            teachPlanInfo.put("semester", teachPlan.getSemester());
            teachPlanInfo.put("className", teachPlan.getClassName());
            teachPlanInfo.put("teachPlanCode", teachPlan.getTeachPlanCode());
            teachPlanInfo.put("maxStudentCount", teachPlan.getMaxStudentCount());
            
            // 获取该教学班的所有教师信息
            List<TeacherTeachPlanRole> roles = teacherTeachPlanRoleRepository.findByTeachPlan_TeachPlanId(teachPlan.getTeachPlanId());
            List<Map<String, Object>> teacherList = new ArrayList<>();
            
            for (TeacherTeachPlanRole role : roles) {
                Teacher teacher = role.getTeacher();
                Map<String, Object> teacherInfo = new HashMap<>();
                teacherInfo.put("teacherId", teacher.getPersonId());
                teacherInfo.put("name", teacher.getPerson().getName());
                teacherInfo.put("title", teacher.getTitle());
                teacherInfo.put("role", role.getRole());
                teacherList.add(teacherInfo);
            }
            
            teachPlanInfo.put("teachers", teacherList);
            teachPlanInfoList.add(teachPlanInfo);
        }
        
        return CommonMethod.getReturnData(teachPlanInfoList);
    }
}