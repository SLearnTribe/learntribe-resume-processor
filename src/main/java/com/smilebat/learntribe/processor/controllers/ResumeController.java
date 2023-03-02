package com.smilebat.learntribe.processor.controllers;

import com.smilebat.learntribe.dataaccess.jpa.entity.FileDB;
import com.smilebat.learntribe.processor.services.FileStorageService;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Resume Controller.
 *
 * <p>Copyright &copy; 2023 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/resume")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResumeController {

  @Autowired private FileStorageService storageService;

  /**
   * Downloads User Resumes.
   *
   * @param keyCloakId the IAM id.
   * @param email the email
   * @return the {@link ResponseEntity}.
   */
  @GetMapping(value = "/download")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved",
            response = String.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  @ApiOperation(value = "Downloads User Resumes", notes = "Download")
  public ResponseEntity<Resource> fetchResume(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId,
      @RequestParam(required = false, value = "email") String email) {
    FileDB file =
        StringUtils.isEmpty(email)
            ? storageService.getFile(keyCloakId)
            : storageService.getFileByEmail(email);
    ByteArrayResource resource = new ByteArrayResource(file.getData());
    return ResponseEntity.ok()
        .contentLength(file.getSize())
        .contentType(MediaType.APPLICATION_PDF)
        .body(resource);
  }

  /**
   * Uploads User Resumes.
   *
   * @param keyCloakId the IAM id.
   * @param file the file to be uploaded.
   * @param email the email.
   * @return the {@link ResponseEntity}.
   */
  @PostMapping(value = "/upload")
  @ResponseBody
  @ApiOperation(value = "Uploads resume", notes = "upload")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully uploaded",
            response = String.class,
            responseContainer = "List"),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> uploadResume(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId,
      @RequestParam(required = true, value = "email") String email,
      @RequestParam("file") MultipartFile file) {

    String message = "";
    try {
      storageService.store(keyCloakId, email, file);
      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(message);
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
    }
  }
}
