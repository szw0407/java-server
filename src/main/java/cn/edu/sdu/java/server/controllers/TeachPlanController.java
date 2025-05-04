package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 教学计划控制器
 * 提供教学计划管理相关的API接口
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach-plan")
public class TeachPlanController {

    private final TeachPlanManagementService teachPlanManagementService;

    public TeachPlanController(TeachPlanManagementService teachPlanManagementService) {
        this.teachPlanManagementService = teachPlanManagementService;
    }

    /**
     * 创建教学计划
     * @param dataRequest 包含课程ID、学年、学期、教师ID列表、教学班代码等信息
     * @return 创建结果
     */
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
//    public DataResponse createTeachPlan(@RequestBody DataRequest dataRequest) {
//        Integer courseId = dataRequest.getInteger("courseId");
//        Integer year = dataRequest.getInteger("year");
//        Integer semester = dataRequest.getInteger("semester");
//        List<Integer> teacherIds = dataRequest.getListInteger("teacherIds");
//        Integer teachPlanCode = dataRequest.getInteger("teachPlanCode");
//
//        return teachPlanManagementService.createTeachPlanForClass(courseId, year, semester, teacherIds, teachPlanCode);
//    }

    /**
     * 获取教师的所有教学计划
     * @param dataRequest 包含教师ID、学年、学期等信息
     * @return 教学计划列表
     */
    @PostMapping("/get-teacher-plans")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public DataResponse getTeacherTeachPlans(@RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        
        return teachPlanManagementService.getTeacherTeachPlans(teacherId, year, semester);
    }
}
