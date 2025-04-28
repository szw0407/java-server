package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentCourseSelectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 学生选课控制器
 * 提供学生选课相关的API接口
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student-selection")
public class StudentCourseSelectionController {

    private final StudentCourseSelectionService selectionService;

    public StudentCourseSelectionController(StudentCourseSelectionService selectionService) {
        this.selectionService = selectionService;
    }

    /**
     * 学生选课
     * @param dataRequest 包含学生ID、教学班级ID
     * @return 选课结果
     */
    @PostMapping("/select-course")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse selectCourse(@RequestBody DataRequest dataRequest) {
        return selectionService.selectCourse(dataRequest);
    }

    /**
     * 学生退课
     * @param dataRequest 包含学生ID、教学班级ID
     * @return 退课结果
     */
    @PostMapping("/withdraw-course")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse withdrawCourse(@RequestBody DataRequest dataRequest) {
        return selectionService.withdrawCourse(dataRequest);
    }

    /**
     * 获取学生当前学期选课列表
     * @param dataRequest 包含学生ID、学年、学期
     * @return 选课列表
     */
    @PostMapping("/get-selections")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'TEACHER')")
    public DataResponse getStudentCourseSelections(@RequestBody DataRequest dataRequest) {
        return selectionService.getStudentCourseSelections(dataRequest);
    }

    /**
     * 获取可选课程列表
     * @param dataRequest 包含学生ID、学年、学期
     * @return 可选课程列表
     */
    @PostMapping("/get-available-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getAvailableCourses(@RequestBody DataRequest dataRequest) {
        return selectionService.getAvailableCourses(dataRequest);
    }
}