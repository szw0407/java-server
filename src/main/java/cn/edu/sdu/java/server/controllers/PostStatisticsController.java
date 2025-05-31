package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.PostService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/postStatistics")
public class PostStatisticsController {
    private final PostService postService;

    public PostStatisticsController(PostService postService) {
        this.postService = postService;
    }

@RequestMapping(value = "/daily", method = {RequestMethod.GET, RequestMethod.POST})
public DataResponse getDailyPostStatistics() {
    return postService.getDailyPostStatistics();
}
}