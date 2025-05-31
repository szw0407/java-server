package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ClassSchedule;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    // 根据学生ID和课程ID查询成绩记录，用于检查学生是否已选某课程
    @Query("from Score where student.personId = ?1 and classSchedule.course.courseId = ?2")
    List<Score> findByStudentPersonIdAndCourseId(Integer personId, Integer courseId);
    
    // 根据学生ID和教学班级ID查询成绩记录，用于检查学生是否已选某教学班级
    Optional<Score> findByStudentPersonIdAndClassScheduleClassScheduleId(Integer personId, Integer classScheduleId);
    
    // 查询学生在特定学期的选课记录
    @Query("from Score where student.personId = ?1 and classSchedule.semester = ?2 and classSchedule.year = ?3")
    List<Score> findStudentSemesterCourses(Integer personId, String semester, String year);

    // 查询某个教学班某一个学生的成绩
    Score findByClassSchedule_ClassScheduleIdAndStudentPersonId(Integer classScheduleId, Integer personId);
    @Query("select s from Score s where s.classSchedule.semester = ?1 and s.classSchedule.year = ?2")
    List<Score> findBySemesterAndYear(String semester, String year);

    Score findByStudent_Person_NumAndClassSchedule_Course_NumAndClassSchedule_ClassNumberAndClassSchedule_yearAndClassSchedule_semester(
            String studentNum, String courseNum, Integer classNum, String year, String semester);

    List<Score> findByStudent_Person_Num(String num);

    List<Score> findByStudent_Person_NumAndClassSchedule_Course_Num(String num, String courseNum);

    List<Score> findByClassSchedule_Course_Num(String courseNum);

    Optional<Score> findByStudent_Person_NumAndClassScheduleClassScheduleId(String personNum, Integer classId);

    Collection<Object> findByStudent_Person_PersonId(Integer myid);
}
