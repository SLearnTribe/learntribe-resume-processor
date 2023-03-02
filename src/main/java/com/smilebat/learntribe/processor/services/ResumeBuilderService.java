package com.smilebat.learntribe.processor.services;

import com.google.common.base.Verify;
import com.smilebat.learntribe.dataaccess.ProfileSummaryRepository;
import com.smilebat.learntribe.dataaccess.ResumeEducationExperienceRepository;
import com.smilebat.learntribe.dataaccess.ResumeRepository;
import com.smilebat.learntribe.dataaccess.ResumeSideProjectRepository;
import com.smilebat.learntribe.dataaccess.ResumeWorkExperienceRepository;
import com.smilebat.learntribe.dataaccess.UserProfileRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.ProfileSummary;
import com.smilebat.learntribe.dataaccess.jpa.entity.Resume;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeEducationExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeSideProject;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeWorkExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.dataaccess.jpa.entity.WorkExperience;
import com.smilebat.learntribe.learntribeclients.openai.OpenAiService;
import com.smilebat.learntribe.learntribevalidator.learntribeexceptions.InvalidDataException;
import com.smilebat.learntribe.processor.converters.ProfileSummaryConverter;
import com.smilebat.learntribe.processor.converters.ResumeConverter;
import com.smilebat.learntribe.processor.services.experiences.ExperienceService;
import com.smilebat.learntribe.resume.ResumeBuilderRequest;
import com.smilebat.learntribe.resume.response.ResumeBuilderResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Resume Builder Service to hold the business logic.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Service
@RequiredArgsConstructor
public class ResumeBuilderService {

  private static final int MAX_RESUMES = 3;

  private final OpenAiService openAiService;

  private final ProfileSummaryRepository summaryRepository;

  private final UserProfileRepository profileRepository;

  private final ProfileSummaryConverter converter;

  private final ResumeConverter resumeConverter;

  private final ExperienceService experienceService;
  private final ResumeRepository repository;

  private final ResumeSideProjectRepository sideProjectRepository;
  private final ResumeWorkExperienceRepository workExperienceRepository;
  private final ResumeEducationExperienceRepository educationExperienceRepository;

  /**
   * Deletes a resume.
   *
   * @param resumeId the Resume id to be deleted.
   */
  @Transactional
  public void deleteResume(Long resumeId) {
    Verify.verifyNotNull(resumeId, "Resume id cannot be null");
    final Optional<Resume> byResumeId = repository.findById(resumeId);

    if (!byResumeId.isPresent()) {
      throw new IllegalArgumentException("Resume Cannot be found");
    }
    Resume resume = byResumeId.get();
    Set<ResumeEducationExperience> educationExperiences = resume.getEducationExperiences();
    Set<ResumeWorkExperience> workExperiences = resume.getWorkExperiences();
    Set<ResumeSideProject> sideProjects = resume.getSideProjects();
    final List<Long> edExpIds =
        educationExperiences
            .stream()
            .map(ResumeEducationExperience::getId)
            .collect(Collectors.toList());
    final List<Long> workExpIds =
        workExperiences.stream().map(ResumeWorkExperience::getId).collect(Collectors.toList());
    final List<Long> sideProjectIds =
        sideProjects.stream().map(ResumeSideProject::getId).collect(Collectors.toList());
    educationExperienceRepository.deleteAllById(edExpIds);
    workExperienceRepository.deleteAllById(workExpIds);
    sideProjectRepository.deleteAllById(sideProjectIds);
    repository.delete(byResumeId.get());
  }

  /**
   * Updates the resume.
   *
   * @param request the {@link ResumeBuilderRequest}
   */
  @Transactional
  public void updateResume(ResumeBuilderRequest request) {
    Verify.verifyNotNull(request, "Resume request cannot be null");
    final String keyCloakId = request.getKeyCloakId();
    Verify.verifyNotNull(keyCloakId, "IAM keycloak id cannot be null");
    final Long resumeId = request.getId();
    final Optional<Resume> byResumeId = repository.findById(resumeId);

    if (byResumeId.isPresent()) {
      Resume resume = byResumeId.get();
      resumeConverter.updateEntity(request, resume);
      experienceService.saveAllExperiences(request, resume);
      repository.save(resume);
    }
  }

  /**
   * Creates/saves a new resume for the user.
   *
   * @param request the {@link ResumeBuilderRequest}.
   */
  @Transactional
  public void createResume(ResumeBuilderRequest request) {
    Verify.verifyNotNull(request, "Resume request cannot be null");
    final String keyCloakId = request.getKeyCloakId();
    Verify.verifyNotNull(keyCloakId, "IAM keycloak id cannot be null");
    List<Resume> resumes = repository.findByKeyCloakId(keyCloakId);
    if (resumes.size() >= MAX_RESUMES) {
      throw new InvalidDataException("Unable to create resume : Maximum limit reached");
    }
    Optional<Resume> existingResume = Optional.empty();
    if (request.getId() != null && request.getId() > 0L) {
      existingResume = repository.findById(request.getId());
    }
    Resume resume = existingResume.isPresent() ? existingResume.get() : new Resume();
    resumeConverter.updateEntity(request, resume);
    experienceService.saveAllExperiences(request, resume);
    repository.save(resume);
  }

  /**
   * Fetches all saved user resumes.
   *
   * @param keyCloakId the IAM id.
   * @return the list of {@link ResumeBuilderResponse}.
   */
  @Transactional
  public List<ResumeBuilderResponse> fetchUserResumes(String keyCloakId) {
    Verify.verifyNotNull(keyCloakId, "IAM id cannot be null");
    List<Resume> resumes = repository.findByKeyCloakId(keyCloakId);
    return resumeConverter.toResponse(resumes);
  }

  /**
   * Retrieves Suggested Work Experiences for User.
   *
   * @param keyCloakId the IAM id.
   * @param pageNum the page number.
   * @return the Collection of Summaries.
   */
  @Transactional
  public Collection<String> getWorkExpSummaries(String keyCloakId, int pageNum) {
    Verify.verifyNotNull(keyCloakId, "IAM User id cannot be null");
    UserProfile profile = profileRepository.findByKeyCloakId(keyCloakId);
    if (profile == null) {
      return Collections.emptyList();
    }
    Set<String> roles =
        profile
            .getWorkExperiences()
            .stream()
            .map(WorkExperience::getDesignation)
            .collect(Collectors.toSet());

    int totalRoles = roles.size();
    int requiredSummaryPerRole = 25 / totalRoles;
    List<ProfileSummary> summaries = Collections.emptyList();
    for (String role : roles) {
      if (summaries.size() >= 25) {
        break;
      }
      Pageable pageable = PageRequest.of(pageNum, requiredSummaryPerRole);
      List<ProfileSummary> roleSummaries = summaryRepository.findByRole(role, pageable);
      summaries.addAll(roleSummaries);
    }
    return converter.toResponse(summaries);
  }
}
