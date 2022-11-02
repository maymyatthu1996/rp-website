package com.planetway.relyingpartyapp.repository;


import org.springframework.stereotype.Repository;

import com.planetway.relyingpartyapp.model.RpFile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FileRepository {
    private final Map<String, RpFile> files = new HashMap<>();

    public void save(RpFile rpFile) {
        files.put(rpFile.getFileId(), rpFile);
    }

    public Collection<RpFile> findAll() {
        return files.values();
    }

    public RpFile findById(String fileId) {
        return files.get(fileId);
    }
}
