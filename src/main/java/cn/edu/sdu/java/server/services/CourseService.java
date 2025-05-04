package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public CourseService(CourseRepository courseRepository, ScoreRepository scoreRepository, TeachPlanRepository teachPlanRepository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId() + "");
            m.put("num", c.getNum());
            m.put("name", c.getName());
            m.put("credit", c.getCredit() + "");
            m.put("coursePath", c.getCoursePath());
            pc = c.getPreCourse();
            if (pc != null) {
                m.put("preCourse", pc.getName());
                m.put("preCourseId", pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse courseSave(DataRequest dataRequest) {
        /*
          This function updates or creates a new course
         */
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Optional<Course> op;
        Course c = null;

        if (courseId != null) {
            op = courseRepository.findById(courseId);
            if (op.isPresent())
                c = op.get();
        }
        if (c == null)
            c = new Course();
        Course pc = null;
        if (preCourseId != null) {
            op = courseRepository.findById(preCourseId);
            if (op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        courseRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c = null;  // 课程对象,默认为null
        if (courseId != null) {
            op = courseRepository.findById(courseId);
            if (op.isPresent()) {
                c = op.get();
                courseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getCourseByNum(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        Optional<Course> op;
        Course c = null;  // 课程对象,默认为null
        if (num != null) {
            op = courseRepository.findByNum(num);
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Map<String, Object> m = new HashMap<>();
        if (c != null) {
            m.put("courseId", c.getCourseId() + "");
            m.put("num", c.getNum());
            m.put("name", c.getName());
            m.put("credit", c.getCredit() + "");
            m.put("coursePath", c.getCoursePath());
            Course pc = c.getPreCourse();
            if (pc != null) {
                m.put("preCourse", pc.getName());
                m.put("preCourseId", pc.getCourseId());
            }
        }
        return CommonMethod.getReturnData(m);
    }

    public DataResponse getCourseByName(DataRequest dataRequest) {
        String name = dataRequest.getString("name");
        List<Course> cList = courseRepository.findByName(name);  //数据库查询操作
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId() + "");
            m.put("num", c.getNum());
            m.put("name", c.getName());
            m.put("credit", c.getCredit() + "");
            m.put("coursePath", c.getCoursePath());
            pc = c.getPreCourse();
            if (pc != null) {
                m.put("preCourse", pc.getName());
                m.put("preCourseId", pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCourseByNumName(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId() + "");
            m.put("num", c.getNum());
            m.put("name", c.getName());
            m.put("credit", c.getCredit() + "");
            m.put("coursePath", c.getCoursePath());
            pc = c.getPreCourse();
            if (pc != null) {
                m.put("preCourse", pc.getName());
                m.put("preCourseId", pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }


    public DataResponse getTeachPlans(Integer year, Integer semester, Integer courseID, Integer teacherID) {
        List<TeachPlan> plans;
        // if null assign 0
        if (year == null) year = 0;
        if (semester == null) semester = 0;
        if (courseID == null) courseID = 0;
        if (teacherID == null) teacherID = 0;

        plans = teachPlanRepository.filterByCourseTeacherSemesterYear(courseID, teacherID, semester, year);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (TeachPlan plan : plans) {
            Map<String, Object> data = new HashMap<>();
            data.put("teachPlanId", plan.getTeachPlanId());
            data.put("courseId", plan.getCourse().getCourseId());
            data.put("courseName", plan.getCourse().getName());
//            data.put("teacherId", plan.get.getPersonId());
//            data.put("teacherName", plan.getTeacher().getPerson().getName());
            data.put("year", plan.getYear());
            data.put("semester", plan.getSemester());
            dataList.add(data);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getTeachPlanInCurrentSemester(Integer courseID, Integer teacherID) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int semester = switch (calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE -> 2;
            case Calendar.JULY, Calendar.AUGUST -> 3;
            default -> 1;
        };

        return getTeachPlans(year, semester, courseID, teacherID);
    }

}
