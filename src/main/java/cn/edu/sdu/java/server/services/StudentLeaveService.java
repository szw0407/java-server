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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;

@Service
public class StudentLeaveService {

    private final StudentLeaveRepository studentLeaveRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository; // 添加 TeacherRepo
    public StudentLeaveService(StudentLeaveRepository studentLeaveRepository, StudentRepository studentRepository,TeacherRepository teacherRepository) {
        this.studentLeaveRepository = studentLeaveRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository; // 初始化 TeacherRepo
    }

    // 获取请假记录列表
    public DataResponse getLeaveList(DataRequest dataRequest) {
        String studentName = dataRequest.getString("studentName");
        List<StudentLeave> leaveList = studentLeaveRepository.findLeaveListByStudentName(studentName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (StudentLeave leave : leaveList) {
            dataList.add(getMapFromStudentLeaveWithLocalTime(leave));
        }
        return CommonMethod.getReturnData(dataList);
    }

    private String getApprovalStatus(Boolean isApproved) {
        if (isApproved == null) {
            return "待审批";
        }
        return isApproved ? "同意" : "拒绝";
    }


    // 将 StudentLeave 转换为 Map，并格式化时间为本地时间
    private Map<String, Object> getMapFromStudentLeaveWithLocalTime(StudentLeave leave) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", leave.getId());
        map.put("studentId", leave.getStudent().getPersonId());
        map.put("studentName", leave.getStudent().getPerson().getName());
        map.put("college", leave.getCollege());
        map.put("startDate", formatToLocalTime(leave.getStartDate()));
        map.put("endDate", formatToLocalTime(leave.getEndDate()));
        map.put("reason", leave.getReason());
        map.put("approverId", leave.getApproverId());
        map.put("isApproved", getApprovalStatus(leave.getIsApproved()));
        return map;
    }

    // 格式化时间为本地时间字符串
    private String formatToLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
// 保存或更新请假记录
// 保存或更新请假记录
public DataResponse saveLeave(DataRequest dataRequest) {
    Map<String, Object> form = dataRequest.getMap("form");
    Integer leaveId = CommonMethod.getInteger(form, "leaveId");
    Integer studentId = CommonMethod.getInteger(form, "studentId");
    String studentName = CommonMethod.getString(form, "studentName");
    Integer approverId = CommonMethod.getInteger(form, "approverId");

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

    // 验证 approverId 是否为老师
    if (approverId != null) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(approverId);
        if (teacherOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("审批人ID无效，必须是老师的ID");
        }
    }

    // 获取并校验日期
    Date startDate = CommonMethod.getDate(form, "startDate");
    Date endDate = CommonMethod.getDate(form, "endDate");
    if (startDate != null && endDate != null && startDate.after(endDate)) {
        return CommonMethod.getReturnMessageError("开始日期不能晚于结束日期");
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
    leave.setStartDate(startDate);
    leave.setEndDate(endDate);
    leave.setReason(CommonMethod.getString(form, "reason"));
    leave.setApproverId(approverId);

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

    // 老师审批请假记录
    public DataResponse approveLeave(DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Boolean isApproved = dataRequest.getBoolean("isApproved");

        // 查找请假记录
        Optional<StudentLeave> leaveOptional = studentLeaveRepository.findById(leaveId);
        if (leaveOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("请假记录不存在");
        }

        StudentLeave leave = leaveOptional.get();
        leave.setIsApproved(isApproved); // 更新审批状态
        studentLeaveRepository.save(leave); // 保存更改

        return CommonMethod.getReturnMessageOK("审批成功");
    }

}

