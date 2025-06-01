package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeachPlanService {
    private static final Logger log = LoggerFactory.getLogger(TeachPlanService.class);
    
    private final TeachPlanRepository teachPlanRepository;
    private final TeacherRepository teacherRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final CourseRepository courseRepository;
    private final SystemService systemService;
    private final ScoreRepository scoreRepository;

    public TeachPlanService(TeachPlanRepository teachPlanRepository, TeacherRepository teacherRepository, 
                           ClassScheduleRepository classScheduleRepository, CourseRepository courseRepository,
                           SystemService systemService, ScoreRepository scoreRepository) {
        this.teachPlanRepository = teachPlanRepository;
        this.teacherRepository = teacherRepository;
        this.classScheduleRepository = classScheduleRepository;
        this.courseRepository = courseRepository;
        this.systemService = systemService;
        this.scoreRepository = scoreRepository;
    }

    /**
     * 获取教师选项列表
     */
    public OptionItemList getTeacherOptionList(DataRequest dataRequest) {
        List<Teacher> teacherList = teacherRepository.findTeacherListByNumName("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Teacher t : teacherList) {
            itemList.add(new OptionItem(t.getPersonId(), t.getPersonId()+"", t.getPerson().getNum()+"-"+t.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    
    /**
     * 获取课程选项列表
     */
    public OptionItemList getCourseOptionList(DataRequest dataRequest) {
        List<Course> courseList = courseRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : courseList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    
    /**
     * 获取教师的教学计划列表
     */
    public DataResponse getTeacherPlanList(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        
        List<TeachPlan> plans;
        if (teacherId != null && semester != null && year != null) {
            plans = teachPlanRepository.findTeacherSemesterPlans(teacherId, semester, year);
        } else if (teacherId != null) {
            plans = teachPlanRepository.findByTeacherPersonId(teacherId);
        } else {
            plans = teachPlanRepository.findAll();
        }
        
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (TeachPlan plan : plans) {
            Map<String, Object> m = new HashMap<>();
            m.put("teachPlanId", plan.getTeachPlanId());
            m.put("teacherId", plan.getTeacher().getPersonId());
            m.put("teacherName", plan.getTeacher().getPerson().getName());
            m.put("classScheduleId", plan.getClassSchedule().getClassScheduleId());
            m.put("courseId", plan.getClassSchedule().getCourse().getCourseId());
            m.put("courseName", plan.getClassSchedule().getCourse().getName());
            m.put("courseNumber", plan.getClassSchedule().getCourse().getNum());
            m.put("semester", plan.getClassSchedule().getSemester());
            m.put("year", plan.getClassSchedule().getYear());
            m.put("classTime", plan.getClassSchedule().getClassTime());
            m.put("classLocation", plan.getClassSchedule().getClassLocation());
            dataList.add(m);
        }
        
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 获取当前学期的教学班级列表
     */
    public DataResponse getCurrentSemesterClasses(DataRequest dataRequest) {
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        
        List<ClassSchedule> classes = classScheduleRepository.findCurrentSemesterClasses(semester, year);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ClassSchedule classSchedule : classes) {
            Map<String, Object> m = new HashMap<>();
            m.put("classScheduleId", classSchedule.getClassScheduleId().toString());
            m.put("courseId", classSchedule.getCourse().getCourseId());
            m.put("courseName", classSchedule.getCourse().getName());
            m.put("classNumber", classSchedule.getClassNumber());
            m.put("courseNumber", classSchedule.getCourse().getNum());
            m.put("semester", classSchedule.getSemester());
            m.put("year", classSchedule.getYear());
            m.put("classTime", classSchedule.getClassTime());
            m.put("classLocation", classSchedule.getClassLocation());
            m.put("teacherIds", classSchedule.getTeachers().stream()
                    .map(Teacher::getPersonId)
                    .collect(Collectors.toList()));
            dataList.add(m);
        }
        
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 添加教师到教学计划
     */
    public DataResponse addTeacherToPlan(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer classScheduleId = dataRequest.getInteger("classScheduleId");
        
        if (teacherId == null || classScheduleId == null) {
            return CommonMethod.getReturnMessageError("教师ID或教学班级ID不能为空");
        }
        
        Optional<Teacher> teacherOp = teacherRepository.findById(teacherId);
        Optional<ClassSchedule> classScheduleOp = classScheduleRepository.findById(classScheduleId);
        
        if (teacherOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教师不存在");
        }
        
        if (classScheduleOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        
        // 检查教师是否已经分配到该教学班级
        Optional<TeachPlan> existingPlan = teachPlanRepository.findByTeacherPersonIdAndClassScheduleClassScheduleId(
                teacherId, classScheduleId);
                
        if (existingPlan.isPresent()) {
            return CommonMethod.getReturnMessageError("该教师已分配到此教学班级");
        }
        
        // 创建新的教学计划
        TeachPlan teachPlan = new TeachPlan();
        teachPlan.setClassSchedule(classScheduleOp.get());
        teachPlan.setTeacher(teacherOp.get());
        
        teachPlanRepository.save(teachPlan);
        systemService.modifyLog(teachPlan, true);
        
        return CommonMethod.getReturnMessageOK();
    }
    
    /**
     * 移除教师的教学计划
     */
    public DataResponse removeTeacherPlan(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("classScheduleId");
        
        if (id == null) {
            return CommonMethod.getReturnMessageError("教学计划ID不能为空");
        }
        
        Optional<ClassSchedule> planOp = classScheduleRepository.findByClassScheduleId(id);
        if (planOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学计划不存在");
        }
        // check teach plans
        List<TeachPlan> teachPlans = teachPlanRepository.findTeachPlansByClassSchedule(planOp.get());
        // delete all teach plans
        teachPlanRepository.deleteAll(teachPlans);
        classScheduleRepository.deleteById(planOp.get().getClassScheduleId());
        return CommonMethod.getReturnMessageOK();
    }
    
    /**
     * 开设当前学期的教学班级
     */
    public DataResponse createClassSchedule(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        Integer classNumber = dataRequest.getInteger("classNumber");
        String classTime = dataRequest.getString("classTime");
        String classLocation = dataRequest.getString("classLocation");
        if (courseId == null || semester == null || year == null || classNumber == null) {
            return CommonMethod.getReturnMessageError("课程ID、学期、年份和班号不能为空");
        }
        var teacherIds = dataRequest.getList("teacherIds");
        
        Optional<Course> courseOp = courseRepository.findById(courseId);
        if (courseOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }
        
        // 检查该班号在该学期是否已存在
        Optional<ClassSchedule> existingClass = classScheduleRepository.findByClassNumberAndSemesterAndYearAndCourse_CourseId(
                classNumber, semester, year, courseId);
                
        if (existingClass.isPresent()) {
            return CommonMethod.getReturnMessageError("该班号在本学期已存在");
        }
        
        // 创建新的教学班级
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setCourse(courseOp.get());
        classSchedule.setClassNumber(classNumber);
        classSchedule.setSemester(semester);
        classSchedule.setYear(year);
        classSchedule.setClassTime(classTime);
        classSchedule.setClassLocation(classLocation);
        classScheduleRepository.save(classSchedule);
        // clear all teachers
        teachPlanRepository.deleteAll(teachPlanRepository.findTeachPlansByClassSchedule(classSchedule));

        for (var teacherid: teacherIds) {
            // add this teacherid (it should be integer)
            Integer _id = (Integer) teacherid;
            Optional<Teacher> teacherOp = teacherRepository.findById(_id);
            if (teacherOp.isEmpty()) {
                return CommonMethod.getReturnMessageError("教师ID " + _id + " 不存在");
            }

            var teachplan = new TeachPlan();
            teachplan.setClassSchedule(classSchedule);
            teachplan.setTeacher(teacherOp.get());

            teachPlanRepository.save(teachplan);

        }

        systemService.modifyLog(classSchedule, true);

        return CommonMethod.getReturnData(classSchedule.getClassScheduleId());
    }
    
    /**
     * 修改教学班级信息
     */
    public DataResponse updateClassSchedule(DataRequest dataRequest) {
        Integer classScheduleId = dataRequest.getInteger("classScheduleId");
        String classTime = dataRequest.getString("classTime");
        String classLocation = dataRequest.getString("classLocation");
        Integer classNum= dataRequest.getInteger("classNumber");
        String semester = dataRequest.getString("semester");
        String year = dataRequest.getString("year");
        Integer courseId = dataRequest.getInteger("courseId");

        Course course = courseRepository.findById(courseId).get();
        List<Integer> ids = (List<Integer>) dataRequest.getList("teacherIds");

        if (classScheduleId == null) {
            return CommonMethod.getReturnMessageError("教学班级ID不能为空");
        }
        
        Optional<ClassSchedule> classScheduleOp = classScheduleRepository.findById(classScheduleId);
        if (classScheduleOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        
        ClassSchedule classSchedule = classScheduleOp.get();

            classSchedule.setClassTime(classTime);
            classSchedule.setSemester(semester);
            classSchedule.setYear(year);


            classSchedule.setClassLocation(classLocation);

            classSchedule.setClassNumber(classNum);
            classSchedule.setCourse(course);

        
        classScheduleRepository.save(classSchedule);
        // 处理老师
        teachPlanRepository.deleteAll(teachPlanRepository.findTeachPlansByClassSchedule(classSchedule));
        for (Integer teacherId : ids) {
            Optional<Teacher> teacherOp = teacherRepository.findById(teacherId);
            if (teacherOp.isPresent()) {
                // 检查教师是否已经分配到该教学班级
                Optional<TeachPlan> existingPlan = teachPlanRepository.findByTeacherPersonIdAndClassScheduleClassScheduleId(
                        teacherId, classScheduleId);
                if (!existingPlan.isPresent()) {
                    TeachPlan teachPlan = new TeachPlan();
                    teachPlan.setClassSchedule(classSchedule);
                    teachPlan.setTeacher(teacherOp.get());
                    teachPlanRepository.save(teachPlan);
                }
            } else {
                return CommonMethod.getReturnMessageError("教师ID " + teacherId + " 不存在");
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getCoursePlanList(@Valid DataRequest dataRequest) {
        String courseNum = dataRequest.getString("courseNum");
        if (courseNum == null) {
            return CommonMethod.getReturnMessageError("课程编号不能为空");
        }

        List<TeachPlan> plans = teachPlanRepository.findByClassSchedule_Course_Num(courseNum);

        return CommonMethod.getReturnData(plans.stream().map(plan -> {
            Map<String, Object> m = new HashMap<>();
            m.put("teachPlanId", plan.getTeachPlanId());
            m.put("teacherId", plan.getTeacher().getPersonId());
            m.put("teacherName", plan.getTeacher().getPerson().getName());
            m.put("classScheduleId", plan.getClassSchedule().getClassScheduleId());
            m.put("courseId", plan.getClassSchedule().getCourse().getCourseId());
            m.put("courseName", plan.getClassSchedule().getCourse().getName());
            m.put("courseNumber", plan.getClassSchedule().getCourse().getNum());
            m.put("semester", plan.getClassSchedule().getSemester());
            m.put("year", plan.getClassSchedule().getYear());
            m.put("classTime", plan.getClassSchedule().getClassTime());
            m.put("classLocation", plan.getClassSchedule().getClassLocation());
            return m;
        }).collect(Collectors.toList()));

    }

    public DataResponse checkTeachPlanByInfo(@Valid DataRequest dataRequest) {
        String year = dataRequest.getString("year");
        String semester = dataRequest.getString("semester");
        String courseNum = dataRequest.getString("courseNum");
        Integer classNumber = dataRequest.getInteger("classNumber");

        if (year == null || semester == null || courseNum == null || classNumber == null) {
            return CommonMethod.getReturnMessageError("学年、学期、课程编号和班级号不能为空");
        }

        List<ClassSchedule> plans = classScheduleRepository.findByCourse_NumAndYearAndSemester(
                courseNum, year, semester);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (var plan : plans) {
            Map<String, Object> m = new HashMap<>();

            m.put("classScheduleId", plan.getClassScheduleId());

            dataList.add(m);
        }
        if (dataList.isEmpty()) {
            return CommonMethod.getReturnMessageError("没有找到对应的教学计划");
        }
        if (dataList.size() > 1) {
            return CommonMethod.getReturnMessageError("找到多个教学计划，请检查数据库，存在异常数据需要运维排查");
        }
        return CommonMethod.getReturnData(dataList.getFirst());
    }

    public DataResponse getStudentPlanList(@Valid DataRequest dataRequest) {
//        Integer studentId = dataRequest.getInteger("studentId");
        String studentNum = dataRequest.getString("studentNum");
        if (studentNum == null) {
            return CommonMethod.getReturnMessageError("学生ID不能为空");
        }

        List<ClassSchedule> plans = scoreRepository.findByStudent_Person_Num(studentNum).stream()
                .map(Score::getClassSchedule).toList();


        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ClassSchedule classSchedule : plans) {
            Map<String, Object> m = new HashMap<>();
            m.put("classScheduleId", classSchedule.getClassScheduleId());
            m.put("classNum", classSchedule.getClassNumber());
            m.put("courseId", classSchedule.getCourse().getCourseId());
            m.put("courseName", classSchedule.getCourse().getName());
            m.put("courseNumber", classSchedule.getCourse().getNum());
            m.put("credit", classSchedule.getCourse().getCredit());
            m.put("semester", classSchedule.getSemester());
            m.put("year", classSchedule.getYear());
            m.put("classTime", classSchedule.getClassTime());
            m.put("classLocation", classSchedule.getClassLocation());
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }
}
