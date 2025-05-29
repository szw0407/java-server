package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.MyService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/me")
class MyController {
    @PostMapping("/PlanList")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public DataResponse getMyPlanList(@Valid @RequestBody DataRequest dataRequest) {

        return MyService.getMyPlanList(dataRequest);
    }
    @PostMapping("/CourseList")
    public DataResponse getMyCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return MyService.getMyCourseList(dataRequest);
    }

    @PostMapping("/AvailableCourseList")
    public DataResponse getMyAvailableCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return MyService.getMyAvailableCourseList(dataRequest);
    }

    @PostMapping("/ScoreList")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public DataResponse getMyScoreList(@Valid @RequestBody DataRequest dataRequest) {
        return MyService.getMyScoreList(dataRequest);
    }
}
