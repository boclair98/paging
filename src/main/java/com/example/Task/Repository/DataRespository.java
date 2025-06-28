package com.example.Task.Repository;

import com.example.Task.Entity.Data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//{
////    "timestamp": "2025-06-11T20:59:03.364633Z",
////    "level": "ERROR",
////    "service": "user-api",
////    "message": "Unhandled exception",
////    "userId": "u69574"
////  },
@Repository
public interface DataRespository extends JpaRepository<Data, Long> {

    @Override
    <S extends Data> List<S> saveAll(Iterable<S> entities);

    @Override
    List<Data> findAll();

    //이름별 조히
    @Query("SELECT d FROM Data d WHERE d.userId = :userid")
    List<Data> findByUserId(@Param("userid") String userid);

    //level 별 조회
    @Query("SELECT d FROM Data d WHERE d.level = :level")
    List<Data> findByLevel(@Param("level") String level);

    //service별 조회
    @Query("SELECT d FROM Data d WHERE d.service = :service")
    List<Data> findByService(@Param("service") String service);

    //message별 조회.
    @Query("SELECT d FROM Data d WHERE d.message = :message")
    List<Data> findByMessage(@Param("message") String message);


    // 페이징 x 사용자 id별 정렬
    @Query("SELECT d FROM Data d order by d.userId")
    List<Data> SortByUserId();
    // 페이징 x 사용자 Level별 정렬

    @Query("SELECT d FROM Data d order by d.level")
    List<Data> SortByLevel();

    // 페이징 x 사용자 TimeStamp별 정렬
    @Query("SELECT d FROM Data d order by d.timestamp")
    List<Data> SortByTimeStamp();

    // 페이징 정렬된 결과 반환 (사용자 ID 기준)
    @Query("SELECT d FROM Data d ORDER BY d.userId")
    Page<Data> findAllOrderByUserId(Pageable pageable);

    // 페이징 정렬된 결과 반환 (레벨 기준)
    @Query("SELECT d FROM Data d ORDER BY d.level")
    Page<Data> findAllOrderByLevel(Pageable pageable);

    // 페이징 정렬된 결과 반환 (타임스탬프 기준)
    @Query("SELECT d FROM Data d ORDER BY d.timestamp")
    Page<Data> findAllOrderByTimestamp(Pageable pageable);

    //들어온 값이 있는지 없는지 확인하기 위한 쿼리
    @Query("SELECT d FROM Data d WHERE " +
            "LOWER(d.userId) LIKE LOWER(CONCAT('%', :content, '%')) OR " +
            "LOWER(d.message) LIKE LOWER(CONCAT('%', :content, '%')) OR " +
            "LOWER(d.service) LIKE LOWER(CONCAT('%', :content, '%')) OR " +
            "LOWER(d.level) LIKE LOWER(CONCAT('%', :content, '%'))")
    Page<Data> findData(@Param("content") String content, Pageable pageable);


    Page<Data> findByLevel(String level, Pageable pageable);
}
