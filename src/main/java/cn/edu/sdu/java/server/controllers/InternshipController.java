package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.InternshipService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/internship")
public class InternshipController {
    private final InternshipService internshipService;
    public InternshipController(InternshipService internshipService)
    {
        this.internshipService = internshipService;
    }
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest)
    {
        return internshipService.getStudentItemOptionList(dataRequest);
    }
    @PostMapping("/getInternshipList")
    public DataResponse getInternshipList(@Valid @RequestBody DataRequest dataRequest)
    {
        return internshipService.getInternshipList(dataRequest);
    }
    @PostMapping("/internshipSave")
    public DataResponse internshipSave(@Valid @RequestBody DataRequest dataRequest)
    {
        return internshipService.internshipSave(dataRequest);
    }
    @PostMapping("/internshipDelete")
    public DataResponse internshipDelete(@Valid @RequestBody DataRequest dataRequest)
    {
        return internshipService.internshipDelete(dataRequest);
    }
}
