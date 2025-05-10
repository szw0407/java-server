package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.TeachPlanService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teachplan")
public class TeachPlanController {
    
    private final TeachPlanService teachPlanService;
    
    public TeachPlanController(TeachPlanService teachPlanService) {
        this.teachPlanService = teachPlanService;
    }
    
    /**
     * 获取教师选项列表
     */
    @PostMapping("/getTeacherOptionList")
    @PreAuthorize("hasRole('ADMIN')")
    public OptionItemList getTeacherOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.getTeacherOptionList(dataRequest);
    }
    
    /**
     * 获取课程选项列表
     */
    @PostMapping("/getCourseOptionList")
    @PreAuthorize("hasRole('ADMIN')")
    public OptionItemList getCourseOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.getCourseOptionList(dataRequest);
    }
    
    /**
     * 获取教师的教学计划列表
     */
    @PostMapping("/getTeacherPlanList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getTeacherPlanList(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.getTeacherPlanList(dataRequest);
    }
    
    /**
     * 获取当前学期的教学班级列表
     */
    @PostMapping("/getCurrentSemesterClasses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getCurrentSemesterClasses(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.getCurrentSemesterClasses(dataRequest);
    }
    
    /**
     * 添加教师到教学计划
     */
    @PostMapping("/addTeacherToPlan")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addTeacherToPlan(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.addTeacherToPlan(dataRequest);
    }
    
    /**
     * 移除教师的教学计划
     */
    @PostMapping("/removeTeacherPlan")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse removeTeacherPlan(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.removeTeacherPlan(dataRequest);
    }
    
    /**
     * 开设当前学期的教学班级
     */
    @PostMapping("/createClassSchedule")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse createClassSchedule(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.createClassSchedule(dataRequest);
    }
    
    /**
     * 修改教学班级信息
     */
    @PostMapping("/updateClassSchedule")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateClassSchedule(@Valid @RequestBody DataRequest dataRequest) {
        return teachPlanService.updateClassSchedule(dataRequest);
    }
}
