package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.SocialPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SocialPracticeRepository extends JpaRepository<SocialPractice, Integer> {
    List<SocialPractice> findByPerson_PersonId(Integer personId);

}
