package com.smilebat.learntribe.processor.services;

import com.smilebat.learntribe.dataaccess.FileDBRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.FileDB;
import com.smilebat.learntribe.learntribevalidator.learntribeexceptions.InvalidDataException;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Storage Service to hold the business logic.
 *
 * <p>Copyright &copy; 2023 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Service
public class FileStorageService {

  @Autowired private FileDBRepository fileDBRepository;

  /**
   * Uploads a file to db
   *
   * @param keycloakId the IAM id.
   * @param file the {@link MultipartFile}.
   * @param email the user email.
   * @return the {@link FileDB}
   * @throws IOException on error.
   */
  @Transactional
  public FileDB store(String keycloakId, String email, MultipartFile file) throws IOException {
    Optional<FileDB> opFile = fileDBRepository.findByKeycloakId(keycloakId);
    FileDB fileDB = opFile.isPresent() ? opFile.get() : new FileDB();
    if (file == null) {
      throw new InvalidDataException("Unable to process file");
    }
    String originalFilename = file.getOriginalFilename();
    if (originalFilename != null) {
      String fileName = StringUtils.cleanPath(originalFilename);
      fileDB.setName(fileName);
    }
    fileDB.setType(file.getContentType());
    fileDB.setData(file.getBytes());
    fileDB.setSize(file.getSize());
    fileDB.setKeycloakId(keycloakId);
    fileDB.setEmail(email);
    return fileDBRepository.save(fileDB);
  }

  /**
   * Fetchs File entity from db
   *
   * @param keycloakId the IAM id
   * @return the {@link FileDB}
   */
  @Transactional
  public FileDB getFile(String keycloakId) {
    log.info("Fetching user resume");
    final Optional<FileDB> opFile = fileDBRepository.findByKeycloakId(keycloakId);
    if (opFile.isEmpty()) {
      throw new InvalidDataException("No resumes present for the user");
    }
    return opFile.get();
  }

  /**
   * Fetchs File entity from db based on email
   *
   * @param email the IAM email id
   * @return the {@link FileDB}
   */
  @Transactional
  public FileDB getFileByEmail(String email) {
    log.info("Fetching applicant resume");
    final Optional<FileDB> opFile = fileDBRepository.findByEmailId(email);
    if (opFile.isEmpty()) {
      throw new InvalidDataException("No resumes present for the user");
    }
    return opFile.get();
  }

  /**
   * Gets all files as streams.
   *
   * @return the {@link FileDB} as stream.
   */
  public Stream<FileDB> getAllFiles() {
    return fileDBRepository.findAll().stream();
  }
}
