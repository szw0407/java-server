package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.TeachPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachPlanRepository extends JpaRepository<TeachPlan, Integer> {

}
