package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentLeaveService;
import cn.edu.sdu.java.server.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentLeave")
public class StudentLeaveController {
    private final StudentLeaveService studentLeaveService;
    private final StudentService studentService;

    public StudentLeaveController(StudentLeaveService studentLeaveService, StudentService studentService) {
        this.studentLeaveService = studentLeaveService;
        this.studentService = studentService;
    }

    @PostMapping("/getStudentLeaveList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.getStudentLeaveList(dataRequest);
    }

    @PostMapping("/studentLeaveDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentLeaveDelete(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.studentLeaveDelete(dataRequest);
    }

    @PostMapping("/checkStudentIdExists")
    public DataResponse checkStudentIdExists(@Valid @RequestBody DataRequest dataRequest) {
        String studentId = dataRequest.getString("studentId");
        boolean exists = studentLeaveService.checkStudentIdExists(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return new DataResponse(0, response, "成功");
    }


    @PostMapping("/getStudentLeaveInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.getStudentLeaveInfo(dataRequest);
    }

    @PostMapping("/studentLeaveEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentLeaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.studentLeaveEditSave(dataRequest);
    }
}
