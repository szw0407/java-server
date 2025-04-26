package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Training;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.TrainingRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TrainingService {
    private final TrainingRepository trainingRepository ;
    private final StudentRepository studentRepository;

    public TrainingService(TrainingRepository trainingRepository, StudentRepository studentRepository)
    {
        this.trainingRepository = trainingRepository;
        this.studentRepository = studentRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse getTrainingList(DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("studentId");
        if(personId == null) {
            personId = 0;
        }
        List<Training> iList = trainingRepository.findByStudentId(personId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Training i : iList) {
            m = new HashMap<>();
            m.put("id", i.getId()+"");
            m.put("name", i.getStudent().getPerson().getName());
            m.put("studentId", i.getStudent().getPersonId()+"");
            m.put("time", i.getTime());
            m.put("location", i.getLocation());
            m.put("theme", i.getTheme());
            m.put("description", i.getDescription());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse trainingSave(DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("studentId");
        Integer id = dataRequest.getInteger("id");
        Optional <Training> op;
        Training i = null;
        if(id!=null){
            op = trainingRepository.findById(id);
            if(op.isPresent()){
                i = op.get();
            }
        }
        if(i==null){
            i = new Training();
            i.setStudent(studentRepository.findById(personId).get());
        }
        i.setId(id);
        i.setTime(dataRequest.getString("time"));
        i.setLocation(dataRequest.getString("location"));
        i.setTheme(dataRequest.getString("theme"));
        i.setDescription(dataRequest.getString("description"));
        trainingRepository.save(i);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse trainingDelete(DataRequest dataRequest){
        Integer id = dataRequest.getInteger("id");
        Optional op;
        Training i = null;
        if(id!=null){
            op = trainingRepository.findById(id);
            if(op.isPresent()){
                i = (Training)op.get();
                trainingRepository.delete(i);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
