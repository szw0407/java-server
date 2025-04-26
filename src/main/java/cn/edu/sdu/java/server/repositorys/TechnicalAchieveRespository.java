package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.TechnicalAchieve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TechnicalAchieveRespository extends JpaRepository<TechnicalAchieve, Integer>{
    @Query (value = "from TechnicalAchieve where student.personId=?1")
    Optional<TechnicalAchieve> findByStudentId(Integer StudentId);
    @Query(value = "from TechnicalAchieve where id=?1")
    Optional<TechnicalAchieve> findById(Integer id);

    @Query(value = "from TechnicalAchieve where ?1='' or description LIKE %?1% or subject LIKE %?1% OR achievement LIKE %?1%")
    List<TechnicalAchieve> findTechnicalAchieveByNumName(String numName);
}
