package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentLeaveService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

/**
 * StudentLeaveController 主要是为学生请假管理提供的Web请求服务
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentLeave")
public class StudentLeaveController {

    private final StudentLeaveService studentLeaveService;

    public StudentLeaveController(StudentLeaveService studentLeaveService) {
        this.studentLeaveService = studentLeaveService;
    }

    /**
     * 获取请假记录列表
     * @param dataRequest 前端传入的查询参数
     * @return 请假记录列表
     */
    @PostMapping("/getLeaveList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.getLeaveList(dataRequest);
    }

    /**
     * 获取单条请假记录详情
     * @param dataRequest 前端传入的请假记录ID
     * @return 请假记录详情
     */
    @PostMapping("/getLeaveInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.getLeaveInfo(dataRequest);
    }

    /**
     * 保存或更新请假记录
     * @param dataRequest 前端传入的请假记录表单数据
     * @return 操作结果
     */
    @PostMapping("/saveLeave")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse saveLeave(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.saveLeave(dataRequest);
    }

    /**
     * 删除请假记录
     * @param dataRequest 前端传入的请假记录ID
     * @return 操作结果
     */
    @PostMapping("/deleteLeave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteLeave(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.deleteLeave(dataRequest);
    }

    /**
     * 分页获取请假记录
     * @param dataRequest 前端传入的分页参数
     * @return 分页请假记录
     */
    @PostMapping("/getLeavePageData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getLeavePageData(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.getLeavePageData(dataRequest);
    }

    /**
     * 导出请假记录为Excel
     * @param dataRequest 前端传入的查询参数
     * @return Excel文件流
     */
    @PostMapping("/exportLeaveData")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> exportLeaveData(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.exportLeaveData(dataRequest);
    }

    @PostMapping("/approveLeave")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse approveLeave(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.approveLeave(dataRequest);
    }
}
