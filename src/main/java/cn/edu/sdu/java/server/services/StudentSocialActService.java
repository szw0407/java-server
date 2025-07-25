package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentSocialActivity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.StudentSocialActRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StudentSocialActService {
    private final StudentSocialActRepository studentSocialActRepository;
    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    public StudentSocialActService(StudentSocialActRepository studentSocialActRepository,
                                   StudentRepository studentRepository,
                                   PersonRepository personRepository) {
        this.studentSocialActRepository = studentSocialActRepository;
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
    }
    public Map<String, Object> getMapFromStudentSocialActivity(StudentSocialActivity studentSocialAct) {
        Map<String, Object> m = new HashMap<>();
        if (studentSocialAct == null) {
            return m;
        }
        m.put("id", studentSocialAct.getId());

        // 检查 student 是否为 null
        if (studentSocialAct.getStudent() != null) {
            m.put("studentId", studentSocialAct.getStudent().getPerson().getNum());
        } else {
            m.put("studentId", null); // 或者根据需求设置默认值
        }

        m.put("name", studentSocialAct.getName());
        m.put("description", studentSocialAct.getDescription());
        m.put("type", studentSocialAct.getType());
        // 获取原始时间
        Date startTime = studentSocialAct.getStartTime();
        Date endTime = studentSocialAct.getEndTime();

// 加 8 小时（转北京时间）
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 只保留年月日

        if (startTime != null) {
            calendar.setTime(startTime);
            calendar.add(Calendar.HOUR_OF_DAY, 8); // 加8小时
            m.put("startTime", dateFormat.format(calendar.getTime())); // 转为字符串，只保留年月日
        }

        if (endTime != null) {
            calendar.setTime(endTime);
            calendar.add(Calendar.HOUR_OF_DAY, 8); // 加8小时
            m.put("endTime", dateFormat.format(calendar.getTime())); // 转为字符串，只保留年月日
        }
        m.put("location", studentSocialAct.getLocation());
        m.put("role", studentSocialAct.getRole());


        return m;
    }
    public List<Map<String,Object>> getStudentSocialActMapList(String studentId) {//这里是通过学号获取
        List<Map<String,Object>> dataList = new ArrayList<>();
        //System.out.println(numName);
        List<StudentSocialActivity> studentSocialActs = studentSocialActRepository.findByStudentPersonNum(studentId);

        for (StudentSocialActivity studentSocialAct : studentSocialActs) {

            Map<String, Object> m = getMapFromStudentSocialActivity(studentSocialAct);
            dataList.add(m);
        }
        return dataList;
    }
    public List<Map<String,Object>> getStudentSocialActMapList() {
        List<Map<String,Object>> dataList = new ArrayList<>();
        //System.out.println(numName);
        List<StudentSocialActivity> studentSocialActs = studentSocialActRepository.findAll();

        for (StudentSocialActivity studentSocialAct : studentSocialActs) {
            Map<String, Object> m = getMapFromStudentSocialActivity(studentSocialAct);

            dataList.add(m);
        }
        return dataList;
    }
    public DataResponse getList(DataRequest dataRequest) {
        //String numName = dataRequest.getString("");
        String studentId = dataRequest.getString("studentId");
        List<Map<String,Object>> dataList = new ArrayList<>();
        if (studentId == null || studentId.isEmpty()) {
            dataList = getStudentSocialActMapList();
        }else {
            dataList = getStudentSocialActMapList(studentId);
        }
        System.out.println(dataList);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public DataResponse studentSocialActEditSave(DataRequest dataRequest) throws ParseException {
        Map<String, Object> form = dataRequest.getMap("form");
        String studentIdStr = CommonMethod.getString(form, "studentId");
        StudentSocialActivity socialActivity = new StudentSocialActivity();

        if (studentIdStr != null && !studentIdStr.isEmpty()) {
            Optional<Student> studentOptional = studentRepository.findByPersonNum(studentIdStr);
            if (studentOptional.isPresent()) {
                socialActivity.setStudent(studentOptional.get());
            } else {
                return CommonMethod.getReturnMessageError("保存失败，您填写的学号不存在！");
            }
        }

        socialActivity.setName(CommonMethod.getString(form, "name"));
        socialActivity.setType(CommonMethod.getString(form, "type"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String startTimeStr = CommonMethod.getString(form, "startTime");
        String endTimeStr = CommonMethod.getString(form, "endTime");

        // 校验并解析开始时间
        if (startTimeStr != null && !startTimeStr.equals("请选择开始日期")) {
            try {
                socialActivity.setStartTime(dateFormat.parse(startTimeStr));
            } catch (ParseException e) {
                return CommonMethod.getReturnMessageError("开始日期格式错误，请重新选择！");
            }
        }

        // 校验并解析结束时间
        if (endTimeStr != null && !endTimeStr.equals("请选择结束日期")) {
            try {
                socialActivity.setEndTime(dateFormat.parse(endTimeStr));
            } catch (ParseException e) {
                return CommonMethod.getReturnMessageError("结束日期格式错误，请重新选择！");
            }
        }

        socialActivity.setLocation(CommonMethod.getString(form, "location"));
        socialActivity.setRole(CommonMethod.getString(form, "role"));
        socialActivity.setDescription(CommonMethod.getString(form, "description"));

        try {
            studentSocialActRepository.save(socialActivity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return CommonMethod.getReturnMessageError("数据已被其他用户修改，请刷新后重试！");
        }

        return CommonMethod.getReturnData(socialActivity.getId());
    }

    public DataResponse socialActDelete(DataRequest dataRequest) {
        Integer Id = dataRequest.getInteger("Id");
        StudentSocialActivity s = null;
        Optional<StudentSocialActivity> op;
        if (Id != null && Id > 0) {
            op = studentSocialActRepository.findById(Id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                studentSocialActRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}
