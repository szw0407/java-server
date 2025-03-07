package cn.edu.sdu.java.server.payload.request;


import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/*
 * DataRequest 请求参数数据类
 * Map data 保存前端请求参数的Map集合
 */
@Getter
@Setter
public class DataRequest {
    private Map<String,Object> data;

    public DataRequest() {
        data = new HashMap<>();
    }

    public void add(String key, Object obj){
        data.put(key,obj);
    }
    public Object get(String key){
        return data.get(key);
    }

    public String getString(String key){
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof String)
            return (String)obj;
        return obj.toString();
    }
    public Boolean getBoolean(String key){
        return CommonMethod.getBoolean(data, key);
    }

    public List<?> getList(String key){
        return CommonMethod.getList(data, key);
    }
    public Map<String,Object> getMap(String key){
        if(data == null)
            return new HashMap<>();
        return CommonMethod.getMap(data, key);
    }

    public Integer getInteger(String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Integer)
            return (Integer)obj;
        String str = obj.toString();
        try {
            return (int)Double.parseDouble(str);
        }catch(Exception e) {
            return null;
        }
    }
    public Long getLong(String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Long)
            return (Long)obj;
        String str = obj.toString();
        try {
            return Long.parseLong(str);
        }catch(Exception e) {
            return null;
        }
    }

    public Double getDouble(String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Double)
            return (Double)obj;
        String str = obj.toString();
        try {
            return Double.parseDouble(str);
        }catch(Exception e) {
            return null;
        }
    }
    public Date getDate( String key) {
        return CommonMethod.getDate(data, key);
    }
    public Date getTime(String key) {
        return CommonMethod.getTime(data, key);
    }
    public Integer getCurrentPage(){
        Integer cPage = this.getInteger("currentPage");
        if(cPage != null && cPage >0 )
            cPage = cPage -1;
        else
            cPage = 0;
        return cPage;

    }

}
