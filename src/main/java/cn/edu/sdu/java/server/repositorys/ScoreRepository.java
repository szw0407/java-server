package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
 * Score 数据操作接口，主要实现Score数据的查询操作
 * List<Score> findByStudentPersonId(Integer personId);  根据关联的Student的student_id查询获得List<Score>对象集合,  命名规范
 */

@Repository
public interface ScoreRepository extends JpaRepository<Score,Integer> {
    List<Score> findByStudentPersonId(Integer personId);

    // find by student and course_id via link CourseSchedule
    List<Score> findByClassSchedule_Course_CourseId(Integer courseId);

    List<Score> findByClassSchedule_ClassScheduleId(Integer classScheduleId);

}
