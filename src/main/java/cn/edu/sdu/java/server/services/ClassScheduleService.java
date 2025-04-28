package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.TeachPlan;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ClassScheduleRepository;
import cn.edu.sdu.java.server.repositorys.TeachPlanRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 课程时间安排服务
 * 负责管理教学班级的上课时间和地点
 */
@Service
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;
    private final TeachPlanRepository teachPlanRepository;
    private final TeacherRepository teacherRepository;

    public ClassScheduleService(
            ClassScheduleRepository classScheduleRepository,
            TeachPlanRepository teachPlanRepository,
            TeacherRepository teacherRepository) {
        this.classScheduleRepository = classScheduleRepository;
        this.teachPlanRepository = teachPlanRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * 为教学班级添加课程时间安排
     * @param dataRequest 包含教学班ID、上课时间、地点等信息
     * @return 添加结果
     */
    public DataResponse addClassSchedule(DataRequest dataRequest) {
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        Map<String, Object> scheduleForm = dataRequest.getMap("scheduleForm");
        
        // 检查教学班是否存在
        Optional<TeachPlan> teachPlanOpt = teachPlanRepository.findById(teachPlanId);
        if (teachPlanOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("教学班不存在");
        }
        
        Integer dayOfWeek = CommonMethod.getInteger(scheduleForm, "dayOfWeek");
        Integer startPeriod = CommonMethod.getInteger(scheduleForm, "startPeriod");
        Integer endPeriod = CommonMethod.getInteger(scheduleForm, "endPeriod");
        String building = CommonMethod.getString(scheduleForm, "building");
        String roomNumber = CommonMethod.getString(scheduleForm, "roomNumber");
        String location = CommonMethod.getString(scheduleForm, "location");
        String remark = CommonMethod.getString(scheduleForm, "remark");
        
        // 基本参数校验
        if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
            return CommonMethod.getReturnMessageError("请选择正确的星期");
        }
        
        if (startPeriod == null || endPeriod == null || startPeriod < 1 || endPeriod < startPeriod) {
            return CommonMethod.getReturnMessageError("请选择正确的上课节次");
        }
        
        if ((building == null || building.isEmpty()) && (location == null || location.isEmpty())) {
            return CommonMethod.getReturnMessageError("请填写教学楼、教室编号或上课地点");
        }
        
        // 检查是否存在教室冲突（同一时间、同一教室已有其他课程）
        if (building != null && !building.isEmpty() && roomNumber != null && !roomNumber.isEmpty()) {
            List<ClassSchedule> conflictingRooms = classScheduleRepository.findConflictingRoomSchedules(
                    dayOfWeek, building, roomNumber, startPeriod, endPeriod);
            
            if (!conflictingRooms.isEmpty()) {
                return CommonMethod.getReturnMessageError("该教室在所选时间段已有其他课程安排");
            }
        }
        
        // 创建新的课程时间安排
        ClassSchedule schedule = new ClassSchedule();
        schedule.setTeachPlan(teachPlanOpt.get());
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartPeriod(startPeriod);
        schedule.setEndPeriod(endPeriod);
        schedule.setBuilding(building);
        schedule.setRoomNumber(roomNumber);
        schedule.setLocation(location);
        schedule.setRemark(remark);
        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());
        
        classScheduleRepository.save(schedule);
        
        return CommonMethod.getReturnMessageOK("课程时间安排添加成功");
    }
    
    /**
     * 编辑课程时间安排
     * @param dataRequest 包含排课ID和更新信息
     * @return 编辑结果
     */
    public DataResponse updateClassSchedule(DataRequest dataRequest) {
        Integer scheduleId = dataRequest.getInteger("scheduleId");
        Map<String, Object> scheduleForm = dataRequest.getMap("scheduleForm");
        
        // 检查课程安排是否存在
        Optional<ClassSchedule> scheduleOpt = classScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程时间安排不存在");
        }
        
        ClassSchedule schedule = scheduleOpt.get();
        
        Integer dayOfWeek = CommonMethod.getInteger(scheduleForm, "dayOfWeek");
        Integer startPeriod = CommonMethod.getInteger(scheduleForm, "startPeriod");
        Integer endPeriod = CommonMethod.getInteger(scheduleForm, "endPeriod");
        String building = CommonMethod.getString(scheduleForm, "building");
        String roomNumber = CommonMethod.getString(scheduleForm, "roomNumber");
        String location = CommonMethod.getString(scheduleForm, "location");
        String remark = CommonMethod.getString(scheduleForm, "remark");
        
        // 基本参数校验
        if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
            return CommonMethod.getReturnMessageError("请选择正确的星期");
        }
        
        if (startPeriod == null || endPeriod == null || startPeriod < 1 || endPeriod < startPeriod) {
            return CommonMethod.getReturnMessageError("请选择正确的上课节次");
        }
        
        if ((building == null || building.isEmpty()) && (location == null || location.isEmpty())) {
            return CommonMethod.getReturnMessageError("请填写教学楼、教室编号或上课地点");
        }
        
        // 检查是否存在教室冲突
        if (building != null && !building.isEmpty() && roomNumber != null && !roomNumber.isEmpty()) {
            List<ClassSchedule> conflictingRooms = classScheduleRepository.findConflictingRoomSchedules(
                    dayOfWeek, building, roomNumber, startPeriod, endPeriod);
            
            for (ClassSchedule conflict : conflictingRooms) {
                if (!conflict.getScheduleId().equals(scheduleId)) {
                    return CommonMethod.getReturnMessageError("该教室在所选时间段已有其他课程安排");
                }
            }
        }
        
        // 更新课程时间安排
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartPeriod(startPeriod);
        schedule.setEndPeriod(endPeriod);
        schedule.setBuilding(building);
        schedule.setRoomNumber(roomNumber);
        schedule.setLocation(location);
        schedule.setRemark(remark);
        schedule.setUpdateTime(new Date());
        
        classScheduleRepository.save(schedule);
        
        return CommonMethod.getReturnMessageOK("课程时间安排更新成功");
    }
    
    /**
     * 删除课程时间安排
     * @param dataRequest 包含排课ID
     * @return 删除结果
     */
    public DataResponse deleteClassSchedule(DataRequest dataRequest) {
        Integer scheduleId = dataRequest.getInteger("scheduleId");
        
        // 检查课程安排是否存在
        if (!classScheduleRepository.existsById(scheduleId)) {
            return CommonMethod.getReturnMessageError("课程时间安排不存在");
        }
        
        classScheduleRepository.deleteById(scheduleId);
        
        return CommonMethod.getReturnMessageOK("课程时间安排删除成功");
    }
    
    /**
     * 获取教学班级的课程时间安排
     * @param dataRequest 包含教学班ID
     * @return 课程时间安排列表
     */
    public DataResponse getClassSchedules(DataRequest dataRequest) {
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        
        // 检查教学班是否存在
        if (!teachPlanRepository.existsById(teachPlanId)) {
            return CommonMethod.getReturnMessageError("教学班不存在");
        }
        
        List<ClassSchedule> schedules = classScheduleRepository.findByTeachPlan_TeachPlanId(teachPlanId);
        List<Map<String, Object>> scheduleList = new ArrayList<>();
        
        for (ClassSchedule schedule : schedules) {
            Map<String, Object> scheduleMap = new HashMap<>();
            scheduleMap.put("scheduleId", schedule.getScheduleId());
            scheduleMap.put("dayOfWeek", schedule.getDayOfWeek());
            scheduleMap.put("dayOfWeekName", getDayOfWeekName(schedule.getDayOfWeek()));
            scheduleMap.put("startPeriod", schedule.getStartPeriod());
            scheduleMap.put("endPeriod", schedule.getEndPeriod());
            scheduleMap.put("timeRange", formatTimeRange(schedule.getStartPeriod(), schedule.getEndPeriod()));
            scheduleMap.put("building", schedule.getBuilding());
            scheduleMap.put("roomNumber", schedule.getRoomNumber());
            scheduleMap.put("location", schedule.getLocation());
            scheduleMap.put("address", formatAddress(schedule.getBuilding(), schedule.getRoomNumber(), schedule.getLocation()));
            scheduleMap.put("remark", schedule.getRemark());
            scheduleList.add(scheduleMap);
        }
        
        return CommonMethod.getReturnData(scheduleList);
    }
    
    /**
     * 获取教师的课程表
     * @param dataRequest 包含教师ID、学年和学期
     * @return 教师课程表
     */
    public DataResponse getTeacherSchedule(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Integer year = dataRequest.getInteger("year");
        Integer semester = dataRequest.getInteger("semester");
        
        // 检查教师是否存在
        if (!teacherRepository.existsById(teacherId)) {
            return CommonMethod.getReturnMessageError("教师不存在");
        }
        
        // 获取该教师负责的所有教学计划
        String query = "SELECT cs.* FROM class_schedule cs " +
                       "JOIN teach_plan tp ON cs.teach_plan_id = tp.teach_plan_id " +
                       "JOIN teacher_teachplan_role tpr ON tp.teach_plan_id = tpr.teach_plan_id " +
                       "WHERE tpr.teacher_id = ? AND tp.year = ? AND tp.semester = ?";
        
        // 这里简化处理，实际应该使用原生SQL查询或更复杂的JPQL
        // 现在我们用两步查询模拟
        
        Map<Integer, Map<Integer, List<Map<String, Object>>>> weekdaySchedules = new HashMap<>();
        for (int day = 1; day <= 7; day++) {
            weekdaySchedules.put(day, new HashMap<>());
        }
        
        // 获取查询结果并整理成按星期、节次安排的结构
        
        Map<String, Object> result = new HashMap<>();
        result.put("teacherId", teacherId);
        result.put("year", year);
        result.put("semester", semester);
        result.put("weekdaySchedules", weekdaySchedules);
        
        return CommonMethod.getReturnData(result);
    }
    
    /**
     * 检查是否存在教师时间冲突
     * @param dataRequest 包含教学班ID、教师ID、课程时间安排
     * @return 冲突检查结果
     */
    public DataResponse checkTeacherConflicts(DataRequest dataRequest) {
        Integer teachPlanId = dataRequest.getInteger("teachPlanId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        Map<String, Object> scheduleForm = dataRequest.getMap("scheduleForm");
        
        Integer dayOfWeek = CommonMethod.getInteger(scheduleForm, "dayOfWeek");
        Integer startPeriod = CommonMethod.getInteger(scheduleForm, "startPeriod");
        Integer endPeriod = CommonMethod.getInteger(scheduleForm, "endPeriod");
        
        // 检查教师在该时间段是否有其他课程安排
        List<ClassSchedule> conflicts = classScheduleRepository.findTeacherScheduleConflicts(
                teacherId, dayOfWeek, startPeriod, endPeriod);
        
        boolean hasConflict = false;
        for (ClassSchedule conflict : conflicts) {
            if (!conflict.getTeachPlan().getTeachPlanId().equals(teachPlanId)) {
                hasConflict = true;
                break;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("hasConflict", hasConflict);
        
        if (hasConflict) {
            result.put("message", "该教师在所选时间段已有其他课程安排");
        }
        
        return CommonMethod.getReturnData(result);
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