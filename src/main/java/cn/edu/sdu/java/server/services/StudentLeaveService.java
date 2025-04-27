package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.StudentLeave;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentLeaveRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class StudentLeaveService {

    private final StudentLeaveRepository studentLeaveRepository;
    private final StudentRepository studentRepository;

    public StudentLeaveService(StudentLeaveRepository studentLeaveRepository, StudentRepository studentRepository) {
        this.studentLeaveRepository = studentLeaveRepository;
        this.studentRepository = studentRepository;
    }

    // 获取请假记录列表
    public DataResponse getLeaveList(DataRequest dataRequest) {
        String studentName = dataRequest.getString("studentName");
        List<StudentLeave> leaveList = studentLeaveRepository.findLeaveListByStudentName(studentName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (StudentLeave leave : leaveList) {
            dataList.add(getMapFromStudentLeave(leave));
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 获取单条请假记录详情
    public DataResponse getLeaveInfo(DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Optional<StudentLeave> leaveOptional = studentLeaveRepository.findById(leaveId);
        if (leaveOptional.isPresent()) {
            return CommonMethod.getReturnData(getMapFromStudentLeave(leaveOptional.get()));
        }
        return CommonMethod.getReturnMessageError("请假记录不存在");
    }

    // 保存或更新请假记录
    public DataResponse saveLeave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer leaveId = CommonMethod.getInteger(form, "leaveId");
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        String studentName = CommonMethod.getString(form, "studentName");

        // 验证学生是否存在
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在，无法添加请假记录");
        }
        Student student = studentOptional.get();
        String actualStudentName = student.getPerson().getName();
        if (!actualStudentName.equals(studentName)) {
            return CommonMethod.getReturnMessageError("学生ID和姓名不匹配，无法添加请假记录");
        }


        // 验证 studentId 和 college 是否匹配
        String actualCollege = student.getPerson().getDept();
        String formCollege = CommonMethod.getString(form, "college");
        if (!actualCollege.equals(formCollege)) {
            return CommonMethod.getReturnMessageError("学生ID和学院不匹配，无法添加请假记录");
        }

        StudentLeave leave = null;
        if (leaveId != null) {
            Optional<StudentLeave> leaveOptional = studentLeaveRepository.findById(leaveId);
            if (leaveOptional.isPresent()) {
                leave = leaveOptional.get();
            }
        }
        if (leave == null) {
            leave = new StudentLeave();
        }

        leave.setStudent(studentOptional.get());
        leave.setStudentName(studentName); // 记录 studentName
        leave.setCollege(CommonMethod.getString(form, "college"));
        leave.setStartDate(CommonMethod.getDate(form, "startDate"));
        leave.setEndDate(CommonMethod.getDate(form, "endDate"));
        leave.setReason(CommonMethod.getString(form, "reason"));
        leave.setApproverId(CommonMethod.getInteger(form, "approverId"));
        leave.setIsApproved(CommonMethod.getBoolean(form, "isApproved"));

        studentLeaveRepository.save(leave);
        return CommonMethod.getReturnMessageOK();
    }

    // 删除请假记录
    public DataResponse deleteLeave(DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        studentLeaveRepository.deleteById(leaveId);
        return CommonMethod.getReturnMessageOK();
    }

    // 导出请假记录为 Excel 文件
    public ResponseEntity<StreamingResponseBody> exportLeaveData(DataRequest dataRequest) {
        String studentName = dataRequest.getString("studentName");
        List<StudentLeave> leaveList = studentLeaveRepository.findLeaveListByStudentName(studentName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Leave Records");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Student Name", "College", "Start Date", "End Date", "Reason", "Approver ID", "Is Approved"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowIndex = 1;
        for (StudentLeave leave : leaveList) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(leave.getId());
            row.createCell(1).setCellValue(leave.getStudent().getPerson().getName());
            row.createCell(2).setCellValue(leave.getCollege());
            row.createCell(3).setCellValue(leave.getStartDate().toString());
            row.createCell(4).setCellValue(leave.getEndDate().toString());
            row.createCell(5).setCellValue(leave.getReason());
            row.createCell(6).setCellValue(leave.getApproverId() != null ? leave.getApproverId().toString() : "");
            row.createCell(7).setCellValue(leave.getIsApproved() ? "Yes" : "No");
        }

        StreamingResponseBody stream = outputStream -> {
            workbook.write(outputStream);
            workbook.close();
        };

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=leave_records.xlsx")
                .body(stream);
    }

    // 分页获取请假记录
    public DataResponse getLeavePageData(DataRequest dataRequest) {
        String studentName = dataRequest.getString("studentName");
        Integer currentPage = dataRequest.getCurrentPage();
        int pageSize = 10; // 每页记录数，可根据需求调整

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<StudentLeave> leavePage = studentLeaveRepository.findLeavePageByStudentName(studentName, pageable);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (StudentLeave leave : leavePage.getContent()) {
            dataList.add(getMapFromStudentLeave(leave));
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("dataList", dataList);
        responseData.put("totalPages", leavePage.getTotalPages());
        responseData.put("totalElements", leavePage.getTotalElements());

        return CommonMethod.getReturnData(responseData);
    }

    // 将 StudentLeave 转换为 Map
    private Map<String, Object> getMapFromStudentLeave(StudentLeave leave) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", leave.getId());
        map.put("studentId", leave.getStudent().getPersonId());
        map.put("studentName", leave.getStudent().getPerson().getName());
        map.put("college", leave.getCollege());
        map.put("startDate", leave.getStartDate());
        map.put("endDate", leave.getEndDate());
        map.put("reason", leave.getReason());
        map.put("approverId", leave.getApproverId()); // 添加审批人ID
        map.put("isApproved", leave.getIsApproved()); // 添加审批状态
        return map;
    }
}

