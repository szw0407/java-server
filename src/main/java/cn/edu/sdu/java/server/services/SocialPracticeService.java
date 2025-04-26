package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class SocialPracticeService {
    private static final Logger log = LoggerFactory.getLogger(SocialPracticeService.class);

    private final SocialPracticeRepository socialPracticeRepository;
    private final StudentRepository studentRepository;
    private final SystemService systemService;

    public SocialPracticeService(SocialPracticeRepository socialPracticeRepository, StudentRepository studentRepository, SystemService systemService) {
        this.socialPracticeRepository = socialPracticeRepository;
        this.studentRepository = studentRepository;
        this.systemService = systemService;
    }

    // 将社会实践对象转为Map
    public List<Map<String, Object>> getSocialPracticeMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<SocialPractice> spList = socialPracticeRepository.findSocialPracticeByNumName(numName);
        if(spList==null||spList.isEmpty()){
            return dataList;
        }
        for(SocialPractice sp : spList){
            dataList.add(getMapFromPractice(sp));
        }
        return dataList;
    }
    public Map<String, Object> getMapFromPractice(SocialPractice sp) {
        Map<String, Object> m = new HashMap<>();
        Student s;
        if(sp==null)
            return m;
        m.put("id", sp.getId());
        s = sp.getStudent();
        if(s==null)
            return m;
        m.put("studentId", s.getPersonId());
        m.put("practiceTime", sp.getPracticeTime());
        m.put("location", sp.getPracticeLocation());
        m.put("organization", sp.getPracticeOrganization());
        m.put("description", sp.getPracticeDescription());
        m.put("durationDays", sp.getDurationDays());
        return m;

    }

    // 获取社会实践列表
    public DataResponse getPracticeList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<SocialPractice> spList = socialPracticeRepository.findSocialPracticeByNumName(numName);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (SocialPractice sp : spList) {
            dataList.add(getMapFromPractice(sp));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getSocialPracticeList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getSocialPracticeMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    // 保存或更新社会实践记录
    public DataResponse practiceEditSave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        Integer id = CommonMethod.getInteger(form, "id");


        SocialPractice sp = null;

        Optional<Student> studentOpt = studentRepository.findByPersonPersonId(studentId);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        Student student = studentOpt.get();

        if (id == null) {
            Optional<SocialPractice> op = socialPracticeRepository.findById(id);
            if (!op.isPresent()) {
                sp = new SocialPractice();
                sp.setStudent(student);
            }
        }else{
            Optional<SocialPractice> op = socialPracticeRepository.findById(id);
            if (!op.isPresent()) {
                return CommonMethod.getReturnMessageError("该社会实践记录不存在");
            }
            sp = op.get();

        }

        sp.setPracticeTime(CommonMethod.getString(form, "practiceTime"));
        sp.setPracticeLocation(CommonMethod.getString(form, "location"));
        sp.setPracticeOrganization(CommonMethod.getString(form, "organization"));
        sp.setPracticeDescription(CommonMethod.getString(form, "description"));
        sp.setDurationDays(CommonMethod.getInteger(form, "durationDays"));

        socialPracticeRepository.save(sp);
        return CommonMethod.getReturnData(sp.getId());
    }
    
    public DataResponse practiceDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        socialPracticeRepository.deleteById(id);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getPracticeInfo(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        SocialPractice sp = null;
        Optional<SocialPractice> op;
        if (id != null) {
            op = socialPracticeRepository.findById(id);
            if (op.isPresent()) {
                sp = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromPractice(sp));
    }


    // 分页查询


}