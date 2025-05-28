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
            m.put("courseType", c.getCourseType() != null ? c.getCourseType() : "");
            m.put("department", c.getDepartment() != null ? c.getDepartment() : "");
            pc = c.getPreCourse();
            if (pc != null) {
                m.put("preCourse", pc.getName());
                m.put("preCourseId", pc.getCourseId());
            }
            m.put("description", c.getDescription() != null ? c.getDescription() : "");
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
        String courseType = dataRequest.getString("courseType");
        String department = dataRequest.getString("department");
        String description = dataRequest.getString("description");
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
        c.setCourseType(courseType);
        c.setDepartment(department);
        c.setDescription(description);
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


    

}
