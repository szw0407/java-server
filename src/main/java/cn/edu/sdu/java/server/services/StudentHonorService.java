package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentHonor;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentHonorRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class StudentHonorService {
    private final StudentHonorRepository studentHonorRepository;
    private final StudentRepository studentRepository;

    public StudentHonorService(StudentHonorRepository studentHonorRepository, StudentRepository studentRepository) {
        this.studentHonorRepository = studentHonorRepository;
        this.studentRepository = studentRepository;
    }    public Map<String, Object> getMapFromStudentHonor(StudentHonor honor) {
        Map<String, Object> map = new HashMap<>();
        if (honor == null) return map;
        map.put("id", honor.getId());
        map.put("studentId", honor.getStudentId());
        map.put("title", honor.getTitle());
        map.put("description", honor.getDescription());
        
        // 如果有学生关联，添加学生详细信息
        if (honor.getStudent() != null) {
            Student student = honor.getStudent();
            if (student.getPerson() != null) {
                map.put("studentName", student.getPerson().getName());
                map.put("studentNum", student.getPerson().getNum());
                map.put("className", student.getClassName());
                map.put("major", student.getMajor());
            }
        }
        
        return map;
    }    public List<Map<String, Object>> getStudentHonorMapList(String numName) {
        List<StudentHonor> honors;
        if (numName == null || numName.trim().isEmpty()) {
            honors = studentHonorRepository.findAll();
        } else {
            // 使用新的支持模糊查询的方法
            honors = studentHonorRepository.findByNumName(numName.trim());
        }
        return honors.stream()
                .map(this::getMapFromStudentHonor)
                .collect(Collectors.toList());
    }

    public DataResponse getStudentHonorList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getStudentHonorMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse studentHonorDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id != null) {
            studentHonorRepository.deleteById(id);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getStudentHonorInfo(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Optional<StudentHonor> honor = studentHonorRepository.findById(id);
        return honor.map(h -> CommonMethod.getReturnData(getMapFromStudentHonor(h)))
                .orElseGet(() -> CommonMethod.getReturnMessageError("Honor not found"));
    }    public DataResponse studentHonorEditSave(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map<String, Object> form = dataRequest.getMap("form");
        StudentHonor honor = null;
        if (id != null) {
            Optional<StudentHonor> op = studentHonorRepository.findById(id);
            if (op.isPresent()) {
                honor = op.get();
            }
        }
        if (honor == null) {
            honor = new StudentHonor();
        }

        // 查找学生实体并建立关联
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        
        // 设置学生关联关系
        honor.setStudent(studentOpt.get());
        honor.setTitle(CommonMethod.getString(form, "title"));
        honor.setDescription(CommonMethod.getString(form, "description"));
        studentHonorRepository.save(honor);

        return CommonMethod.getReturnData(honor.getId());
    }    public ResponseEntity<StreamingResponseBody> getStudentHonorListExcl(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        // TODO: 实现Excel导出功能
        // 目前返回空的Excel文件作为占位符
        return ResponseEntity.ok().body(outputStream -> {
            List<Map<String, Object>> dataList = getStudentHonorMapList(numName);
            // 实际应用中需要使用Apache POI等库来生成Excel文件
            // 这里暂时写入一个简单的文本作为占位符
            outputStream.write(("导出" + dataList.size() + "条学生荣誉记录").getBytes());
        });
    }
}