package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.GradingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 成绩管理控制器
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/grading")
public class GradingController {

    private final GradingService gradingService;

    public GradingController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    /**
     * 获取教师所教授班级的学生成绩列表
     */
    @PostMapping("/getStudentScores")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getStudentScores(@Valid @RequestBody DataRequest dataRequest) {
        return gradingService.getStudentScores(dataRequest);
    }

    /**
     * 提交学生成绩
     */
    @PostMapping("/submitScore")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse submitScore(@Valid @RequestBody DataRequest dataRequest) {
        return gradingService.submitScore(dataRequest);
    }

    /**
     * 批量提交学生成绩
     */
    @PostMapping("/submitScoreBatch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse submitScoreBatch(@Valid @RequestBody DataRequest dataRequest) {
        return gradingService.submitScoreBatch(dataRequest);
    }
}
