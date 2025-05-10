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
import org.springframework.stereotype.Service;

@Service
class GradingService {

}
