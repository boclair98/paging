package com.example.Task.Service;

import com.example.Task.Entity.Data;
import com.example.Task.Repository.DataRespository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DataService {

    private final DataRespository repository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataService(DataRespository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void importLogs(String filePath) throws IOException {
        File file = new File(filePath);
        List<Data> logs = objectMapper.readValue(file, new TypeReference<>() {});
        repository.saveAll(logs);
    }

    public List<Data> findAll(){
        return repository.findAll(PageRequest.of(0,10)).getContent();
    }

    public List<Data> findByUserId(String userid){
        return repository.findByUserId(userid);
    }
    public List<Data> findByLevel(String level){
        return repository.findByLevel(level);
    }
    public List<Data> findByService(String service){
        return repository.findByService(service);
    }
    public List<Data> findByMessage(String message){
        return repository.findByMessage(message);
    }

    public List<Data> sortByUserId(){
        return repository.SortByUserId();
    }
    public List<Data> sortByTimestamp(){
        return repository.SortByTimeStamp();
    }
    public List<Data> sortByLevel(){
        return repository.SortByLevel();
    }

    public Page<Data> findData(String content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findData(content, pageable);
    }

    public Set<Long> findHighlightIds(List<Data> logs, String targetLevel, int N) {
        Set<Long> highlightIds = new HashSet<>();

        int count = 0;
        for (int i = 0; i < logs.size(); i++) {
            Data log = logs.get(i);
            if (targetLevel.equalsIgnoreCase(log.getLevel())) {
                count++;
                if (count >= N) {
                    // 연속 N회 이상이니까 이 구간의 ID를 highlight에 추가
                    for (int j = i - N + 1; j <= i; j++) {
                        highlightIds.add(logs.get(j).getId());
                    }
                }
            } else {
                count = 0;
            }
        }
        return highlightIds;
    }
}
