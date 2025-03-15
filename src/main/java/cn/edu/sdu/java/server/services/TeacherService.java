package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(StudentService.class);
    private final TeacherRepository teacherRepository;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SystemService systemService;

    public TeacherService(TeacherRepository teacherRepository, PersonRepository personRepository, UserRepository userRepository, PasswordEncoder encoder, SystemService systemService) {
        this.teacherRepository = teacherRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.systemService = systemService;
    }

    public Map<String, Object> getMapFromTeacher(Teacher t) {
        /* generate A Map from a Teacher object */
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (t == null) {
            return m;
        }
        p = t.getPerson();
        m.put("degree", t.getDegree());
        m.put("title", t.getTitle());
        m.put("enterTime", t.getEnterTime());
        m.put("studentNum", t.getStudentNum());
        if (p == null) {
            return m;
        }
        m.put("personId", t.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    public List<Map<String, Object>> getTeacherMapList(String numName) {
        /* get a list of teacher map from a list of teacher */
        List<Teacher> teacherList = teacherRepository.findTeacherListByNumName(numName);
        List<Map<String, Object>> teacherMapList = new ArrayList<>();
        if (teacherList == null || teacherList.isEmpty()) {
            return teacherMapList;
        }
        for (Teacher t : teacherList) {
            teacherMapList.add(getMapFromTeacher(t));
        }
        return teacherMapList;
    }

    public DataResponse getTeacherList(DataRequest req) {
        /* get a list of teacher map from a list of teacher */
        String numName = req.getString("numName");
        List<Map<String, Object>> teacherMapList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(teacherMapList);
    }


}