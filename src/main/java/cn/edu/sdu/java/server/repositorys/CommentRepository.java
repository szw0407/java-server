// 文件路径: src/main/java/cn/edu/sdu/java/server/repositorys/CommentRepository.java
package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Comment;
import cn.edu.sdu.java.server.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostOrderByCreateTimeAsc(Post post);

    @Query("SELECT DATE(c.createTime) AS date, COUNT(c) AS count FROM Comment c GROUP BY DATE(c.createTime) ORDER BY date ASC")
    List<Object[]> countCommentsByDate();
}