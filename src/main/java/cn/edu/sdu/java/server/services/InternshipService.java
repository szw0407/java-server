package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Internship;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.InternshipRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class InternshipService {
    private final StudentRepository studentRepository;
    private final InternshipRepository internshipRepository;
    public InternshipService(StudentRepository studentRepository, InternshipRepository internshipRepository)
    {
        this.studentRepository = studentRepository;
        this.internshipRepository = internshipRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse  getInternshipList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("studentId");
        if(personId == null) {
            personId = 0;
        }
        List<Internship> iList = internshipRepository.findByStudentId(personId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Internship i : iList) {
            m = new HashMap<>();
            m.put("id", i.getId()+"");
            m.put("studentId", i.getStudent().getPersonId()+"");
            m.put("studentNum", i.getStudent().getPerson().getNum());
            m.put("studentName", i.getStudent().getPerson().getName());
            m.put("startTime", i.getStartTime());
            m.put("endTime", i.getEndTime());
            m.put("position", i.getPosition());
            m.put("company", i.getCompany());
            m.put("description", i.getDescription());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse internshipSave(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Integer studentId = dataRequest.getInteger("studentId");

        Optional<Internship> op;
        Internship i = null;
        if(id!=null){
            op = internshipRepository.findById(id);
            if(op.isPresent()){
                i = op.get();
            }
        }
        if(i==null){
            i = new Internship();
            i.setStudent(studentRepository.findById(studentId).get());
        }
        i.setId(id);
        i.setStartTime(dataRequest.getString("startTime"));
        i.setEndTime(dataRequest.getString("endTime"));
        i.setPosition(dataRequest.getString("position"));
        i.setCompany(dataRequest.getString("company"));
        i.setDescription(dataRequest.getString("description"));
        internshipRepository.save(i);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse internshipDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Optional op;
        Internship i = null;
        if(id!=null){
            op = internshipRepository.findById(id);
            if(op.isPresent()){
                i = (Internship) op.get();
                internshipRepository.delete(i);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
