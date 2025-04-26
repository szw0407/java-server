package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TechnicalAchieveService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/technicalAchieve")
public class TechnicalAchieveController {
    private final TechnicalAchieveService technicalAchieveService;
    public TechnicalAchieveController(TechnicalAchieveService technicalAchieveService) {
        this.technicalAchieveService = technicalAchieveService;
    }

    @PostMapping("/getTechnicalAchieveList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getTechnicalAchieveList(@Valid @RequestBody DataRequest dataRequest) {
        return technicalAchieveService.getTechnicalAchieveList(dataRequest);
    }
    @PostMapping("/technicalAchieveDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse technicalAchieveDelete(@Valid @RequestBody DataRequest dataRequest) {
        return technicalAchieveService.technicalAchieveDelete(dataRequest);
    }
    @PostMapping("/getTechnicalAchieveInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getTechnicalAchieveInfo(@Valid @RequestBody DataRequest dataRequest) {
        return technicalAchieveService.getTechnicalAchieveInfo(dataRequest);
    }
    @PostMapping("/technicalAchieveEditSave")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse technicalAchieveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return technicalAchieveService.technicalAchieveEditSave(dataRequest);
    }
}
