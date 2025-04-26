package cn.edu.sdu.java.server.services;


import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;

import cn.edu.sdu.java.server.repositorys.AcademicCompetitionRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcademicCompetitionService {
    private final StudentRepository studentRepository;
    private final AcademicCompetitionRepository academicCompetitionRepository;
    @Autowired //实现信息注入
    public AcademicCompetitionService( StudentRepository studentRepository, AcademicCompetitionRepository academicCompetitionRepository) {

        this.studentRepository = studentRepository;
        this.academicCompetitionRepository = academicCompetitionRepository;
    }

    public Map<String, Object> getMapFromAcademicCompetition(AcademicCompetition ac) {
        //单个对象的键值对映射
        Map<String, Object> m = new HashMap<>();
        Student s;
        if(ac==null) //输入的参数比赛类为空，返回空map
            return m;
        m.put("id", ac.getId());
        s = ac.getStudent();
        if(s==null)
            return m;//比赛类中的学生为空，返回空map
        m.put("studentId",s.getPersonId());//获得学生id
        m.put("time", ac.getTime());
        m.put("subject", ac.getSubject());
        m.put("achievement", ac.getAchievement());
        return m;
    }
    //模糊匹配
    public List<Map<String, Object>> getAcademicCompetitionMapList(String subjectAchievement){
        //查询操作，一般输入相关的字
        List<Map<String ,Object>> dataList = new ArrayList<>();
        List<AcademicCompetition> sList = academicCompetitionRepository.findAcademicByNumName(subjectAchievement);
        //通过id从数据库中找到比赛的元组
        if(sList==null || sList.isEmpty())
            return dataList;
        for(AcademicCompetition ac:sList){
            dataList.add(getMapFromAcademicCompetition(ac));
        }
        return dataList;

    }
    //模糊匹配
    public DataResponse getAcademicCompetitionList(DataRequest dataRequest) {
        String subjectAchievement = dataRequest.getString("numName");//这里如果用numName是什么意思?与之相关的查询字段
        //这里的key，是一个实际上不存在的键，会在前端进行初始化
        //此处的key在前端进行初始化查询，这里是模糊匹配
        List<Map<String, Object>> dataList = getAcademicCompetitionMapList(subjectAchievement);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse academicCompetitionDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id != null && id > 0) {//Mysql默认id从1开始
            academicCompetitionRepository.deleteById(id); // 使用 JPA 内置方法
            return CommonMethod.getReturnMessageOK();
        }
        return CommonMethod.getReturnMessageError("无效的竞赛记录ID");
    }

    public DataResponse getAcademicCompetitionInfo(DataRequest dataRequest){
        Integer id = dataRequest.getInteger("id");//这里的Id又是啥？主键的值，通过竞赛主键id获取
        AcademicCompetition ac = null;
        Optional<AcademicCompetition> op;
        if(id!=null && id>0){
            op = academicCompetitionRepository.findById(id);//精确查找
            if(op.isPresent()){
                ac = op.get();
                return CommonMethod.getReturnData(getMapFromAcademicCompetition(ac));
            }else{
                return CommonMethod.getReturnMessageError("竞赛记录不存在");
            }
        }else{
            return CommonMethod.getReturnMessageError("无效的竞赛记录ID");
        }

    }

    public DataResponse academicCompetitionEditSave(DataRequest dataRequest) {

        Map<String, Object> form = dataRequest.getMap("form");
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form, "studentId");

        // 检查学生是否存在
        Optional<Student> studentOpt = studentRepository.findByPersonPersonId(studentId);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在");
        }
        Student student = studentOpt.get();
        AcademicCompetition ac = null;

        if (id == null) {
            Optional<AcademicCompetition> op = academicCompetitionRepository.findById(id);
            if (!op.isPresent()) {
                ac = new AcademicCompetition();
                ac.setStudent(student);
            }
        }else{
            Optional<AcademicCompetition> op = academicCompetitionRepository.findById(id);
            if (!op.isPresent()) {
                return CommonMethod.getReturnMessageError("该社会实践记录不存在");
            }
            ac = op.get();

        }
        // 设置或更新竞赛信息
        ac.setTime(CommonMethod.getString(form, "time"));
        ac.setSubject(CommonMethod.getString(form, "subject"));
        ac.setAchievement(CommonMethod.getString(form, "achievement"));

        // 保存记录
        academicCompetitionRepository.save(ac);
        return CommonMethod.getReturnData(ac.getId(), "保存成功");

    }



}



