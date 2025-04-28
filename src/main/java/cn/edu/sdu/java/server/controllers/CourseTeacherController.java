package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseTeacherManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 课程教师管理控制器
 * 提供课程教师管理相关的API接口
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course-teacher")
public class CourseTeacherController {

    private final CourseTeacherManagementService courseTeacherManagementService;

    public CourseTeacherController(CourseTeacherManagementService courseTeacherManagementService) {
        this.courseTeacherManagementService = courseTeacherManagementService;
    }

    /**
     * 创建教学班级
     * @param dataRequest 包含课程ID、学年、学期、班级名称、最大学生人数等信息
     * @return 创建结果
     */
    @PostMapping("/create-teach-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse createTeachPlan(@RequestBody DataRequest dataRequest) {
        return courseTeacherManagementService.createTeachPlan(dataRequest);
    }

    /**
     * 为教学班分配教师
     * @param dataRequest 包含教学班ID、教师ID列表及其角色
     * @return 分配结果
     */
//    @PostMapping("/assign-teachers")
//    @PreAuthorize("hasRole('ADMIN')")
//    public DataResponse assignTeachersToTeachPlan(@RequestBody DataRequest dataRequest) {
//        return courseTeacherManagementService.assignTeachersToTeachPlan(dataRequest);
//    }

    /**
     * 获取教学班的所有教师信息
     * @param dataRequest 包含教学班ID
     * @return 教师信息列表
     */
    @PostMapping("/get-teachers-by-teach-plan")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public DataResponse getTeachersByTeachPlan(@RequestBody DataRequest dataRequest) {
        return courseTeacherManagementService.getTeachersByTeachPlan(dataRequest);
    }

    /**
     * 获取教师的所有教学任务
     * @param dataRequest 包含教师ID、学年和学期
     * @return 教学任务列表
     */
    @PostMapping("/get-teacher-assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public DataResponse getTeacherTeachingAssignments(@RequestBody DataRequest dataRequest) {
        return courseTeacherManagementService.getTeacherTeachingAssignments(dataRequest);
    }

    /**
     * 获取当前学期所有教学班级信息
     * @param dataRequest 包含查询条件：学年、学期、课程ID
     * @return 教学班级信息列表
     */
    @PostMapping("/get-teach-plan-list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public DataResponse getTeachPlanList(@RequestBody DataRequest dataRequest) {
        return courseTeacherManagementService.getTeachPlanList(dataRequest);
    }
}