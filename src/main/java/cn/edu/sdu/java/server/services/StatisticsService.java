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
    // 加入此方法到你的 StatisticsService.java 中
    /** 5. 请假分布情况（例如：0次、1–2次、3–5次、5次以上） */
    public List<Map<String, Object>> getLeaveStatistics() {
        String sql = """
        SELECT
            st.person_id AS 学生ID,
            p.name AS 学生姓名,
            COUNT(*) AS 总请假次数,
            SUM(CASE WHEN sl.is_approved = 1 THEN 1 ELSE 0 END) AS 批准请假次数
        FROM student_leave sl
        JOIN student st ON sl.student_id = st.person_id
        JOIN person p ON st.person_id = p.person_id
        GROUP BY st.person_id, p.name
    """;
        return jdbcTemplate.queryForList(sql);
    }
    /** 6. 请假次数分布（统计不同请假次数的人数） */
    public List<Map<String, Object>> getLeaveDistribution() {
        String sql = """
        SELECT
          CASE
            WHEN total_leaves = 0 THEN '0次'
            WHEN total_leaves = 1 THEN '1次'
            WHEN total_leaves = 2 THEN '2次'
            WHEN total_leaves = 3 THEN '3次'
            WHEN total_leaves = 4 THEN '4次'
            ELSE '5次及以上'
          END AS 状态,
          COUNT(*) AS 数量
        FROM (
          SELECT st.person_id, COUNT(*) AS total_leaves
          FROM student_leave sl
          JOIN student st ON sl.student_id = st.person_id
          GROUP BY st.person_id
        ) sub
        GROUP BY 状态
        ORDER BY 状态
    """;
        return jdbcTemplate.queryForList(sql);
    }



}



