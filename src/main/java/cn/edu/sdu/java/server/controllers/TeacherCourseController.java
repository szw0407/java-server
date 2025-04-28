package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherCourseManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 教师课程管理控制器
 * 提供教师查看课程、学生名单和打分的功能
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher-course")
public class TeacherCourseController {

    private final TeacherCourseManagementService teacherCourseService;

    public TeacherCourseController(TeacherCourseManagementService teacherCourseService) {
        this.teacherCourseService = teacherCourseService;
    }

    /**
     * 获取教师教授的课程列表
     * @param dataRequest 包含教师ID、学年和学期
     * @return 课程列表
     */
    @PostMapping("/my-courses")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getTeacherCourses(@RequestBody DataRequest dataRequest) {
        return teacherCourseService.getTeacherCourses(dataRequest);
    }

    /**
     * 获取教学班级的学生名单
     * @param dataRequest 包含教师ID、教学班级ID
     * @return 学生名单
     */
    @PostMapping("/class-students")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getClassStudents(@RequestBody DataRequest dataRequest) {
        return teacherCourseService.getClassStudents(dataRequest);
    }

    /**
     * 教师为学生打分
     * @param dataRequest 包含教师ID、教学班级ID、学生ID和成绩
     * @return 打分结果
     */
    @PostMapping("/grade-student")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse gradeStudent(@RequestBody DataRequest dataRequest) {
        return teacherCourseService.gradeStudent(dataRequest);
    }

    /**
     * 教师批量导入成绩
     * @param dataRequest 包含教师ID、教学班级ID和学生成绩列表
     * @return 导入结果
     */
    @PostMapping("/batch-grade")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse batchGradeStudents(@RequestBody DataRequest dataRequest) {
        return teacherCourseService.batchGradeStudents(dataRequest);
    }
}