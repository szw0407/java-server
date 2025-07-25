package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/getTeacherList")
    @PreAuthorize(" hasRole('ADMIN') ")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }

    @PostMapping("/teacherDelete")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }

    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherEditSave(dataRequest);
    }

    @PostMapping("/getSTeacherListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherListExcl(dataRequest);
    }
    @PostMapping("/getCurrentTeacherData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getCurrentTeacherData(@Valid @RequestBody DataRequest dataRequest) {
        // 调用 StudentService 中的 getCurrentStudentData 方法
        Map<String, Object> teacherData = teacherService.getCurrentTeacherData(dataRequest);
        // 返回封装的 DataResponse
        return CommonMethod.getReturnData(teacherData);
    }




}
