package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ClassScheduleRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.edu.sdu.java.server.util.CommonMethod.getPersonId;

/**
 * 学生选课退课服务
 */
@Service
public class CourseSelectionService {
    private static final Logger log = LoggerFactory.getLogger(CourseSelectionService.class);

    private final StudentRepository studentRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final ScoreRepository scoreRepository;
    private final SystemService systemService;
    private final CourseRepository courseRepository;
    public CourseSelectionService(StudentRepository studentRepository,
                                 ClassScheduleRepository classScheduleRepository,
                                 ScoreRepository scoreRepository,
                                 SystemService systemService,
                                  CourseRepository courseRepository
    ) {
        this.studentRepository = studentRepository;
        this.classScheduleRepository = classScheduleRepository;
        this.scoreRepository = scoreRepository;
        this.systemService = systemService;
        this.courseRepository = courseRepository;
    }

    /**
     * 获取学生的已选课程列表
     */
    public DataResponse getSelectedCourses(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String semester = dataRequest.getString("semester");

        String year = dataRequest.getString("year");
        
        if (personId == null) {
            personId = getPersonId(); // 如果没有提供personId，则使用当前登录用户的ID
            if (personId == null) {
                return CommonMethod.getReturnMessageError("未能识别学生信息");
            }
        }
        
        List<Score> scores;
        if (semester != null && year != null) {
            // 查询特定学期的选课
            scores = scoreRepository.findStudentSemesterCourses(personId, semester, year);
        } else {
            // 查询所有选课
            scores = scoreRepository.findByStudentPersonId(personId);
        }
        
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Score score : scores) {
            Map<String, Object> m = new HashMap<>();
            m.put("scoreId", score.getScoreId());
            m.put("classScheduleId", score.getClassSchedule().getClassScheduleId());
            m.put("courseId", score.getClassSchedule().getCourse().getCourseId());
            m.put("courseName", score.getClassSchedule().getCourse().getName());
            m.put("courseNum", score.getClassSchedule().getCourse().getNum());
            m.put("credit", score.getClassSchedule().getCourse().getCredit());
            m.put("classNumber", score.getClassSchedule().getClassNumber());
            m.put("semester", score.getClassSchedule().getSemester());
            m.put("year", score.getClassSchedule().getYear());
            m.put("classTime", score.getClassSchedule().getClassTime());
            m.put("classLocation", score.getClassSchedule().getClassLocation());
            m.put("mark", score.getMark());
            m.put("ranking", score.getRanking());
            dataList.add(m);
        }
        
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 获取学生可选的课程列表
     */
    public DataResponse getAvailableCourses(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        
        if (personId == null) {
            personId = getPersonId(); // 如果没有提供personId，则使用当前登录用户的ID
            if (personId == null) {
                return CommonMethod.getReturnMessageError("未能识别学生信息");
            }
        }
        
        if (semester == null || year == null) {
            return CommonMethod.getReturnMessageError("学期和年份不能为空");
        }
        
        // 获取当前学期所有课程班级
        List<ClassSchedule> allClasses = classScheduleRepository.findCurrentSemesterClasses(semester, year);
        
        // 获取学生已选课程的ID列表
        List<Score> selectedScores = scoreRepository.findStudentSemesterCourses(personId, semester, year);
        Set<Integer> selectedCourseIds = new HashSet<>();
        Set<Integer> selectedClassIds = new HashSet<>();
        List<Score> selectedScoreHistory = scoreRepository.findByStudentPersonId(personId);
        for (Score score : selectedScores) {
            selectedCourseIds.add(score.getClassSchedule().getCourse().getCourseId());
            selectedClassIds.add(score.getClassSchedule().getClassScheduleId());
        }
        
        // 筛选可选课程班级
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ClassSchedule classSchedule : allClasses) {
            // 如果该课程的班级尚未被学生选择，则添加到可选列表
            if (!selectedClassIds.contains(classSchedule.getClassScheduleId())) {
                // 如果precourse没有被选择，禁止选课
                Course course = classSchedule.getCourse();
                if (course.getPreCourse() != null) {
                    Optional<Score> preCourseScore = selectedScoreHistory.stream()
                            .filter(s -> s.getClassSchedule().getCourse().getCourseId().equals(course.getPreCourse().getCourseId()))
                            .findFirst();
                    if (preCourseScore.isEmpty()) {
                        continue; // 如果前置课程未被选，则跳过
                    }
                }
                Map<String, Object> m = new HashMap<>();
                m.put("classScheduleId", classSchedule.getClassScheduleId());
                m.put("courseId", classSchedule.getCourse().getCourseId());
                m.put("courseName", classSchedule.getCourse().getName());
                m.put("courseNum", classSchedule.getCourse().getNum());
                m.put("credit", classSchedule.getCourse().getCredit());
                m.put("classNumber", classSchedule.getClassNumber());
                m.put("semester", classSchedule.getSemester());
                m.put("year", classSchedule.getYear());
                m.put("classTime", classSchedule.getClassTime());
                m.put("classLocation", classSchedule.getClassLocation());
                // 显示这个课程的有关老师
                List<String> teachers = new ArrayList<>();
                for (var teacher : classSchedule.getTeachers()) {
                    teachers.add(teacher.getPerson().getName());
                }
                m.put("teachers", teachers);
                // 标记该课程是否已经被学生选择（不同班级）
                m.put("courseSelected", selectedCourseIds.contains(classSchedule.getCourse().getCourseId()));
                
                dataList.add(m);
            }
        }
        
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 选课
     */
    public DataResponse selectCourse(DataRequest dataRequest) {
        Integer personId = getPersonId();
        Integer classScheduleId = dataRequest.getInteger("classScheduleId");
        
        if (personId == null) {
            personId = getPersonId(); // 如果没有提供personId，则使用当前登录用户的ID
            if (personId == null) {
                return CommonMethod.getReturnMessageError("未能识别学生信息");
            }
        }
        
        if (classScheduleId == null) {
            return CommonMethod.getReturnMessageError("教学班级ID不能为空");
        }
        
        // 检查学生是否存在
        Optional<Student> studentOp = studentRepository.findById(personId);
        if (studentOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }
        Student student = studentOp.get();
        
        // 检查教学班级是否存在
        Optional<ClassSchedule> classOp = classScheduleRepository.findById(classScheduleId);
        if (classOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        ClassSchedule classSchedule = classOp.get();
        
        // 检查学生是否已选择该教学班级
        Optional<Score> existingScoreOp = scoreRepository.findByStudentPersonIdAndClassScheduleClassScheduleId(
                personId, classScheduleId);
        if (existingScoreOp.isPresent()) {
            return CommonMethod.getReturnMessageError("已选择该教学班级，不能重复选择");
        }
        
        // 检查学生是否已选择该课程的其他教学班级
        List<Score> existingCourseScores = scoreRepository.findByStudentPersonIdAndCourseId(
                personId, classSchedule.getCourse().getCourseId());
        if (!existingCourseScores.isEmpty()) {
            return CommonMethod.getReturnMessageError("该课程的其他教学班级已被选择，不能重复选择同一课程");
        }
        
        // 创建新的成绩记录
        Score score = new Score();
        score.setStudent(student);
        score.setClassSchedule(classSchedule);
        score.setMark(null); // 初始成绩为空
        score.setRanking(null); // 初始排名为空
        
        scoreRepository.save(score);
        systemService.modifyLog(score, true);
        
        return CommonMethod.getReturnMessageOK("选课成功");
    }
    
    /**
     * 退课
     */
    public DataResponse dropCourse(DataRequest dataRequest) {
//        Integer personId = dataRequest.getInteger("personId");
        Integer scoreId = dataRequest.getInteger("scoreId");
        Integer personId = getPersonId();
        
        if (personId == null) {
            personId = getPersonId(); // 如果没有提供personId，则使用当前登录用户的ID
            if (personId == null) {
                return CommonMethod.getReturnMessageError("未能识别学生信息");
            }
        }
        
        if (scoreId == null) {
            return CommonMethod.getReturnMessageError("成绩记录ID不能为空");
        }
        
        // 检查成绩记录是否存在
        Optional<Score> scoreOp = scoreRepository.findById(scoreId);
        if (!scoreOp.isPresent()) {
            return CommonMethod.getReturnMessageError("选课记录不存在");
        }
        
        Score score = scoreOp.get();
        
        // 检查成绩记录是否属于该学生
        if (!score.getStudent().getPersonId().equals(personId)) {
            return CommonMethod.getReturnMessageError("无权操作此选课记录");
        }
        
        // 检查是否已有成绩
        if (score.getMark() != null) {
            return CommonMethod.getReturnMessageError("该课程已有成绩，不能退课");
        }
        
        // 删除成绩记录
        scoreRepository.deleteById(scoreId);
        
        return CommonMethod.getReturnMessageOK("退课成功");
    }

    public DataResponse getSelectedCoursesAll(@Valid DataRequest dataRequest) {
        var semester = dataRequest.getString("semester");
        var year = dataRequest.getString("year");
        if (semester == null || year == null) {
            return CommonMethod.getReturnMessageError("学期和年份不能为空");
        }
        var data = scoreRepository.findBySemesterAndYear(semester, year);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (var o : data) {
            Map<String, Object> m = new HashMap<>();
            m.put("scoreId", o.getScoreId());
            m.put("studentId", o.getStudent().getPersonId());
            m.put("studentNum", o.getStudent().getPerson().getNum());
            m.put("studentName", o.getStudent().getPerson().getName());
            m.put("classScheduleId", o.getClassSchedule().getClassScheduleId());
            m.put("courseId", o.getClassSchedule().getCourse().getCourseId());
            m.put("courseName", o.getClassSchedule().getCourse().getName());
            m.put("courseNum", o.getClassSchedule().getCourse().getNum());
            m.put("credit", o.getClassSchedule().getCourse().getCredit());
            m.put("classNumber", o.getClassSchedule().getClassNumber().toString());
            m.put("semester", o.getClassSchedule().getSemester());
            m.put("year", o.getClassSchedule().getYear());
            m.put("classTime", o.getClassSchedule().getClassTime());
            m.put("classLocation", o.getClassSchedule().getClassLocation());
            m.put("mark", o.getMark());
            m.put("ranking", o.getRanking());
            m.put("teachers", o.getClassSchedule().getTeachers().stream()
                    .map(t -> t.getPerson().getName()).toList());
            dataList.add(m);
        }

        if (dataList.isEmpty()) {
            return CommonMethod.getReturnMessageError("没有选课记录");
        } else {
            return CommonMethod.getReturnData(dataList);
        }
    }

    public DataResponse getAvailableCoursesAll(@Valid DataRequest dataRequest) {
        var semester = dataRequest.getString("semester");
        var year = dataRequest.getString("year");
        if (semester == null || year == null) {
            return CommonMethod.getReturnMessageError("学期和年份不能为空");
        }

        // 获取当前学期所有课程班级
        List<ClassSchedule> allClasses = classScheduleRepository.findCurrentSemesterClasses(semester, year);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ClassSchedule classSchedule : allClasses) {
            Map<String, Object> m = new HashMap<>();
            m.put("classScheduleId", classSchedule.getClassScheduleId());
            m.put("courseId", classSchedule.getCourse().getCourseId());
            m.put("courseName", classSchedule.getCourse().getName());
            m.put("courseNum", classSchedule.getCourse().getNum());
            m.put("credit", classSchedule.getCourse().getCredit());
            m.put("classNumber", classSchedule.getClassNumber().toString());
            m.put("semester", classSchedule.getSemester());
            m.put("year", classSchedule.getYear());
            m.put("classTime", classSchedule.getClassTime());
            m.put("classLocation", classSchedule.getClassLocation());

            // 显示这个课程的有关老师
            List<String> teachers = new ArrayList<>();
            for (var teacher : classSchedule.getTeachers()) {
                teachers.add(teacher.getPerson().getName());
            }
            m.put("teachers", teachers);

            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse selectCourseForStudent(@Valid DataRequest dataRequest) {
        String studentNum = dataRequest.getString("studentNum");
//        Integer classScheduleId = dataRequest.getInteger("classScheduleId");
        String courseNum = dataRequest.getString("courseNum");
        String year = dataRequest.getString("year");
        String semester = dataRequest.getString("semester");
        Integer teachClassNum = dataRequest.getInteger("classNum");
        if (studentNum == null || courseNum == null || year == null || semester == null || teachClassNum == null) {
            return CommonMethod.getReturnMessageError("学生ID和教学班级ID不能为空");
        }
        Optional<Course> courseOp = courseRepository.findByNum(courseNum);
        if (courseOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("这个课程不存在");
        }
        // 查找这个课程对应的班级号码有没有匹配的
        Optional<ClassSchedule> classOp = classScheduleRepository.findByClassNumberAndSemesterAndYearAndCourse_CourseId(
                teachClassNum, semester, year, courseOp.get().getCourseId());
        if (classOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("这个教学班级不存在");
        }
        // 检查学生是否存在
        Optional<Student> studentOp = studentRepository.findByPersonNum(studentNum);
        if (studentOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("这个学生不存在");
        }
        Student student = studentOp.get();

        // 检查教学班级是否存在
        ClassSchedule classSchedule = classOp.get();

        // 检查学生是否已选择该教学班级
        Optional<Score> existingScoreOp = scoreRepository.findByStudentPersonIdAndClassScheduleClassScheduleId(
                studentOp.get().getPersonId(), classSchedule.getClassScheduleId());
        if (existingScoreOp.isPresent()) {
            return CommonMethod.getReturnMessageError("已选择该教学班级，不能重复选择");
        }

        // 创建新的成绩记录
        Score score = new Score();
        score.setStudent(student);
        score.setClassSchedule(classSchedule);
        score.setMark(null); // 初始成绩为空
        score.setRanking(null); // 初始排名为空

        scoreRepository.save(score);
        systemService.modifyLog(score, true);

        return CommonMethod.getReturnMessageOK("选课成功");
    }

    public DataResponse dropCourseForStudent(@Valid DataRequest dataRequest) {
        String studentNum = dataRequest.getString("studentNum");
        Integer classNum = dataRequest.getInteger("classNum");
        String courseNum = dataRequest.getString("courseNum");
        String year = dataRequest.getString("year");
        String semester = dataRequest.getString("semester");

        var score = scoreRepository.findByStudent_Person_NumAndClassSchedule_Course_NumAndClassSchedule_ClassNumberAndClassSchedule_yearAndClassSchedule_semester(
                studentNum, courseNum, classNum, year, semester);
        // 检查是否已有成绩
        if (score == null) {
            return CommonMethod.getReturnMessageError("没有找到这个选课记录");
        }
        if (score.getMark() != null) {
            return CommonMethod.getReturnMessageError("该课程已有成绩，不能退课");
        }

        // 删除成绩记录
        scoreRepository.deleteById(score.getScoreId());

        return CommonMethod.getReturnMessageOK("退课成功");
    }
}
