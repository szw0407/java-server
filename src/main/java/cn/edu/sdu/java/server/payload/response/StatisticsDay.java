package cn.edu.sdu.java.server.payload.response;

public class StatisticsDay {
    private String day;
    private long requestCount;
    private long createCount;
    private long loginCount;

    public StatisticsDay() {}
    // getter & setter...
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public long getRequestCount() { return requestCount; }
    public void setRequestCount(long requestCount) { this.requestCount = requestCount; }
    public long getCreateCount() { return createCount; }
    public void setCreateCount(long createCount) { this.createCount = createCount; }
    public long getLoginCount() { return loginCount; }
    public void setLoginCount(long loginCount) { this.loginCount = loginCount; }
}
