package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.InnovationProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/innovationProject")
public class InnovationProjectController {
    private final InnovationProjectService innovationProjectService;
    public InnovationProjectController(InnovationProjectService innovationProjectService)
    {
        this.innovationProjectService = innovationProjectService;
    }
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest)
    {
        return innovationProjectService.getStudentItemOptionList(dataRequest);
    }
    @PostMapping("/getInnovationProjectList")
    public DataResponse getInnovationProjectList(@Valid @RequestBody DataRequest dataRequest)
    {
        return innovationProjectService.getInnovationProjectList(dataRequest);
    }
    @PostMapping("/innovationProjectSave")
    public DataResponse innovationProjectSave(@Valid @RequestBody DataRequest dataRequest)
    {
        return innovationProjectService.innovationProjectSave(dataRequest);
    }
    @PostMapping("/innovationProjectDelete")
    public DataResponse innovationProjectDelete(@Valid @RequestBody DataRequest dataRequest)
    {
        return innovationProjectService.innovationProjectDelete(dataRequest);
    }
}
