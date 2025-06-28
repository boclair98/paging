package com.example.Task.Controller;

import com.example.Task.Entity.Data;
import com.example.Task.Repository.DataRespository;
import com.example.Task.Service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/logs")
public class DataController {

    private final DataService dataService;
    private final DataRespository dataRespository;
    @Autowired
    public DataController(DataService dataService, DataRespository dataRespository) {
        this.dataService = dataService;
        this.dataRespository = dataRespository;
    }

    //저장 로직, 한번에 저장 /api/logs/import
    @GetMapping("/import")
    @ResponseBody
    public ResponseEntity<String> importLogs() {
        try {
            dataService.importLogs("src/main/resources/logs_20000.json");
            return ResponseEntity.ok("저장 완료");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("저장 실패: " + e.getMessage());
        }
    }


    //기본 전체 조회
    //라디오 버튼을 이용해 userId,Level,TimeStamp 별 정렬
    @GetMapping("/view")
    public String view(@RequestParam(name = "sortBy", required = false) String sortBy,
                       @RequestParam(name = "searchTerm", required = false) String searchTerm,
                       @RequestParam(name = "highlightLevel", required = false) String highlightLevel,  // 강조할 레벨
                       @RequestParam(name = "highlightCount", defaultValue = "3") int highlightCount, // 연속 횟수 기본 3
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Data> dataPage;

        if (searchTerm != null && !searchTerm.isEmpty()) {
            dataPage = dataService.findData(searchTerm, page, size);
        } else if ("userId".equals(sortBy)) {
            dataPage = dataRespository.findAllOrderByUserId(pageable);
        } else if ("level".equals(sortBy)) {
            dataPage = dataRespository.findAllOrderByLevel(pageable);
        } else if ("timestamp".equals(sortBy)) {
            dataPage = dataRespository.findAllOrderByTimestamp(pageable);
        } else {
            dataPage = dataRespository.findAll(pageable);
        }

        List<Data> logs = dataPage.getContent();

        // 연속 N회 이상 특정 level 등장 체크용 Set
        Set<Long> highlightIds = new HashSet<>();
        if (highlightLevel != null && !highlightLevel.isEmpty()) {
            highlightIds = findHighlightIds(logs, highlightLevel, highlightCount);
        }

        model.addAttribute("logs", logs);
        model.addAttribute("highlightIds", highlightIds);  // 강조할 로그 ID들
        model.addAttribute("currentPage", dataPage.getNumber());
        model.addAttribute("totalPages", dataPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("pageSize", size);
        model.addAttribute("highlightLevel", highlightLevel);
        model.addAttribute("highlightCount", highlightCount);

        return "index";
    }

    // 연속 N회 이상 등장하는 로그 ID 찾는 메서드
    private Set<Long> findHighlightIds(List<Data> logs, String targetLevel, int N) {
        Set<Long> highlightIds = new HashSet<>();
        int count = 0;

        for (int i = 0; i < logs.size(); i++) {
            Data log = logs.get(i);
            if (targetLevel.equalsIgnoreCase(log.getLevel())) {
                count++;
                if (count >= N) {
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
