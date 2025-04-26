package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;


import cn.edu.sdu.java.server.services.AcademicCompetitionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/academicCompetition")
public class AcademicCompetitionController {
    private final AcademicCompetitionService academicCompetitionService;
    public AcademicCompetitionController(AcademicCompetitionService academicCompetitionService) {
        this.academicCompetitionService = academicCompetitionService;
    }

    @PostMapping("/getAcademicCompetitionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getAcademicCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        return academicCompetitionService.getAcademicCompetitionList(dataRequest);
    }

    @PostMapping("/academicCompetitionDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse academicCompetitionDelete(@Valid @RequestBody DataRequest dataRequest) {// 前端需传递 competitionId
        return academicCompetitionService.academicCompetitionDelete(dataRequest);
    }

    @PostMapping("/getAcademicCompetitionInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getAcademicCompetitionInfo(@Valid @RequestBody DataRequest dataRequest) {
        return academicCompetitionService.getAcademicCompetitionInfo(dataRequest);
    }

    @PostMapping("/academicCompetitionEditSave")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse academicCompetitionEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return academicCompetitionService.academicCompetitionEditSave(dataRequest);
    }
}
