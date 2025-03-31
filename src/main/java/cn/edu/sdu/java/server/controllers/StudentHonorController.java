package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentHonorService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentHonor")
public class StudentHonorController {
    private final StudentHonorService studentHonorService;

    public StudentHonorController(StudentHonorService studentHonorService) {
        this.studentHonorService = studentHonorService;
    }

    @PostMapping("/getStudentHonorList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentHonorList(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.getStudentHonorList(dataRequest);
    }

    @PostMapping("/studentHonorDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentHonorDelete(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.studentHonorDelete(dataRequest);
    }

    @PostMapping("/getStudentHonorInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentHonorInfo(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.getStudentHonorInfo(dataRequest);
    }

    @PostMapping("/studentHonorEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentHonorEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.studentHonorEditSave(dataRequest);
    }

    @PostMapping("/getStudentHonorListExcl")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> getStudentHonorListExcl(@Valid @RequestBody DataRequest dataRequest) {
        return studentHonorService.getStudentHonorListExcl(dataRequest);
    }
}