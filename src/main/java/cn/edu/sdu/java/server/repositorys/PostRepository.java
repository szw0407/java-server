package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 根据用户的 personId 查询帖子列表，按创建时间降序排列
    List<Post> findByUserPersonIdOrderByCreateTimeDesc(Integer personId);

    // 根据标题模糊查询帖子列表
    @Query("from Post where ?1='' or title like %?1%")
    List<Post> findPostListByTitle(String title);

    // 分页查询帖子，按创建时间降序排列
    @Query(value = "from Post where ?1='' or title like %?1%",
            countQuery = "SELECT count(postId) from Post where ?1='' or title like %?1%")
    Page<Post> findPostPageByTitle(String title, Pageable pageable);

    // 根据帖子ID查询帖子
    Optional<Post> findByPostId(Integer postId);

    // 文件路径: src/main/java/cn/edu/sdu/java/server/repositorys/PostRepository.java
    @Query("from Post where isTop = true order by createTime desc")
    List<Post> findTopPosts();
}