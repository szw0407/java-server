package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.TrainingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/training")
public class TrainingController {
    private final TrainingService trainingService;
    public TrainingController(TrainingService trainingService){
        this.trainingService = trainingService;
    }
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        return trainingService.getStudentItemOptionList(dataRequest);
    }
    @PostMapping("/getTrainingList")
    public DataResponse getTrainingList(@Valid @RequestBody DataRequest dataRequest){
        return trainingService.getTrainingList(dataRequest);
    }
    @PostMapping("/trainingSave")
    public DataResponse trainingSave(@Valid @RequestBody DataRequest dataRequest){
        return trainingService.trainingSave(dataRequest);
    }
    @PostMapping("/trainingDelete")
    public DataResponse trainingDelete(@Valid @RequestBody DataRequest dataRequest){
        return trainingService.trainingDelete(dataRequest);
    }

}
