package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialPracticeService {
    private static final Logger log = LoggerFactory.getLogger(SocialPracticeService.class);

    private final SocialPracticeRepository socialPracticeRepository;
    private final PersonRepository personRepository;
    private final SystemService systemService;

    // 将社会实践对象转为Map
    public Map<String, Object> getMapFromPractice(SocialPractice sp) {
        Map<String, Object> m = new HashMap<>();
        if (sp == null) return m;

        m.put("id", sp.getId());
        m.put("personId", sp.getPerson().getPersonId());
        m.put("practiceTime", sp.getPracticeTime());
        m.put("location", sp.getPracticeLocation());
        m.put("organization", sp.getPracticeOrganization());
        m.put("description", sp.getPracticeDescription());
        m.put("durationDays", sp.getDurationDays());
        return m;
    }

    // 获取社会实践列表
    public DataResponse getPracticeList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        List<SocialPractice> spList = socialPracticeRepository.findByPerson_PersonId(personId);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (SocialPractice sp : spList) {
            dataList.add(getMapFromPractice(sp));
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 保存或更新社会实践记录
    public DataResponse practiceEditSave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer personId = CommonMethod.getInteger(form, "personId");
        String num = CommonMethod.getString(form, "num");

        SocialPractice sp = null;
        boolean isNew = false;

        if (personId != null) {
            List<SocialPractice> op = socialPracticeRepository.findByPerson_PersonId(personId);
            if (op.size() > 0) {
                sp = op.get(0);
            }
        }else{
            sp = new SocialPractice();
        }




        sp.setPracticeTime(CommonMethod.getLocalDate(form, "practiceTime"));
        sp.setPracticeLocation(CommonMethod.getString(form, "location"));
        sp.setPracticeOrganization(CommonMethod.getString(form, "organization"));
        sp.setPracticeDescription(CommonMethod.getString(form, "description"));
        sp.setDurationDays(CommonMethod.getInteger(form, "durationDays"));

        socialPracticeRepository.save(sp);
        systemService.modifyLog(sp, isNew);
        return CommonMethod.getReturnData(sp.getId());
    }
    
    public DataResponse practiceDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        socialPracticeRepository.deleteById(id);
        return CommonMethod.getReturnMessageOK();
    }


    // 分页查询
    public DataResponse getPracticePage(DataRequest dataRequest) {
        Integer page = dataRequest.getInteger("page");
        Integer size = dataRequest.getInteger("size");
        Pageable pageable = PageRequest.of(page, size);

        Page<SocialPractice> pagedData = socialPracticeRepository.findAll(pageable);
        List<Map<String, Object>> dataList = new ArrayList<>();

        pagedData.getContent().forEach(sp ->
                dataList.add(getMapFromPractice(sp))
        );

        Map<String, Object> response = new HashMap<>();
        response.put("dataList", dataList);
        response.put("total", pagedData.getTotalElements());
        return CommonMethod.getReturnData(response);
    }

}