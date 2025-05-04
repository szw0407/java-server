package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentCourseSelection;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学生选课服务
 * 提供学生选课、退课及课程表查询等功能
 */
@Service
public class StudentCourseSelectionService {

    private final StudentCourseSelectionRepository selectionRepository;
    private final StudentRepository studentRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final CourseRepository courseRepository;

    public StudentCourseSelectionService(
            StudentCourseSelectionRepository selectionRepository,
            StudentRepository studentRepository,
            TeachPlanRepository teachPlanRepository,
            ClassScheduleRepository classScheduleRepository,
            CourseRepository courseRepository) {
        this.selectionRepository = selectionRepository;
        this.studentRepository = studentRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.classScheduleRepository = classScheduleRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 学生选课
     * @param dataRequest 包含学生ID、教学班级ID
     * @return 选课结果
     */
    public DataResponse selectCourse(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        
        // 检查学生是否存在
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }
        
        // 检查教学班级是否存在
        Optional<TeachPlan> teachPlanOpt = teachPlanRepository.findById(teachPlanId);
        if (teachPlanOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        
        TeachPlan teachPlan = teachPlanOpt.get();
        
        // 检查是否已经选择了该教学班
        Optional<StudentCourseSelection> existingSelection = selectionRepository.findByStudent_PersonIdAndTeachPlan_TeachPlanId(
                studentId, teachPlanId);
        
        if (existingSelection.isPresent()) {
            StudentCourseSelection selection = existingSelection.get();
            if ("WITHDRAWN".equals(selection.getStatus())) {
                // 如果之前退选了，可以重新选择
                selection.setStatus("SELECTED");
                selection.setSelectionTime(new Date());
                selectionRepository.save(selection);
                return CommonMethod.getReturnMessageOK("选课成功");
            } else {
                // 已经选择了该教学班
                return CommonMethod.getReturnMessageError("您已经选择了该教学班");
            }
        }
        
        // 检查是否已经选择了同一课程的其他教学班
        List<StudentCourseSelection> sameCourseDifferentClass = selectionRepository.findBySameCourseDifferentClass(
                studentId, teachPlan.getCourse().getCourseId(), teachPlan.getYear(), teachPlan.getSemester());
        
        if (!sameCourseDifferentClass.isEmpty()) {
            return CommonMethod.getReturnMessageError("您已经选择了该课程的其他教学班级");
        }
        
        // 检查课程人数限制
        Integer currentStudentCount = selectionRepository.countStudentsByTeachPlanId(teachPlanId);
        
        if (teachPlan.getMaxStudentCount() != null && currentStudentCount >= teachPlan.getMaxStudentCount()) {
            return CommonMethod.getReturnMessageError("该教学班人数已满");
        }
        
        // 检查时间冲突
        List<ClassSchedule> teachPlanSchedules = classScheduleRepository.findByTeachPlan_TeachPlanId(teachPlanId);
        List<StudentCourseSelection> studentSelections = selectionRepository.findByStudentAndYearAndSemester(
                studentId, teachPlan.getYear(), teachPlan.getSemester());
        
        if (!teachPlanSchedules.isEmpty() && !studentSelections.isEmpty()) {
            for (StudentCourseSelection selection : studentSelections) {
                List<ClassSchedule> selectedSchedules = classScheduleRepository.findByTeachPlan_TeachPlanId(selection.getTeachPlan().getTeachPlanId());
                
                for (ClassSchedule schedule1 : teachPlanSchedules) {
                    for (ClassSchedule schedule2 : selectedSchedules) {
                        if (schedule1.getDayOfWeek().equals(schedule2.getDayOfWeek())) {
                            // 检查时间段是否重叠
                            if ((schedule1.getStartPeriod() <= schedule2.getEndPeriod() && 
                                 schedule1.getEndPeriod() >= schedule2.getStartPeriod())) {
                                return CommonMethod.getReturnMessageError("课程时间冲突，无法选课");
                            }
                        }
                    }
                }
            }
        }
        
        // 创建选课记录
        StudentCourseSelection selection = new StudentCourseSelection();
        selection.setStudent(studentOpt.get());
        selection.setTeachPlan(teachPlan);
        selection.setStatus("SELECTED");
        selection.setSelectionTime(new Date());
        selection.setCreditEarned(false);
        
        selectionRepository.save(selection);
        
        return CommonMethod.getReturnMessageOK("选课成功");
    }
    
    /**
     * 学生退课
     * @param dataRequest 包含学生ID、教学班级ID
     * @return 退课结果
     */
    public DataResponse withdrawCourse(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        
        // 检查学生选课记录是否存在
        Optional<StudentCourseSelection> selectionOpt = selectionRepository.findByStudent_PersonIdAndTeachPlan_TeachPlanId(
                studentId, teachPlanId);
        
        if (selectionOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("未选择该课程");
        }
        
        StudentCourseSelection selection = selectionOpt.get();
        
        if ("WITHDRAWN".equals(selection.getStatus())) {
            return CommonMethod.getReturnMessageError("已经退选过该课程");
        }
        
        // 更新选课状态为退选
        selection.setStatus("WITHDRAWN");
        selectionRepository.save(selection);
        
        return CommonMethod.getReturnMessageOK("退课成功");
    }
    
    /**
     * 获取学生当前学期选课列表
     * @param dataRequest 包含学生ID、学年、学期
     * @return 选课列表
     */
    public DataResponse getStudentCourseSelections(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        
        // 检查学生是否存在
        if (!studentRepository.existsById(studentId)) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }
        
        // 如果未提供年份和学期，使用当前学期
        if (year == null || semester == null) {
            Calendar calendar = Calendar.getInstance();
            if (year == null) {
                year = calendar.get(Calendar.YEAR);
            }
            if (semester == null) {
                semester = switch (calendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                         Calendar.JUNE -> 2;
                    case Calendar.JULY, Calendar.AUGUST -> 3;
                    default -> 1;
                };
            }
        }
        
        List<StudentCourseSelection> selections = selectionRepository.findByStudentAndYearAndSemester(
                studentId, year, semester);
        
        List<Map<String, Object>> selectionList = new ArrayList<>();
        
        for (StudentCourseSelection selection : selections) {
            TeachPlan teachPlan = selection.getTeachPlan();
            
            Map<String, Object> selectionMap = new HashMap<>();
            selectionMap.put("selectionId", selection.getSelectionId());
            selectionMap.put("teachPlanId", teachPlan.getTeachPlanId());
            selectionMap.put("courseId", teachPlan.getCourse().getCourseId());
            selectionMap.put("courseName", teachPlan.getCourse().getName());
            selectionMap.put("courseNum", teachPlan.getCourse().getNum());
            selectionMap.put("credit", teachPlan.getCourse().getCredit());
            selectionMap.put("className", teachPlan.getClassName());
            selectionMap.put("teachPlanCode", teachPlan.getTeachPlanCode());
            selectionMap.put("status", selection.getStatus());
            
            // 获取上课时间地点信息
            List<ClassSchedule> schedules = classScheduleRepository.findByTeachPlan_TeachPlanId(teachPlan.getTeachPlanId());
            List<Map<String, Object>> scheduleList = new ArrayList<>();
            
            for (ClassSchedule schedule : schedules) {
                Map<String, Object> scheduleMap = new HashMap<>();
                scheduleMap.put("dayOfWeek", schedule.getDayOfWeek());
                scheduleMap.put("dayOfWeekName", getDayOfWeekName(schedule.getDayOfWeek()));
                scheduleMap.put("startPeriod", schedule.getStartPeriod());
                scheduleMap.put("endPeriod", schedule.getEndPeriod());
                scheduleMap.put("timeRange", formatTimeRange(schedule.getStartPeriod(), schedule.getEndPeriod()));
                scheduleMap.put("building", schedule.getBuilding());
                scheduleMap.put("roomNumber", schedule.getRoomNumber());
                scheduleMap.put("location", schedule.getLocation());
                scheduleMap.put("address", formatAddress(schedule.getBuilding(), schedule.getRoomNumber(), schedule.getLocation()));
                scheduleList.add(scheduleMap);
            }
            
            selectionMap.put("schedules", scheduleList);
            selectionList.add(selectionMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("year", year);
        result.put("semester", semester);
        result.put("courses", selectionList);
        
        return CommonMethod.getReturnData(result);
    }
    
    /**
     * 获取可选课程列表
     * @param dataRequest 包含学生ID、学年、学期
     * @return 可选课程列表
     */
    public DataResponse getAvailableCourses(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        
        // 检查学生是否存在
        if (!studentRepository.existsById(studentId)) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }
        
        // 如果未提供年份和学期，使用当前学期
        if (year == null || semester == null) {
            Calendar calendar = Calendar.getInstance();
            if (year == null) {
                year = calendar.get(Calendar.YEAR);
            }
            if (semester == null) {
                semester = switch (calendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                         Calendar.JUNE -> 2;
                    case Calendar.JULY, Calendar.AUGUST -> 3;
                    default -> 1;
                };
            }
        }
        
        // 获取当前学期已开设的所有教学班级
        List<TeachPlan> availableTeachPlans = teachPlanRepository.findByYearAndSemester(year, semester);
        
        // 获取学生当前已选课程
        List<StudentCourseSelection> currentSelections = selectionRepository.findByStudentAndYearAndSemester(
                studentId, year, semester);
        
        // 已选课程的ID集合
        Set<Integer> selectedCourseIds = new HashSet<>();
        for (StudentCourseSelection selection : currentSelections) {
            selectedCourseIds.add(selection.getTeachPlan().getCourse().getCourseId());
        }
        
        List<Map<String, Object>> availableCoursesList = new ArrayList<>();
        
        // 按课程分组整理可选的教学班级
        Map<Integer, List<Map<String, Object>>> courseTeachPlansMap = new HashMap<>();
        
        for (TeachPlan teachPlan : availableTeachPlans) {
            Integer courseId = teachPlan.getCourse().getCourseId();
            
            // 如果已经选择了该课程，则跳过
            if (selectedCourseIds.contains(courseId)) {
                continue;
            }
            
            // 统计当前班级人数
            Integer currentStudentCount = selectionRepository.countStudentsByTeachPlanId(teachPlan.getTeachPlanId());
            
            Map<String, Object> teachPlanMap = new HashMap<>();
            teachPlanMap.put("teachPlanId", teachPlan.getTeachPlanId());
            teachPlanMap.put("className", teachPlan.getClassName());
            teachPlanMap.put("teachPlanCode", teachPlan.getTeachPlanCode());
            teachPlanMap.put("currentStudentCount", currentStudentCount);
            teachPlanMap.put("maxStudentCount", teachPlan.getMaxStudentCount());
            
            // 查询教师信息和课程安排
            // 获取上课时间地点信息
            List<ClassSchedule> schedules = classScheduleRepository.findByTeachPlan_TeachPlanId(teachPlan.getTeachPlanId());
            List<Map<String, Object>> scheduleList = new ArrayList<>();
            
            for (ClassSchedule schedule : schedules) {
                Map<String, Object> scheduleMap = new HashMap<>();
                scheduleMap.put("dayOfWeek", schedule.getDayOfWeek());
                scheduleMap.put("dayOfWeekName", getDayOfWeekName(schedule.getDayOfWeek()));
                scheduleMap.put("startPeriod", schedule.getStartPeriod());
                scheduleMap.put("endPeriod", schedule.getEndPeriod());
                scheduleMap.put("timeRange", formatTimeRange(schedule.getStartPeriod(), schedule.getEndPeriod()));
                scheduleMap.put("location", formatAddress(schedule.getBuilding(), schedule.getRoomNumber(), schedule.getLocation()));
                scheduleList.add(scheduleMap);
            }
            
            teachPlanMap.put("schedules", scheduleList);
            
            // 按课程ID分组
            courseTeachPlansMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(teachPlanMap);
        }
        
        // 将分组后的数据整合成最终结果
        for (Map.Entry<Integer, List<Map<String, Object>>> entry : courseTeachPlansMap.entrySet()) {
            Integer courseId = entry.getKey();
            List<Map<String, Object>> teachPlans = entry.getValue();
            
            // 找到对应的课程信息
            Optional<cn.edu.sdu.java.server.models.Course> courseOpt = courseRepository.findById(courseId);
            if (courseOpt.isEmpty()) {
                continue;
            }
            
            cn.edu.sdu.java.server.models.Course course = courseOpt.get();
            
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("courseId", courseId);
            courseMap.put("courseName", course.getName());
            courseMap.put("courseNum", course.getNum());
            courseMap.put("credit", course.getCredit());
            courseMap.put("description", course.getDescription());
            courseMap.put("teachPlans", teachPlans);
            
            availableCoursesList.add(courseMap);
        }
        
        return CommonMethod.getReturnData(availableCoursesList);
    }
    
    // 根据星期几编号获取名称
    private String getDayOfWeekName(Integer dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "周一";
            case 2: return "周二";
            case 3: return "周三";
            case 4: return "周四";
            case 5: return "周五";
            case 6: return "周六";
            case 7: return "周日";
            default: return "";
        }
    }
    
    // 格式化上课时间段
    private String formatTimeRange(Integer startPeriod, Integer endPeriod) {
        if (startPeriod.equals(endPeriod)) {
            return "第" + startPeriod + "节";
        } else {
            return "第" + startPeriod + "-" + endPeriod + "节";
        }
    }
    
    // 格式化地址
    private String formatAddress(String building, String roomNumber, String location) {
        if (building != null && !building.isEmpty() && roomNumber != null && !roomNumber.isEmpty()) {
            return building + " " + roomNumber;
        } else if (location != null && !location.isEmpty()) {
            return location;
        } else {
            return "";
        }
    }
}