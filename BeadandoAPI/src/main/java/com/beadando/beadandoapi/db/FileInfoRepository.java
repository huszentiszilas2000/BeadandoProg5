package com.beadando.beadandoapi.db;

import com.beadando.beadandoapi.dto.FileInfoDTO;
import com.beadando.beadandoapi.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    FileInfo findFileInfoByUseridAndFilename(String userid, String filename);

    @Query("SELECT fi FROM FileInfo fi where fi.userid = :userid")
    List<FileInfo> findAllByUserid(@Param("userid") String userid);

    void deleteByUseridAndFilename(String userid, String filename);

}
