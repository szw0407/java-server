package cn.edu.sdu.java.server.services;
/*
* 这个service的目的是让老师给学生打分。
*
* 需要注意两件事：
* * 其一，课程成绩和课程挂钩固然是正确的，但是考虑到重修等其他的复杂的情况，需要给学生打分的时候，必须绑定新增加的教学计划栏目
* * 其二，由于上述情况的存在，如果需要查询分数的时候，请务必找对课程教学计划，或者查询一个课程对应的以列表形式体现的每次重修的分数
*
*
*/
import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ClassScheduleRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GradingService {
    private static final Logger log = LoggerFactory.getLogger(GradingService.class);

    private final ScoreRepository scoreRepository;
    private final TeacherRepository teacherRepository;
    private final ClassScheduleRepository classScheduleRepository;

    public GradingService(ScoreRepository scoreRepository, TeacherRepository teacherRepository, 
                        ClassScheduleRepository classScheduleRepository) {
        this.scoreRepository = scoreRepository;
        this.teacherRepository = teacherRepository;
        this.classScheduleRepository = classScheduleRepository;
    }

    /**
     * 获取教师所教授班级的学生成绩列表
     */
    public DataResponse getStudentScores(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer classScheduleId = dataRequest.getInteger("classScheduleId");
        
        if (teacherId == null) {
            teacherId = CommonMethod.getPersonId(); // 如果没有提供teacherId，则使用当前登录用户的ID
        }
        
        if (classScheduleId == null) {
            return CommonMethod.getReturnMessageError("教学班级ID不能为空");
        }
        
        // 检查教师是否存在
        Optional<Teacher> teacherOp = teacherRepository.findById(teacherId);
        if (teacherOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教师不存在");
        }
        
        // 检查教学班级是否存在
        Optional<ClassSchedule> classOp = classScheduleRepository.findById(classScheduleId);
        if (classOp.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班级不存在");
        }
        
        // 获取该教学班级的所有学生成绩记录
        List<Score> scores = scoreRepository.findByClassSchedule_ClassScheduleId(classScheduleId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        for (Score score : scores) {
            Map<String, Object> m = new HashMap<>();
            m.put("scoreId", score.getScoreId());
            m.put("studentId", score.getStudent().getPersonId());
            m.put("studentNum", score.getStudent().getPerson().getNum());
            m.put("studentName", score.getStudent().getPerson().getName());
            m.put("classScheduleId", score.getClassSchedule().getClassScheduleId());
            m.put("courseName", score.getClassSchedule().getCourse().getName());
            m.put("mark", score.getMark());
            m.put("ranking", score.getRanking());
            dataList.add(m);
        }
        
        return CommonMethod.getReturnData(dataList);
    }
    
    /**
     * 提交学生成绩
     */
    public DataResponse submitScore(DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Integer mark = dataRequest.getInteger("mark");
        
        if (scoreId == null) {
            return CommonMethod.getReturnMessageError("成绩记录ID不能为空");
        }
        
        if (mark == null) {
            return CommonMethod.getReturnMessageError("成绩不能为空");
        }
        
        // 检查成绩记录是否存在
        Optional<Score> scoreOp = scoreRepository.findById(scoreId);
        if (!scoreOp.isPresent()) {
            return CommonMethod.getReturnMessageError("成绩记录不存在");
        }
        
        Score score = scoreOp.get();
        score.setMark(mark);
        
        // 计算排名
        updateRankings(score.getClassSchedule().getClassScheduleId());
        
        return CommonMethod.getReturnMessageOK("成绩提交成功");
    }
    
    /**
     * 批量提交学生成绩
     */
    public DataResponse submitScoreBatch(DataRequest dataRequest) {
        Integer classScheduleId = dataRequest.getInteger("classScheduleId");
        List<Map<String, Object>> scoreList = (List<Map<String, Object>>) dataRequest.getList("scores");
        
        if (classScheduleId == null) {
            return CommonMethod.getReturnMessageError("教学班级ID不能为空");
        }
        
        if (scoreList == null || scoreList.isEmpty()) {
            return CommonMethod.getReturnMessageError("成绩列表不能为空");
        }
        
        for (Map<String, Object> scoreMap : scoreList) {
            Integer sId = (Integer) scoreMap.get("studentId");
            Integer mark = (Integer) scoreMap.get("mark");
            
            if (sId != null && mark != null) {
                Optional<Score> scoreOp = Optional.ofNullable(scoreRepository.findByClassSchedule_ClassScheduleIdAndStudentPersonId(
                        classScheduleId, sId));
                if (scoreOp.isPresent()) {
                    Score score = scoreOp.get();
                    score.setMark(mark);
                    scoreRepository.save(score);
                }
            }
        }
        
        // 计算排名
        updateRankings(classScheduleId);
        
        return CommonMethod.getReturnMessageOK("成绩批量提交成功");
    }
    
    /**
     * 更新教学班级中的学生排名
     */
    private void updateRankings(Integer classScheduleId) {
        List<Score> scores = scoreRepository.findByClassSchedule_ClassScheduleId(classScheduleId);
        
        // 过滤出有成绩的学生记录并按成绩降序排序
        scores.stream()
            .filter(s -> s.getMark() != null)
            .sorted(Comparator.comparing(Score::getMark, Comparator.reverseOrder()))
            .forEach(score -> {
                // 计算当前分数的排名
                int ranking = 1;
                for (Score s : scores) {
                    if (s.getMark() != null && s.getMark() > score.getMark()) {
                        ranking++;
                    }
                }
                score.setRanking(ranking);
                scoreRepository.save(score);
            });
    }
}
