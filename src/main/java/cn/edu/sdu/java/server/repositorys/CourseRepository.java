package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1% ")
    List<Course> findCourseListByNumName(String numName);

    Optional<Course> findByNum(String num);
    List<Course> findByName(String name);

//    @Query(value = "FROM Course c JOIN Score sc ON c.courseId = sc.course.courseId JOIN Student st ON sc.student.personId = st.personId WHERE c.courseId = ?1")
//    List<Course> findStudentByCourseId(Integer personId, Integer courseId);



}
