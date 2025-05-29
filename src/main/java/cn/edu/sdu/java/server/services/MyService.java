package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.sdu.java.server.util.CommonMethod.getPersonId;

@Service
public class MyService {
    private static final Logger log = LoggerFactory.getLogger(CourseSelectionService.class);

    private static StudentRepository studentRepository;
    private static ClassScheduleRepository classScheduleRepository;
    private static ScoreRepository scoreRepository;
    private static TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private static TeachPlanRepository teachPlanRepository;
    private final UserRepository userRepository;
    public MyService(StudentRepository studentRepository, ClassScheduleRepository classScheduleRepository, ScoreRepository scoreRepository, TeacherRepository teacherRepository, CourseRepository courseRepository, TeachPlanRepository teachPlanRepository, UserRepository userRepository) {
        MyService.studentRepository = studentRepository;
        this.classScheduleRepository = classScheduleRepository;
        MyService.scoreRepository = scoreRepository;
        MyService.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.userRepository = userRepository;
    }


    public static DataResponse getMyPlanList(@Valid DataRequest dataRequest) {
        var myid = CommonMethod.getPersonId();
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        // check if student available
        Student s = studentRepository.findByPerson_PersonId(myid);
        Teacher t = teacherRepository.findByPerson_PersonId(myid);
        List<ClassSchedule> plans = null;
        if (s == null && t == null) {
            return CommonMethod.getReturnData("error", "You are not a student or teacher.");
        }
        if (s != null) {
            plans = scoreRepository.findByStudentPersonId(myid).stream().map(
                    Score::getClassSchedule
            ).distinct().toList();
        } else {
            plans = teachPlanRepository.findByTeacherPersonId(myid).stream().map(
                    TeachPlan::getClassSchedule
            ).distinct().toList();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ClassSchedule classSchedule : plans) {
            Map<String, Object> m = new HashMap<>();
            m.put("classScheduleId", classSchedule.getClassScheduleId().toString());
            m.put("classNum", classSchedule.getClassNumber().toString());
            m.put("courseId", classSchedule.getCourse().getCourseId());
            m.put("courseName", classSchedule.getCourse().getName());
            m.put("courseNumber", classSchedule.getCourse().getNum());
            m.put("credit", classSchedule.getCourse().getCredit());
            m.put("semester", classSchedule.getSemester());
            m.put("year", classSchedule.getYear());
            m.put("classTime", classSchedule.getClassTime());
            m.put("classLocation", classSchedule.getClassLocation());
            m.put("teachers", classSchedule.getTeachers().stream().map(Teacher::getPerson).map(Person::getName).collect(Collectors.joining(",")));
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    public static DataResponse getMyCourseList(@Valid DataRequest dataRequest) {
        var myid = CommonMethod.getPersonId();
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        // check if student available
        Student s = studentRepository.findByPerson_PersonId(myid);
        Teacher t = teacherRepository.findByPerson_PersonId(myid);
        List<Course> cs;
        if (s == null && t == null) {
            return CommonMethod.getReturnData("error", "You are not a student or teacher.");
        }
        if (s != null) {
            cs = scoreRepository.findStudentSemesterCourses(myid, semester, year).stream()
                    .map(Score::getClassSchedule).map(ClassSchedule::getCourse).collect(Collectors.toList());
        } else {
            cs = teachPlanRepository.findTeacherSemesterPlans(myid, semester, year).stream()
                    .map( TeachPlan::getClassSchedule).map(ClassSchedule::getCourse).collect(Collectors.toList());
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (var c : cs) {
            Map<String, Object> m = new HashMap<>();
            m.put("courseId", c.getCourseId().toString());
            m.put("courseNum", c.getNum());
            m.put("courseName", c.getName());
            m.put("credit", c.getCredit());
            m.put("description", c.getDescription());
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    public static DataResponse getMyScoreList(@Valid DataRequest dataRequest) {
        var myid = CommonMethod.getPersonId();
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        // check if student available
        Student s = studentRepository.findByPerson_PersonId(myid);
        Teacher t = teacherRepository.findByPerson_PersonId(myid);
        List<Score> scores;
        if (s == null && t == null) {
            return CommonMethod.getReturnData("error", "You are not a student or teacher.");
        }
        if (s != null) {
            scores = scoreRepository.findStudentSemesterCourses(myid, semester, year);
        } else {
            scores = teachPlanRepository.findTeacherSemesterPlans(myid, semester, year).stream()
                    .flatMap(tp -> tp.getClassSchedule().getScores().stream()).collect(Collectors.toList());
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (var score : scores) {
            Map<String, Object> m = new HashMap<>();
            m.put("scoreId", score.getScoreId().toString());
            m.put("courseId", score.getClassSchedule().getCourse().getCourseId());
            m.put("courseNum", score.getClassSchedule().getCourse().getNum());
            m.put("courseName", score.getClassSchedule().getCourse().getName());
            m.put("classScheduleId", score.getClassSchedule().getClassScheduleId());
            m.put("classNumber", score.getClassSchedule().getClassNumber());
            m.put("semester", score.getClassSchedule().getSemester());
            m.put("year", score.getClassSchedule().getYear());
            m.put("credit", score.getClassSchedule().getCourse().getCredit());
            m.put("mark", score.getMark());
            m.put("studentNum", score.getStudent().getPerson().getNum());
            m.put("studentName", score.getStudent().getPerson().getName());
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    public static DataResponse getMyAvailableCourseList(@Valid DataRequest dataRequest) {

        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");

        var personId = getPersonId(); // 如果没有提供personId，则使用当前登录用户的ID
        if (personId == null) {
            return CommonMethod.getReturnMessageError("未能识别学生信息");
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
//            // 如果该课程的班级尚未被学生选择，则添加到可选列表
//            if (!selectedClassIds.contains(classSchedule.getClassScheduleId())) {
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
//            }
        }

        return CommonMethod.getReturnData(dataList);
    }
}
