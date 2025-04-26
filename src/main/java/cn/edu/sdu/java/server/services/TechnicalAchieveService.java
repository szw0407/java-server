package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;

import cn.edu.sdu.java.server.repositorys.AcademicCompetitionRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

@Service
public class TechnicalAchieveService {
    private final TechnicalAchieveRespository technicalAchieveRespository;
    private final StudentRepository studentRepository;
    @Autowired
    public TechnicalAchieveService(TechnicalAchieveRespository technicalAchieveRespository, StudentRepository studentRepository) {
        this.technicalAchieveRespository = technicalAchieveRespository;
        this.studentRepository = studentRepository;
    }
    public Map<String, Object> getMapFromTechnicalAchieve(TechnicalAchieve ta) {
        Map<String, Object> m = new HashMap<>();
        Student s;
        if(ta==null)
            return m;
        m.put("id", ta.getId());
        s = ta.getStudent();
        if(s==null)
            return m;
        m.put("studentId", s.getPersonId());
        m.put("subject", ta.getSubject());
        m.put("description", ta.getDescription());
        m.put("achievement", ta.getAchievement());
        return m;
    }
    public List<Map<String, Object>> getTechnicalAchieveMapList(String numName) {
        List<Map<String, Object>> datalist = new ArrayList<>();
        List<TechnicalAchieve> taList = technicalAchieveRespository.findTechnicalAchieveByNumName(numName);
        if(taList==null||taList.isEmpty())
            return datalist;
        for (TechnicalAchieve ta : taList) {
            datalist.add(getMapFromTechnicalAchieve(ta));
        }
        return datalist;
    }
    public DataResponse getTechnicalAchieveList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getTechnicalAchieveMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse technicalAchieveDelete(DataRequest dataRequest){
        Integer id = dataRequest.getInteger("id");
        if (id != null && id > 0) {
            technicalAchieveRespository.deleteById(id);
            return CommonMethod.getReturnMessageOK();
        }
        return CommonMethod.getReturnMessageError("无效的竞赛记录ID");
    }
    public DataResponse getTechnicalAchieveInfo(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        TechnicalAchieve ta = null;
        Optional<TechnicalAchieve> op;
        if(id!=null&&id>0){
            op = technicalAchieveRespository.findById(id);
            if(op.isPresent()){
                ta = op.get();
                return CommonMethod.getReturnData(getMapFromTechnicalAchieve(ta));
            }else{
                return CommonMethod.getReturnMessageError("记录不存在");
            }
        }else{
            return CommonMethod.getReturnMessageError("无效的科技创新ID");
        }

    }
    public DataResponse technicalAchieveEditSave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form, "studentId");

        Optional<Student> studentOpt = studentRepository.findByPersonPersonId(studentId);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        Student student = studentOpt.get();
        TechnicalAchieve ta = null;

        if (id == null) {
            Optional<TechnicalAchieve> op = technicalAchieveRespository.findById(id);
            if (!op.isPresent()) {
                ta = new TechnicalAchieve();
                ta.setStudent(student);
            }

        }else{
            Optional<TechnicalAchieve> op = technicalAchieveRespository.findById(id);
            if (!op.isPresent()) {
                return CommonMethod.getReturnMessageError("该科技Achieve记录不存在");
            }
            ta = op.get();
        }
        ta.setSubject(CommonMethod.getString(form, "subject"));
        ta.setDescription(CommonMethod.getString(form, "description"));
        ta.setAchievement(CommonMethod.getString(form, "achievement"));

        technicalAchieveRespository.save(ta);
        return CommonMethod.getReturnData(ta.getId(),"保存成功");
    }
}
