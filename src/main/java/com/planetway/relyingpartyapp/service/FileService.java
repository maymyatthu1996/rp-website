package com.planetway.relyingpartyapp.service;

import com.planetway.relyingpartyapp.model.RpFile;
import com.planetway.relyingpartyapp.repository.FileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public RpFile storeFile(MultipartFile file) {
        byte[] fileContents;
        try {
            fileContents = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return storeFile(fileContents, file.getOriginalFilename(), file.getContentType());
    }

    public RpFile storeFile(byte[] fileContents, String originalFilename, String contentType) {
        RpFile rpFile = new RpFile();
        rpFile.setFileId(UUID.randomUUID().toString());
        rpFile.setFileName(originalFilename);
        rpFile.setContentType(contentType);
        rpFile.setData(fileContents);
        rpFile.setHash(DigestUtils.sha256Hex(fileContents));

        fileRepository.save(rpFile);

        return rpFile;
    }

    public RpFile readFile(String fileId) {
        return fileRepository.findById(fileId);
    }
}
