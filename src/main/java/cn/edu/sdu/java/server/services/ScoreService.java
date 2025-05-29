package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.ClassScheduleRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScoreService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final ClassScheduleRepository classScheduleRepository;
    public ScoreService(CourseRepository courseRepository, ScoreRepository scoreRepository, StudentRepository studentRepository, ClassScheduleRepository classScheduleRepository) {
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
        this.classScheduleRepository = classScheduleRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> sList = courseRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse getScoreListOfStudent(DataRequest dataRequest) {
        var sList = new ArrayList<Score>();List<Map<String,Object>> dataList = new ArrayList<>();String num = dataRequest.getString("studentNum");String courseNum = dataRequest.getString("courseNum");
        if(num == null) {
            if (courseNum == null) {
                sList = (ArrayList<Score>) scoreRepository.findAll();
            } else {
                sList = (ArrayList<Score>) scoreRepository.findByClassSchedule_Course_Num(courseNum);
            }
        }

        else

        if (courseNum == null) {
            sList = (ArrayList<Score>) scoreRepository.findByStudent_Person_Num(num);
        } else {
            sList = (ArrayList<Score>) scoreRepository.findByStudent_Person_NumAndClassSchedule_Course_Num(num, courseNum);
        }
        Map<String,Object> m;
        for (Score s : sList) {
            m = new HashMap<>();
            m.put("scoreId", s.getScoreId()+"");
            m.put("personId",s.getStudent().getPersonId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseNum",s.getCourse().getNum());
            m.put("courseName",s.getCourse().getName());
            m.put("teachClassNum", s.getClassSchedule().getClassNumber());
            m.put("term", s.getClassSchedule().getSemester());
            m.put("year", s.getClassSchedule().getYear());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("mark",s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse getScoreList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if(personId == null)
            personId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<Score> sList = scoreRepository.findByStudentPersonIdAndCourseId(personId, courseId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Score s : sList) {
            m = new HashMap<>();
            m.put("scoreId", s.getScoreId()+"");
            m.put("personId",s.getStudent().getPersonId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseNum",s.getCourse().getNum());
            m.put("courseName",s.getCourse().getName());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("mark",""+s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse scoreSave(DataRequest dataRequest) {
        String personNum = dataRequest.getString("studentNum");
        Integer classId = dataRequest.getInteger("classId");
        Integer mark = dataRequest.getInteger("mark");
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op;
        Score s = null;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            // check if the score HAS EXISTS
            op = scoreRepository.findByStudent_Person_NumAndClassScheduleClassScheduleId(personNum, classId);
            if (op.isPresent()) {
                if (op.get().getMark() != null) {
                    return CommonMethod.getReturnMessageError("该学生在该教学班级已经有成绩记录，无法重复添加，请使用修改功能");
                } else {
                    s = op.get(); // 如果存在但没有成绩，则使用现有记录
                }
            } else {
               return CommonMethod.getReturnMessageError("该学生在该教学班级没有选课记录，无法添加成绩");
            }
        }
        s.setMark(mark);
        scoreRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse scoreDelete(DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op;
        Score s = null;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);
            if(op.isPresent()) {
                s = op.get();
                s.setMark(null);
                scoreRepository.save(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
