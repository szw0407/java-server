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
    }

    public Map<String, Object> getMapFromStudentHonor(StudentHonor honor) {
        Map<String, Object> map = new HashMap<>();
        if (honor == null) return map;
        map.put("id", honor.getId());
        map.put("studentId", honor.getStudentId());
        map.put("title", honor.getTitle());
        map.put("description", honor.getDescription());
        return map;
    }

    public List<Map<String, Object>> getStudentHonorMapList(String numName) {
        List<StudentHonor> honors = studentHonorRepository.findAll();
        return honors.stream()
                .filter(honor -> honor.getTitle().contains(numName) || honor.getDescription().contains(numName))
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
    }

    public DataResponse studentHonorEditSave(DataRequest dataRequest) {
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

        Optional<Student> s=studentRepository.findById(CommonMethod.getInteger(form, "studentId"));
        if (!s.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        honor.setStudentId(CommonMethod.getInteger(form, "studentId"));
        honor.setTitle(CommonMethod.getString(form, "title"));
        honor.setDescription(CommonMethod.getString(form, "description"));
        studentHonorRepository.save(honor);



        return CommonMethod.getReturnData(honor.getId());
    }

    public ResponseEntity<StreamingResponseBody> getStudentHonorListExcl(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getStudentHonorMapList(numName);
        // Implement the logic to generate and return the Excel file
        // This is a placeholder for the actual implementation
        return ResponseEntity.ok().body(outputStream -> {
            // Write the Excel file to the outputStream
        });
    }
}