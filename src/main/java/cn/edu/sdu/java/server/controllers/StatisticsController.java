package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.StatisticsDay;
import cn.edu.sdu.java.server.services.StatisticsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping("/daily")
    public DataResponse dailyStats() {
        List<StatisticsDay> data = statisticsService.getDailyStatistics();
        return new DataResponse(0, data, "success");
    }

    @PostMapping("/credits-per-semester")
    public DataResponse creditsPerSemester() {
        List<Map<String, Object>> data = statisticsService.getCreditsPerSemester();
        return new DataResponse(0, data, "success");
    }

    @PostMapping("/average-credits-per-semester")
    public DataResponse averageCreditsPerSemester() {
        List<Map<String, Object>> data = statisticsService.getAverageCreditsPerSemester();
        return new DataResponse(0, data, "success");
    }

    @PostMapping("/credit-distribution")
    public DataResponse creditDistribution() {
        List<Map<String, Object>> data = statisticsService.getCreditDistribution();
        return new DataResponse(0, data, "success");
    }

    @PostMapping("/gender-distribution")
    public DataResponse genderDistribution() {
        List<Map<String, Object>> data = statisticsService.getGenderDistribution();
        return new DataResponse(0, data, "success");
    }
}
