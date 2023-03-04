package com.smilebat.learntribe.processor.controllers;

import com.smilebat.learntribe.processor.services.ResumeBuilderService;
import com.smilebat.learntribe.resume.ResumeBuilderRequest;
import com.smilebat.learntribe.resume.response.ResumeBuilderResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resume Builder Controller.
 *
 * <p>Copyright &copy; 2022 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/resume")
// @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResumeBuilderController {

  private final ResumeBuilderService service;

  /**
   * Evaluates resume summaries.
   *
   * @param keyCloakId the IAM id.
   * @param pageNo the page number.
   * @param pageSize the page size.
   * @return the {@link ResponseEntity}.
   */
  @GetMapping(value = "/build/about")
  @ResponseBody
  @ApiOperation(value = "Retrieves summaries based on skill and current role")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Success",
            response = String.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> evaluatePersonalSummaries(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId,
      @RequestParam(value = "page") int pageNo,
      @RequestParam(value = "limit") int pageSize) {
    // Collection<String> summaries = service.getPersonalSummaries(keyCloakId, pageNo);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * Evaluate Resume Summaries.
   *
   * @param keyCloakId the IAM user id.
   * @param pageNo the page number.
   * @param pageSize the page size.
   * @return the Collection of String.
   */
  @GetMapping(value = "/build/workexp")
  @ResponseBody
  @ApiOperation(value = "Retrieves candidates role summaries")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved work summaries",
            response = String.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> evaluateWorkExpSummaries(
      @AuthenticationPrincipal(expression = "subject") String keyCloakId,
      @RequestParam(value = "page") int pageNo,
      @RequestParam(value = "limit") int pageSize) {
    Collection<String> summaries = service.getWorkExpSummaries(keyCloakId, pageNo);
    return ResponseEntity.status(HttpStatus.OK).body(summaries);
  }

  /**
   * Submits user resume.
   *
   * @param keyCloakId the User id.
   * @param request the {@link ResumeBuilderRequest}.
   * @return Response entity.
   */
  @PostMapping
  @ResponseBody
  @ApiOperation(value = "Saves and creates a resume for a user", notes = "Saves a user resume")
  @ApiResponses(
      value = {
        @ApiResponse(code = 201, message = "Successfully Created"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> submitResumes(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId,
      @RequestBody ResumeBuilderRequest request) {
    request.setKeyCloakId(keyCloakId);
    service.createResume(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Updates user resume.
   *
   * @param keyCloakId the User id.
   * @param request the {@link ResumeBuilderRequest}.
   * @return Response entity.
   */
  @PutMapping
  @ResponseBody
  @ApiOperation(value = "Updates existing resume for a user", notes = "Updates a user resume")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> updateResume(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId,
      @RequestBody ResumeBuilderRequest request) {
    request.setKeyCloakId(keyCloakId);
    service.updateResume(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * Deletes user resume.
   *
   * @param keyCloakId the User id.
   * @param id the resume id.
   * @return Response entity.
   */
  @DeleteMapping(value = "/id/{id}")
  @ResponseBody
  @ApiOperation(value = "Deletes a resume for user", notes = "Delete resume")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully deleted"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> deleteResume(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId,
      @PathVariable(value = "id") Long id) {
    service.deleteResume(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * Fetches user resumes.
   *
   * @param keyCloakId the User id.
   * @return Response entity.
   */
  @GetMapping
  @ResponseBody
  @ApiOperation(value = "Retrieves candidates resumes", notes = "Fetches all resumes of candidate")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Successfully Retrieved",
            response = ResumeBuilderResponse.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = SCConstants.BAD_REQUEST),
        @ApiResponse(code = 401, message = SCConstants.UN_AUTHORIZED),
        @ApiResponse(code = 403, message = SCConstants.FORBIDDEN),
        @ApiResponse(code = 404, message = SCConstants.URL_NOT_FOUND),
        @ApiResponse(code = 422, message = SCConstants.INVALID_DATA),
      })
  public ResponseEntity<?> fetchResumes(
      @AuthenticationPrincipal(expression = SCConstants.SUBJECT) String keyCloakId) {
    final List<ResumeBuilderResponse> resumeBuilderResponses = service.fetchUserResumes(keyCloakId);
    return ResponseEntity.status(HttpStatus.OK).body(resumeBuilderResponses);
  }
}
