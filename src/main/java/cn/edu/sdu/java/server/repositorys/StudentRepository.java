package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
/*
 * Student 数据操作接口，主要实现Person数据的查询操作
 * Integer getMaxId()  Student 表中的最大的student_id;    JPQL 注解
 * Optional<Student> findByPersonPersonId(Integer personId); 根据关联的Person的personId查询获得Option<Student>对象 命名规范
 * Optional<Student> findByPersonNum(String num); 根据关联的Person的num查询获得Option<Student>对象  命名规范
 * List<Student> findByPersonName(String name); 根据关联的Person的name查询获得List<Student>对象集合  可能存在相同姓名的多个学生 命名规范
 * List<Student> findStudentListByNumName(String numName); 根据输入的参数 如果参数为空，查询所有的学生，输入参数不为空，查询学号或姓名包含输入参数串的所有学生集合
 */

public interface StudentRepository extends JpaRepository<Student,Integer> {
    Optional<Student> findByPersonPersonId(Integer personId);
    Optional<Student> findByPersonNum(String num);
    List<Student> findByPersonName(String name);

//    @Query(value = "from Student where peronId =?1")
//    Optional<Student> findById(String studentId);

    @Query(value = "from Student where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Student> findStudentListByNumName(String numName);
    //大写还是小写？

    @Query(value = "from Student where ?1='' or person.num like %?1% or person.name like %?1% ",
            countQuery = "SELECT count(personId) from Student where ?1='' or person.num like %?1% or person.name like %?1% ")
    Page<Student> findStudentPageByNumName(String numName,  Pageable pageable);

    Student findByPerson_PersonId(Integer myid);
}
