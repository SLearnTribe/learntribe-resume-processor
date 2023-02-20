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
import com.smilebat.learntribe.openai.OpenAiRequest;
import com.smilebat.learntribe.openai.response.Choice;
import com.smilebat.learntribe.openai.response.OpenAiResponse;
import com.smilebat.learntribe.processor.converters.ProfileSummaryConverter;
import com.smilebat.learntribe.processor.converters.ResumeConverter;
import com.smilebat.learntribe.processor.services.experiences.ExperienceService;
import com.smilebat.learntribe.resume.ResumeBuilderRequest;
import com.smilebat.learntribe.resume.response.ResumeBuilderResponse;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
      throw new IllegalArgumentException("Unable to create resume : Maximum limit reached");
    }
    Resume resume = resumeConverter.toEntity(request);
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

  /**
   * Retrieves/Predicts the personal summary of a user.
   *
   * @param keyCloakId the IAM id.
   * @param pageNum the page number.
   * @return the {@link Collection} of String.
   */
  @Transactional
  public Collection<String> getPersonalSummaries(String keyCloakId, int pageNum) {
    Verify.verifyNotNull(keyCloakId, "IAM user id cannot be null");
    //    UserProfile profile = profileRepository.findByKeyCloakId(keyCloakId);
    //    if (profile == null) {
    //      return Collections.emptyList();
    //    }
    //    String skills = profile.getSkills();
    //    final String[] skillsArr = skills.split(",");
    //    String userCurrentRole = profile.getCurrentDesignation();
    //    Set<ProfileSummary> profileSummaries = new HashSet<>();
    //    int totalSkills = skillsArr.length;
    //    int requiredSummaryPerSkill = 25 / totalSkills;
    //    for (String skill : skillsArr) {
    //      if (profileSummaries.size() >= 25) {
    //        break;
    //      }
    //      Pageable pageable = PageRequest.of(pageNum, requiredSummaryPerSkill);
    //      final List<ProfileSummary> skillSummaries =
    //          summaryRepository.findByRoleAndSkill(userCurrentRole, skill, pageable);
    //      profileSummaries.addAll(skillSummaries);
    //    }

    suggestSummaries("Software Enginner", "Java");
    return Collections.emptyList();
    // return converter.toResponse(profileSummaries);
  }

  private Set<ProfileSummary> suggestSummaries(String role, String skill) {
    final String text = getOpenAiSuggestions(role, skill);
    Set<String> summaries = parseCompletedText(text);
    return getProcessedSummaries(role, skill, summaries);
  }

  private Set<ProfileSummary> getProcessedSummaries(
      String userCurrentRole, String skill, Set<String> summaries) {
    return summaries
        .stream()
        .filter(StringUtils::isNotEmpty)
        .map(summary -> createProfileSummary(userCurrentRole, skill, summary))
        .collect(Collectors.toSet());
  }

  /**
   * Helper to create profile summary entity.
   *
   * @param userCurrentRole the Current role of user
   * @param skill the skill of user.
   * @param summary the summary of the profile.
   * @return the {@link Function}.
   */
  private ProfileSummary createProfileSummary(
      String userCurrentRole, String skill, String summary) {
    ProfileSummary profileSummary = new ProfileSummary();
    profileSummary.setRole(userCurrentRole);
    profileSummary.setSkill(skill);
    profileSummary.setSummary(summary);
    return profileSummary;
  }

  /**
   * Retrieves suggestions from Open AI.
   *
   * @param userCurrentRole the User current role.
   * @param skill the user skill.
   * @return the {@link String}.
   */
  private String getOpenAiSuggestions(String userCurrentRole, String skill) {
    OpenAiRequest request = createOpenAiRequest(userCurrentRole, skill);
    OpenAiResponse completions = openAiService.getCompletions(request);
    final Choice choice = completions.getChoices().get(0);
    return choice.getText();
  }

  /**
   * Creates a Open AI request.
   *
   * @param role the role of user {@link String}.
   * @param skill the skill of user {@link String}.
   * @return the {@link OpenAiRequest}.
   */
  private OpenAiRequest createOpenAiRequest(String role, String skill) {
    String prompt = "Create 2 Good Profile Summaries for " + role + " who is skilled in " + skill;
    if (skill == null) {
      prompt = "Create 2 Good Profile Summaries for " + role;
    }
    OpenAiRequest request = new OpenAiRequest();
    request.setPrompt(prompt);
    request.setTemperature(0.9f);
    return request;
  }

  /**
   * Parses the text completion for query extractions.
   *
   * @param str the completed open ai text.
   * @return Set of String.
   */
  private Set<String> parseCompletedText(String str) {
    String[] arr = str.split("\n");
    Set<String> summaries = new HashSet<>(3);
    int index = 1;
    int arrLen = arr.length;
    while (index < arrLen) {
      while (arr[index].isBlank()) {
        index += 1;
      }
      String inputText = arr[index].trim();
      inputText = inputText.replaceAll("[0-9]. ", "");
      summaries.add(inputText);
      index++;
    }
    return summaries;
  }
}
