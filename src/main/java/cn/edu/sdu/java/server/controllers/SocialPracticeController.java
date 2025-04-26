package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.SocialPracticeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/socialPractice")
public class SocialPracticeController {

    private final SocialPracticeService socialPracticeService;

    public SocialPracticeController(SocialPracticeService socialPracticeService) {
        this.socialPracticeService = socialPracticeService;
    }

    @PostMapping("/getPracticeList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getPracticeList(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.getPracticeList(dataRequest);
    }

    @PostMapping("/practiceEditSave")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse practiceEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.practiceEditSave(dataRequest);
    }

    @PostMapping("/practiceDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceDelete(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.practiceDelete(dataRequest);
    }

    @PostMapping("/getPracticeInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getPracticeInfo(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.getPracticeInfo(dataRequest);
    }

}