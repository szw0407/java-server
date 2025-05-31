package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.PostService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse createPost(@Valid @RequestBody DataRequest dataRequest) {
        return postService.createPost(dataRequest);
    }

    @PostMapping("/getPostList")
    public DataResponse getPostList(@Valid @RequestBody DataRequest dataRequest) {
        return postService.getPostList(dataRequest);
    }

    @PostMapping("/getPostPageData")
    public DataResponse getPostPageData(@Valid @RequestBody DataRequest dataRequest) {
        return postService.getPostPageData(dataRequest);
    }

    @PostMapping("/deletePost")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deletePost(@Valid @RequestBody DataRequest dataRequest) {
        return postService.deletePost(dataRequest);
    }

    @PostMapping("/addComment")
    public DataResponse addComment(@Valid @RequestBody DataRequest dataRequest) {
        return postService.addComment(dataRequest);
    }

    @PostMapping("/getComments")
    public DataResponse getComments(@Valid @RequestBody DataRequest dataRequest) {
        return postService.getComments(dataRequest);
    }
    // 文件路径: src/main/java/cn/edu/sdu/java/server/controllers/PostController.java
    @PostMapping("/toggleTop")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse toggleTopPost(@Valid @RequestBody DataRequest dataRequest) {
        return postService.toggleTopPost(dataRequest);
    }
}