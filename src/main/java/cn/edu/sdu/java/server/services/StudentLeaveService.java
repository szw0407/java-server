package cn.edu.sdu.java.server.services;
import cn.edu.sdu.java.server.models.StudentLeave;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentLeaveRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import java.util.*;

@Service
public class StudentLeaveService {
    private final StudentLeaveRepository studentLeaveRepository;
    private final StudentRepository studentRepository;

    public StudentLeaveService(StudentLeaveRepository studentLeaveRepository, StudentRepository studentRepository) {
        this.studentLeaveRepository = studentLeaveRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String, Object> getMapFromStudentLeave(StudentLeave leave) {
        Map<String, Object> map = new HashMap<>();
        if (leave == null) return map;
        map.put("id", leave.getId());
        map.put("studentId", leave.getStudent().getPersonId());
        map.put("reason", leave.getReason());
        map.put("startDate", leave.getStartDate());
        map.put("endDate", leave.getEndDate());
        map.put("status", leave.getStatus());
        return map;
    }
    
    public boolean checkStudentIdExists(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return false;
        }
        try {
            Integer id = Integer.parseInt(studentId);
            return studentRepository.existsById(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public List<Map<String, Object>> getStudentLeaveMapList(String numName) {
        List<StudentLeave> leaves = studentLeaveRepository.findAll();
        return leaves.stream()
                .filter(leave -> leave.getStudent().getPerson().getNum().contains(numName) || leave.getStudent().getPerson().getName().contains(numName))
                .map(this::getMapFromStudentLeave)
                .collect(Collectors.toList());
    }

    public DataResponse getStudentLeaveList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getStudentLeaveMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse studentLeaveDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id != null) {
            studentLeaveRepository.deleteById(id);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getStudentLeaveInfo(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Optional<StudentLeave> leave = studentLeaveRepository.findById(id);
        return leave.map(l -> CommonMethod.getReturnData(getMapFromStudentLeave(l)))
                .orElseGet(() -> CommonMethod.getReturnMessageError("Leave not found"));
    }

    public DataResponse studentLeaveEditSave(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map<String, Object> form = dataRequest.getMap("form");
        StudentLeave leave = null;
        if (id != null) {
            Optional<StudentLeave> op = studentLeaveRepository.findById(id);
            if (op.isPresent()) {
                leave = op.get();
            }
        }
        if (leave == null) {
            leave = new StudentLeave();
        }
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        if (studentId != null) {
            Optional<Student> studentOp = studentRepository.findById(studentId);
            studentOp.ifPresent(leave::setStudent);
        }
        leave.setReason(CommonMethod.getString(form, "reason"));
        leave.setStartDate(CommonMethod.getString(form, "startDate"));
        leave.setEndDate(CommonMethod.getString(form, "endDate"));
        leave.setStatus(CommonMethod.getString(form, "status"));
        studentLeaveRepository.save(leave);
        return CommonMethod.getReturnData(leave.getId());
    }
}

