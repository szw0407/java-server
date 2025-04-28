package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 教师课程管理服务
 * 提供教师查看课程、学生名单和打分的功能
 */
@Service
public class TeacherCourseManagementService {

    private final TeacherRepository teacherRepository;
    private final TeacherTeachPlanRoleRepository teacherTeachPlanRoleRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final StudentCourseSelectionRepository studentCourseSelectionRepository;
    private final ScoreRepository scoreRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final CourseRepository courseRepository;

    public TeacherCourseManagementService(
            TeacherRepository teacherRepository,
            TeacherTeachPlanRoleRepository teacherTeachPlanRoleRepository,
            TeachPlanRepository teachPlanRepository,
            StudentCourseSelectionRepository studentCourseSelectionRepository,
            ScoreRepository scoreRepository,
            ClassScheduleRepository classScheduleRepository,
            CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherTeachPlanRoleRepository = teacherTeachPlanRoleRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.studentCourseSelectionRepository = studentCourseSelectionRepository;
        this.scoreRepository = scoreRepository;
        this.classScheduleRepository = classScheduleRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 获取教师教授的课程列表
     * @param dataRequest 包含教师ID、学年和学期
     * @return 课程列表
     */
    public DataResponse getTeacherCourses(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        
        // 检查教师是否存在
        if (!teacherRepository.existsById(teacherId)) {
            return CommonMethod.getReturnMessageError("教师不存在");
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
        
        // 查询教师在特定学期教授的所有教学班级
        List<TeacherTeachPlanRole> teacherRoles = teacherTeachPlanRoleRepository.findByTeacherAndYearAndSemester(
                teacherId, year, semester);
        
        List<Map<String, Object>> coursesList = new ArrayList<>();
        
        for (TeacherTeachPlanRole role : teacherRoles) {
            TeachPlan teachPlan = role.getTeachPlan();
            Course course = teachPlan.getCourse();
            
            Map<String, Object> courseMap = new HashMap<>();
            courseMap.put("teachPlanId", teachPlan.getTeachPlanId());
            courseMap.put("courseId", course.getCourseId());
            courseMap.put("courseName", course.getName());
            courseMap.put("courseNum", course.getNum());
            courseMap.put("credit", course.getCredit());
            courseMap.put("courseType", course.getCourseType());
            courseMap.put("teachPlanCode", teachPlan.getTeachPlanCode());
            courseMap.put("className", teachPlan.getClassName());
            courseMap.put("year", teachPlan.getYear());
            courseMap.put("semester", teachPlan.getSemester());
            courseMap.put("teacherRole", role.getRole());
            
            // 获取学生人数
            Integer studentCount = studentCourseSelectionRepository.countStudentsByTeachPlanId(teachPlan.getTeachPlanId());
            courseMap.put("studentCount", studentCount);
            
            // 获取课程时间地点信息
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
            
            courseMap.put("schedules", scheduleList);
            coursesList.add(courseMap);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("teacherId", teacherId);
        result.put("year", year);
        result.put("semester", semester);
        result.put("courses", coursesList);
        
        return CommonMethod.getReturnData(result);
    }
    
    /**
     * 获取教学班级的学生名单
     * @param dataRequest 包含教学班级ID
     * @return 学生名单
     */
    public DataResponse getClassStudents(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        
        // 验证教师是否有权限查看该班级（是否是该班级的教师）
        List<TeacherTeachPlanRole> teacherRoles = Collections.singletonList(teacherTeachPlanRoleRepository.findByTeacher_PersonIdAndTeachPlan_TeachPlanId(
                teacherId, teachPlanId));

        if (teacherRoles.isEmpty()) {
            return CommonMethod.getReturnMessageError("您不是该班级的授课教师，无权查看");
        }

        // 查询该班级所有选课学生
        List<StudentCourseSelection> selections = studentCourseSelectionRepository.findByTeachPlan_TeachPlanIdAndStatusNot(
                teachPlanId, "WITHDRAWN");
        
        List<Map<String, Object>> studentList = new ArrayList<>();
        
        for (StudentCourseSelection selection : selections) {
            Student student = selection.getStudent();
            Person person = student.getPerson();
            
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("personId", student.getPersonId());
            studentMap.put("num", person.getNum());
            studentMap.put("name", person.getName());
            studentMap.put("dept", person.getDept());
            studentMap.put("gender", person.getGender());
            studentMap.put("email", person.getEmail());
            studentMap.put("phone", person.getPhone());
            
            // 获取成绩信息
            List<Score> scores = scoreRepository.findByStudentPersonIdAndTeachPlan_TeachPlanId(
                    student.getPersonId(), teachPlanId);
            
            if (!scores.isEmpty()) {
                Score score = scores.get(0);
                studentMap.put("scoreId", score.getScoreId());
                studentMap.put("mark", score.getMark());
                studentMap.put("ranking", score.getRanking());
            } else {
                studentMap.put("scoreId", null);
                studentMap.put("mark", null);
                studentMap.put("ranking", null);
            }
            
            studentList.add(studentMap);
        }
        
        // 获取教学班级信息
        Optional<TeachPlan> teachPlanOpt = teachPlanRepository.findById(teachPlanId);
        Map<String, Object> teachPlanInfo = new HashMap<>();
        
        if (teachPlanOpt.isPresent()) {
            TeachPlan teachPlan = teachPlanOpt.get();
            Course course = teachPlan.getCourse();
            
            teachPlanInfo.put("teachPlanId", teachPlan.getTeachPlanId());
            teachPlanInfo.put("courseId", course.getCourseId());
            teachPlanInfo.put("courseName", course.getName());
            teachPlanInfo.put("courseNum", course.getNum());
            teachPlanInfo.put("credit", course.getCredit());
            teachPlanInfo.put("className", teachPlan.getClassName());
            teachPlanInfo.put("year", teachPlan.getYear());
            teachPlanInfo.put("semester", teachPlan.getSemester());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("teachPlan", teachPlanInfo);
        result.put("students", studentList);
        result.put("studentCount", studentList.size());
        
        return CommonMethod.getReturnData(result);
    }
    
    /**
     * 教师为学生打分
     * @param dataRequest 包含成绩信息
     * @return 打分结果
     */
    @Transactional
    public DataResponse gradeStudent(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        Integer studentId = dataRequest.getInteger("studentId");
        Integer mark = dataRequest.getInteger("mark");
        
        // 验证教师是否有权限给该班级打分
        List<TeacherTeachPlanRole> teacherRoles = Collections.singletonList(teacherTeachPlanRoleRepository.findByTeacher_PersonIdAndTeachPlan_TeachPlanId(
                teacherId, teachPlanId));
        
        if (teacherRoles.isEmpty()) {
            return CommonMethod.getReturnMessageError("您不是该班级的授课教师，无权打分");
        }
        
        // 检查分数是否合法
        if (mark != null && (mark < 0 || mark > 100)) {
            return CommonMethod.getReturnMessageError("分数应在0-100分之间");
        }
        
        // 验证学生是否选了该课程
        Optional<StudentCourseSelection> selectionOpt = studentCourseSelectionRepository.findByStudent_PersonIdAndTeachPlan_TeachPlanId(
                studentId, teachPlanId);
        
        if (selectionOpt.isEmpty() || "WITHDRAWN".equals(selectionOpt.get().getStatus())) {
            return CommonMethod.getReturnMessageError("该学生未选择此课程或已退选");
        }
        
        // 查找现有成绩记录
        Optional<TeachPlan> teachPlanOpt = teachPlanRepository.findById(teachPlanId);
        if (teachPlanOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        
        TeachPlan teachPlan = teachPlanOpt.get();
        
        List<Score> scores = scoreRepository.findByStudentPersonIdAndTeachPlan_TeachPlanId(studentId, teachPlanId);
        Score score;
        
        if (scores.isEmpty()) {
            // 创建新成绩记录
            score = new Score();
            Optional<Student> studentOpt = studentCourseSelectionRepository.findByStudent_PersonIdAndTeachPlan_TeachPlanId(
                    studentId, teachPlanId).map(StudentCourseSelection::getStudent);
            
            if (studentOpt.isEmpty()) {
                return CommonMethod.getReturnMessageError("学生不存在");
            }
            
            score.setStudent(studentOpt.get());
            score.setCourse(teachPlan.getCourse());
            score.setTeachPlan(teachPlan);
        } else {
            score = scores.get(0);
        }
        
        // 更新分数
        score.setMark(mark);
        
        // 保存成绩
        scoreRepository.save(score);
        
        // 更新排名（可选）
        updateRankings(teachPlanId);
        
        // 如果分数>=60分，更新学生选课记录的学分获得状态
        if (mark != null && mark >= 60) {
            StudentCourseSelection selection = selectionOpt.get();
            selection.setCreditEarned(true);
            studentCourseSelectionRepository.save(selection);
        }
        
        return CommonMethod.getReturnMessageOK("成绩录入成功");
    }
    
    /**
     * 教师批量导入成绩
     * @param dataRequest 包含批量成绩信息
     * @return 导入结果
     */
    @Transactional
    public DataResponse batchGradeStudents(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        List<Map<String, Object>> studentScores = (List<Map<String, Object>>) dataRequest.get("studentScores");
        
        // 验证教师是否有权限给该班级打分
        List<TeacherTeachPlanRole> teacherRoles = Collections.singletonList(teacherTeachPlanRoleRepository.findByTeacher_PersonIdAndTeachPlan_TeachPlanId(
                teacherId, teachPlanId));
        
        if (teacherRoles.isEmpty()) {
            return CommonMethod.getReturnMessageError("您不是该班级的授课教师，无权打分");
        }
        
        // 验证教学班级是否存在
        Optional<TeachPlan> teachPlanOpt = teachPlanRepository.findById(teachPlanId);
        if (teachPlanOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        
        TeachPlan teachPlan = teachPlanOpt.get();
        Course course = teachPlan.getCourse();
        
        int successCount = 0;
        int failedCount = 0;
        List<String> failedReasons = new ArrayList<>();
        
        for (Map<String, Object> studentScore : studentScores) {
            Integer studentId = CommonMethod.getInteger(studentScore, "studentId");
            Integer mark = CommonMethod.getInteger(studentScore, "mark");
            
            if (studentId == null || mark == null) {
                failedCount++;
                failedReasons.add("学生ID或分数为空");
                continue;
            }
            
            // 检查分数是否合法
            if (mark < 0 || mark > 100) {
                failedCount++;
                failedReasons.add("学生" + studentId + "的分数不在0-100分之间");
                continue;
            }
            
            // 验证学生是否选了该课程
            Optional<StudentCourseSelection> selectionOpt = studentCourseSelectionRepository.findByStudent_PersonIdAndTeachPlan_TeachPlanId(
                    studentId, teachPlanId);
            
            if (selectionOpt.isEmpty() || "WITHDRAWN".equals(selectionOpt.get().getStatus())) {
                failedCount++;
                failedReasons.add("学生" + studentId + "未选择此课程或已退选");
                continue;
            }
            
            // 查找现有成绩记录
            List<Score> scores = scoreRepository.findByStudentPersonIdAndTeachPlan_TeachPlanId(studentId, teachPlanId);
            Score score;
            
            if (scores.isEmpty()) {
                // 创建新成绩记录
                score = new Score();
                Optional<Student> studentOpt = selectionOpt.map(StudentCourseSelection::getStudent);
                
                if (studentOpt.isEmpty()) {
                    failedCount++;
                    failedReasons.add("学生" + studentId + "不存在");
                    continue;
                }
                
                score.setStudent(studentOpt.get());
                score.setCourse(course);
                score.setTeachPlan(teachPlan);
            } else {
                score = scores.get(0);
            }
            
            // 更新分数
            score.setMark(mark);
            
            // 保存成绩
            scoreRepository.save(score);
            
            // 如果分数>=60分，更新学生选课记录的学分获得状态
            if (mark >= 60) {
                StudentCourseSelection selection = selectionOpt.get();
                selection.setCreditEarned(true);
                studentCourseSelectionRepository.save(selection);
            }
            
            successCount++;
        }
        
        // 更新排名
        if (successCount > 0) {
            updateRankings(teachPlanId);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("failedReasons", failedReasons);
        
        return CommonMethod.getReturnData(result);
    }
    
    /**
     * 更新课程排名
     * @param teachPlanId 教学班级ID
     */
    private void updateRankings(Integer teachPlanId) {
        // 获取该班级所有有成绩的学生
        List<Score> scores = scoreRepository.findByTeachPlan_TeachPlanIdOrderByMarkDesc(teachPlanId);
        
        // 更新排名
        int rank = 1;
        Integer lastMark = null;
        int sameRankCount = 0;
        
        for (Score score : scores) {
            Integer currentMark = score.getMark();
            
            if (currentMark == null) {
                // 跳过没有成绩的学生
                continue;
            }
            
            if (lastMark != null && !currentMark.equals(lastMark)) {
                // 如果分数不同，排名应该是当前位置
                rank += sameRankCount;
                sameRankCount = 1;
            } else {
                sameRankCount++;
            }
            
            score.setRanking(rank);
            lastMark = currentMark;
        }
        
        scoreRepository.saveAll(scores);
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