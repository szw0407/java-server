package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Comment;
import cn.edu.sdu.java.server.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostOrderByCreateTimeAsc(Post post);
}
