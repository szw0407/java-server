package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.InnovationProject;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.InnovationProjectRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class InnovationProjectService {
    private final InnovationProjectRepository innovationProjectRepository;
    private final StudentRepository studentRepository;

    public InnovationProjectService(InnovationProjectRepository innovationProjectRepository, StudentRepository studentRepository) {
        this.innovationProjectRepository = innovationProjectRepository;
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
    public DataResponse getInnovationProjectList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("studentId");
        if(personId == null) {
            personId = 0;
        }
        List<InnovationProject> iList = innovationProjectRepository.findByStudentId(personId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (InnovationProject i : iList) {
            m = new HashMap<>();
            m.put("id", i.getId()+"");
            m.put("name", i.getStudent().getPerson().getName());
            m.put("studentId", i.getStudent().getPersonId()+"");
            m.put("type", i.getType());
            m.put("time", i.getTime());
            m.put("description", i.getDescription());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse innovationProjectSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("studentId");
        Integer id = dataRequest.getInteger("id");
        Optional<InnovationProject> op;
        InnovationProject i = null;
        if(id!=null){
            op = innovationProjectRepository.findById(id);
            if(op.isPresent()){
                i = op.get();
            }
        }
        if(i==null){
            i = new InnovationProject();
            i.setStudent(studentRepository.findById(personId).get());
        }
        i.setId(id);
        i.setType(dataRequest.getString("type"));
        i.setTime(dataRequest.getString("time"));
        i.setDescription(dataRequest.getString("description"));
        innovationProjectRepository.save(i);
        return CommonMethod.getReturnMessageOK();

    }
    public DataResponse innovationProjectDelete(DataRequest dataRequest){
        Integer id = dataRequest.getInteger("id");
        Optional op;
        InnovationProject i = null;
        if(id!=null){
            op = innovationProjectRepository.findById(id);
            if(op.isPresent()){
                i = (InnovationProject) op.get();
                innovationProjectRepository.delete(i);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}

