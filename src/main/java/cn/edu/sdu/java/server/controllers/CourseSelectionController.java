package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseSelectionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 学生选课退课控制器
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courseSelection")
public class CourseSelectionController {

    private final CourseSelectionService courseSelectionService;

    public CourseSelectionController(CourseSelectionService courseSelectionService) {
        this.courseSelectionService = courseSelectionService;
    }
    @PostMapping("/getSelectedCoursesAll")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getSelectedCoursesAll(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getSelectedCoursesAll(dataRequest);
    }
    @PostMapping("/getAvailableCoursesAll")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAvailableCoursesAll(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getAvailableCoursesAll(dataRequest);
    }
    /**
     * 获取学生的已选课程列表
     */
    @PostMapping("/getStudentSelectedCourses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getSelectedCourses(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getSelectedCourses(dataRequest);
    }

    /**
     * 获取学生可选的课程列表
     */
    @PostMapping("/getStudentAvailableCourses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getAvailableCourses(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.getAvailableCourses(dataRequest);
    }

    @PostMapping("/selectCourseForStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse selectCourseForStudent(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.selectCourseForStudent(dataRequest);
    }

    @PostMapping("/dropCourseForStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse dropCourseForStudent(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.dropCourseForStudent(dataRequest);
    }

    /**
     * 选课
     */
    @PostMapping("/selectCourse")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse selectCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.selectCourse(dataRequest);
    }

    /**
     * 退课
     */
    @PostMapping("/dropCourse")
    @PreAuthorize(" hasRole('STUDENT')")
    public DataResponse dropCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.dropCourse(dataRequest);
    }

    @PostMapping("/verifyStudentCourseSelection")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse verifyStudentCourseSelection(@Valid @RequestBody DataRequest dataRequest) {
        return courseSelectionService.verifyStudentCourseSelection(dataRequest);
    }

}
