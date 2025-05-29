package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Comment;
import cn.edu.sdu.java.server.models.Post;
import cn.edu.sdu.java.server.models.User;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CommentRepository;
import cn.edu.sdu.java.server.repositorys.PostRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public DataResponse createPost(DataRequest dataRequest) {
        Map<String,Object> form = dataRequest.getMap("form");
        String title = CommonMethod.getString(form, "title");
        String content = CommonMethod.getString(form, "content");
        String userIdS = CommonMethod.getString(form, "userId");
        Integer userId = null;
        if (!userIdS.isEmpty()) {
            try {
                userId = Integer.parseInt(userIdS);
            } catch (NumberFormatException e) {
                return CommonMethod.getReturnMessageError("用户ID格式错误");
            }
        }


        if (title == null || content == null || userId == null) {
            return CommonMethod.getReturnMessageError("参数缺失");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return CommonMethod.getReturnMessageError("用户不存在");
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(LocalDateTime.now());
        post.setUser(user);

        try {
            postRepository.save(post);
            return CommonMethod.getReturnMessageOK("保存成功");
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("保存失败：" + e.getMessage());
        }
    }

    public DataResponse getPostList(DataRequest dataRequest) {
        String searchText = dataRequest.getString("searchText");
        List<Post> posts = postRepository.findPostListByTitle(searchText);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("postId", post.getPostId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("createTime", post.getCreateTime());
            map.put("userName", post.getUser().getPerson().getName());
            map.put("userId", post.getUser().getPerson().getNum());
            map.put("isTop", post.getIsTop());
            dataList.add(map);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getPostPageData(DataRequest dataRequest) {
        String searchText = dataRequest.getString("searchText");
        int currentPage = dataRequest.getCurrentPage();
        int pageSize = 10;

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Post> page = postRepository.findPostPageByTitle(searchText, pageable);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Post post : page.getContent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("postId", post.getPostId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("createTime", post.getCreateTime());
            map.put("userName", post.getUser().getUserName());
            dataList.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", dataList);
        result.put("dataTotal", page.getTotalElements());
        result.put("pageSize", pageSize);

        return CommonMethod.getReturnData(result);
    }

    public DataResponse deletePost(DataRequest dataRequest) {
        Integer postId = dataRequest.getInteger("postId");
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            postRepository.delete(postOptional.get());
            return CommonMethod.getReturnMessageOK();
        } else {
            return CommonMethod.getReturnMessageError("Post not found");
        }
    }
    // 文件路径: src/main/java/cn/edu/sdu/java/server/services/PostService.java
    public DataResponse toggleTopPost(DataRequest dataRequest) {
        Integer postId = dataRequest.getInteger("postId");
        Boolean isTop = dataRequest.getBoolean("isTop");

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("帖子不存在");
        }

        Post post = postOptional.get();
        post.setIsTop(isTop);
        postRepository.save(post);

        return CommonMethod.getReturnMessageOK(isTop ? "置顶成功" : "取消置顶成功");
    }

    public DataResponse addComment(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        String content = CommonMethod.getString(form, "content");
        Integer postId = CommonMethod.getInteger(form, "postId");
        Integer userId = CommonMethod.getInteger(form, "userId");

        // 校验评论内容是否为空
        if (content == null || content.isEmpty()) {
            return CommonMethod.getReturnMessageError("评论内容不能为空");
        }

        // 校验 postId 是否为空
        if (postId == null) {
            return CommonMethod.getReturnMessageError("帖子 ID 不能为空");
        }

        // 校验 userId 是否为空
        if (userId == null) {
            return CommonMethod.getReturnMessageError("用户 ID 不能为空");
        }

        // 校验帖子是否存在
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("帖子不存在");
        }

        // 校验用户是否存在
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("用户不存在");
        }

        // 创建并保存评论
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());
        comment.setPost(postOptional.get());
        comment.setUser(userOptional.get());

        commentRepository.save(comment);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getComments(DataRequest dataRequest) {
        Integer postId = dataRequest.getInteger("postId");

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("帖子不存在");
        }

        List<Comment> comments = commentRepository.findByPostOrderByCreateTimeAsc(postOptional.get());
        List<Map<String, Object>> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("commentId", comment.getCommentId());
            map.put("content", comment.getContent());
            map.put("createTime", comment.getCreateTime());
            map.put("userName", comment.getUser().getUserName());
            System.out.println("UserName: " + comment.getUser().getUserName());
            commentList.add(map);
        }

        return CommonMethod.getReturnData(commentList);
    }

}