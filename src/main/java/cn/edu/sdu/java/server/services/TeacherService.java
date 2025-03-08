package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
//    private final PersonRepository personRepository;
//    private final UserRepository userRepository;
//    private final UserTypeRepository userTypeRepository; // 用户类型数据操作自动注入
//    private final PasswordEncoder encoder; // 密码服务自动注入
//    private final FeeRepository feeRepository;

    public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository,
                          PersonRepository personRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder, FeeRepository feeRepository
                          ) {
        this.teacherRepository = teacherRepository;
//        this.userRepository = userRepository;
//        this.personRepository = personRepository;
//        this.userTypeRepository = userTypeRepository;
//        this.encoder = encoder;
//        this.feeRepository = feeRepository;
    }

    public Map<String, Object> getMapFromTeacher(Teacher t) {
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (t == null) {
            return m;
        }
        p = t.getPerson();
        m.put("personId", p.getPersonId());
        m.put("num", p.getNum());
        m.put("name", p.getName());
        m.put("type", p.getType());
        m.put("dept", p.getDept());
        m.put("card", p.getCard());
        m.put("gender", p.getGender());
        m.put("birthday", p.getBirthday());
        m.put("email", p.getEmail());
        m.put("phone", p.getPhone());
        m.put("address", p.getAddress());
        m.put("introduce", p.getIntroduce());
        m.put("degree", t.getDegree());
        m.put("title", t.getTitle());
        m.put("enterTime", t.getEnterTime());
        m.put("studentNum", t.getStudentNum());
        return m;
    }

    public DataResponse getTeacherMaps(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        // 通过编号或姓名查询教师信息
        List<Map<String, Object>> l = new ArrayList<>();
        List<Teacher> ts = teacherRepository.findTeacherListByNumName(numName);
        for (Teacher t : ts) {
            l.add(getMapFromTeacher(t));
        }
        return CommonMethod.getReturnData(l);
        
    }

}