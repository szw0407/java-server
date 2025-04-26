package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.SocialPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SocialPracticeRepository extends JpaRepository<SocialPractice, Integer> {
    @Query (value = "from SocialPractice where student.personId=?1")
    Optional<SocialPractice> findByStudentId(Integer StudentId);

    @Query(value = "from SocialPractice where id=?1")
    Optional<SocialPractice> findById(Integer id);

    @Query(value = "from SocialPractice where ?1='' or practiceLocation like %?1% or practiceOrganization like %?1% or practiceDescription like %?1%")
    List<SocialPractice> findSocialPracticeByNumName(String numName);

}
