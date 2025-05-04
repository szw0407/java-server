package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 课程时间安排控制器
 * 提供课程时间安排相关的API接口
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/class-schedule")
public class ClassScheduleController {

    private final ClassScheduleService classScheduleService;

    public ClassScheduleController(ClassScheduleService classScheduleService) {
        this.classScheduleService = classScheduleService;
    }

    /**
     * 为教学班级添加课程时间安排
     * @param dataRequest 包含教学班ID、上课时间、地点等信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addClassSchedule(@RequestBody DataRequest dataRequest) {
        return classScheduleService.addClassSchedule(dataRequest);
    }

    /**
     * 编辑课程时间安排
     * @param dataRequest 包含排课ID和更新信息
     * @return 编辑结果
     */
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateClassSchedule(@RequestBody DataRequest dataRequest) {
        return classScheduleService.updateClassSchedule(dataRequest);
    }

    /**
     * 删除课程时间安排
     * @param dataRequest 包含排课ID
     * @return 删除结果
     */
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteClassSchedule(@RequestBody DataRequest dataRequest) {
        return classScheduleService.deleteClassSchedule(dataRequest);
    }

    /**
     * 获取教学班级的课程时间安排
     * @param dataRequest 包含教学班ID
     * @return 课程时间安排列表
     */
    @PostMapping("/get-by-teachplan")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public DataResponse getClassSchedules(@RequestBody DataRequest dataRequest) {
        return classScheduleService.getClassSchedules(dataRequest);
    }

    /**
     * 获取教师的课程表
     * @param dataRequest 包含教师ID、学年和学期
     * @return 教师课程表
     */
    @PostMapping("/get-teacher-schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public DataResponse getTeacherSchedule(@RequestBody DataRequest dataRequest) {
        return classScheduleService.getTeacherSchedule(dataRequest);
    }

    /**
     * 检查是否存在教师时间冲突
     * @param dataRequest 包含教学班ID、教师ID、课程时间安排
     * @return 冲突检查结果
     */
    @PostMapping("/check-teacher-conflicts")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse checkTeacherConflicts(@RequestBody DataRequest dataRequest) {
        return classScheduleService.checkTeacherConflicts(dataRequest);
    }
}