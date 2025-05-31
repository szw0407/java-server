package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentSocialActService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentSocialAct")
public class StudentSocialActController {
    private final StudentSocialActService studentSocialActService;
    public StudentSocialActController(StudentSocialActService studentSocialActService) {
        this.studentSocialActService = studentSocialActService;
    }
    // 这里可以添加其他的请求处理方法，例如获取活动列表、添加活动等

    // 例如：
    @PostMapping("/getlist")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return studentSocialActService.getList(dataRequest);
    }
    @PostMapping("/SocialActEditsave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentSocialActEditSave(@Valid @RequestBody DataRequest dataRequest) throws ParseException {
        return studentSocialActService.studentSocialActEditSave(dataRequest);
    }
    @PostMapping("/SocialActDelete" )
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse socialActDelete(@Valid @RequestBody DataRequest dataRequest) {
        return studentSocialActService.socialActDelete(dataRequest);
    }
}
