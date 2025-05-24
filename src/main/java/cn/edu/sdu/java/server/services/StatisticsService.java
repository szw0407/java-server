package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.StatisticsDay;
import cn.edu.sdu.java.server.repositorys.StatisticsDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final JdbcTemplate jdbcTemplate;
    private final StatisticsDayRepository statisticsDayRepository;

    @Autowired
    public StatisticsService(JdbcTemplate jdbcTemplate, StatisticsDayRepository statisticsDayRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.statisticsDayRepository = statisticsDayRepository;
    }

    /** 1. 日统计 */
    public List<cn.edu.sdu.java.server.payload.response.StatisticsDay> getDailyStatistics() {
        String sql = """
            SELECT day,
                   request_count,
                   create_count,
                   login_count
              FROM statistics_day
             ORDER BY day
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            cn.edu.sdu.java.server.payload.response.StatisticsDay sd = new cn.edu.sdu.java.server.payload.response.StatisticsDay();
            sd.setDay(rs.getString("day"));
            sd.setRequestCount(rs.getLong("request_count"));
            sd.setCreateCount(rs.getLong("create_count"));
            sd.setLoginCount(rs.getLong("login_count"));
            return sd;
        });
    }

    /** 2. 每生每学期学分明细 */
    public List<Map<String, Object>> getCreditsPerSemester() {
        String sql = """
            SELECT sc.person_id                                AS studentId,
                   p.name                                      AS studentName,
                   CONCAT(cs.year,' ',cs.semester)             AS semester,
                   SUM(co.credit)                              AS totalCredits
              FROM score sc
              JOIN class_schedule cs ON sc.class_schedule_id = cs.class_schedule_id
              JOIN course co         ON cs.course_id         = co.course_id
              JOIN student st        ON sc.person_id         = st.person_id
              JOIN person p          ON st.person_id         = p.person_id
             GROUP BY sc.person_id, p.name, cs.year, cs.semester
            """;
        return jdbcTemplate.queryForList(sql);
    }

    /** 3. 各学期平均学分 */
    public List<Map<String, Object>> getAverageCreditsPerSemester() {
        String sql = """
            SELECT t.semester,
                   ROUND(AVG(t.totalCredits),2) AS averageCredits
              FROM (
                    SELECT sc.person_id,
                           CONCAT(cs.year,' ',cs.semester) AS semester,
                           SUM(co.credit)                  AS totalCredits
                      FROM score sc
                      JOIN class_schedule cs ON sc.class_schedule_id = cs.class_schedule_id
                      JOIN course co         ON cs.course_id         = co.course_id
                     GROUP BY sc.person_id, cs.year, cs.semester
                   ) t
             GROUP BY t.semester
            """;
        return jdbcTemplate.queryForList(sql);
    }

    /** 4. 学分区间分布 */
    public List<Map<String, Object>> getCreditDistribution() {
        String sql = """
            SELECT credit_range AS `range`,
                   COUNT(*)      AS `count`
              FROM (
                    SELECT CASE
                             WHEN totalCredits BETWEEN 0  AND 5  THEN '0-5学分'
                             WHEN totalCredits BETWEEN 6  AND 10 THEN '6-10学分'
                             WHEN totalCredits BETWEEN 11 AND 15 THEN '11-15学分'
                             ELSE                             '16+学分'
                           END AS credit_range
                      FROM (
                            SELECT sc.person_id,
                                   CONCAT(cs.year,' ',cs.semester) AS semester,
                                   SUM(co.credit)               AS totalCredits
                              FROM score sc
                              JOIN class_schedule cs ON sc.class_schedule_id = cs.class_schedule_id
                              JOIN course co         ON cs.course_id         = co.course_id
                             GROUP BY sc.person_id, cs.year, cs.semester
                           ) sub1
                   ) sub2
             GROUP BY credit_range
            """;
        return jdbcTemplate.queryForList(sql);
    }

    /** 5. 学生性别分布统计 */
    public List<Map<String, Object>> getGenderDistribution() {
        String sql = """
            SELECT 
                CASE 
                    WHEN p.gender = 1 THEN '男'
                    WHEN p.gender = 2 THEN '女'
                    ELSE '未知'
                END AS gender,
                COUNT(*) AS count
            FROM student s
            JOIN person p ON s.person_id = p.person_id
            GROUP BY p.gender
        """;
        return jdbcTemplate.queryForList(sql);
    }
}
