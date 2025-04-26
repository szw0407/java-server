package cn.edu.sdu.java.server.services;
/*
 * 这个service的目的是让学生选课
 * 这个文件安排的功能包括
 * 批量给学生排课（比如说按照班级）
 * 学生自主选课
 * 安排授课老师和教学计划
 *
 * 请务必写对课程的教学计划，先在教学计划里面保证课程的成立，然后在学生的成绩表格里面成绩留空，教学计划和课程号写正确
 */


import cn.edu.sdu.java.server.models.TeachPlan;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
class TeachPlanManagementService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public TeachPlanManagementService(CourseRepository courseRepository, ScoreRepository scoreRepository, TeachPlanRepository teachPlanRepository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    // managing course plans

    public DataResponse createTeachPlanForClass(Integer courseId, Integer year, Integer semester, List<Integer> teacherIds, Integer teachPlanCode) {
        // check if the course exists
        var course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return CommonMethod.getReturnData("课程不存在");
        }
        var teachPlans = teachPlanRepository.filterByCourseTeacherSemesterYear(courseId, 0, semester, year);
        if (teachPlanCode != null) {
            // whether the one in plans.
        for (TeachPlan tp : teachPlans) {
            if (Objects.equals(tp.getTeachPlanCode(), teachPlanCode)) {
                // TODO remove the chosen lines. Use the new record provided instead
            }
        }
        } else {
            // no specific teach plan code, but we need to create one with no conflict with existing ones
            teachPlanCode = 0;
            for (TeachPlan tp : teachPlans) {
                if (tp.getTeachPlanCode() > teachPlanCode) {
                    teachPlanCode = tp.getTeachPlanCode();
                }
            }
            teachPlanCode++;  // this is even larger than the maximum code. I think there should not be bugs
        }
        // unless a specific teach plan is chosen via giving a code, the teach plan should be now created

        if (year == null || semester == null) {
            var calendar = Calendar.getInstance();

            if (year == null) {
                year = calendar.get(Calendar.YEAR);
            }
            if (semester == null) {
                semester = switch (calendar.get(Calendar.MONTH)) {
                    case Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                         Calendar.JUNE -> 2;
                    case Calendar.JULY, Calendar.AUGUST -> 3;
                    default -> 1;
                };  // Just the case in SDU i suppose
            }
        }
        // add the teachers
        List<Teacher> teachers = teacherRepository.findAllById(teacherIds);

        // now prepare a list to be put into dataResponse object
        List<TeachPlan> teachPlanRecords = new ArrayList<>();
        if (teachers.size() == 0) {
            // leave the teacher empty and create ONE record
        } else {
            for (Teacher teacher : teachers) {
                TeachPlan tp = new TeachPlan();
                tp.setCourse(course.get());
                tp.setTeachPlanCode(teachPlanCode);
                tp.setTeacher(teacher);
                tp.setYear(year);
                tp.setSemester(semester);
                teachPlanRecords.add(tp);
            }
        }
        return CommonMethod.getReturnData(teachPlanRecords);






    }
}