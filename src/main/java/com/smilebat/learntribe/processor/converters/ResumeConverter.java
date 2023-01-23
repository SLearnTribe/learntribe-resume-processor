package com.smilebat.learntribe.processor.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.Resume;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeEducationExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeSideProject;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeWorkExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import com.smilebat.learntribe.inquisitve.response.EducationalExpResponse;
import com.smilebat.learntribe.inquisitve.response.SideProjectResponse;
import com.smilebat.learntribe.inquisitve.response.UserProfileResponse;
import com.smilebat.learntribe.inquisitve.response.WorkExperienceResponse;
import com.smilebat.learntribe.resume.ResumeBuilderRequest;
import com.smilebat.learntribe.resume.response.ResumeBuilderResponse;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converter for the Resume Request to entity.
 *
 * <p>Copyright &copy; 2022 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Component
@RequiredArgsConstructor
public class ResumeConverter {

  private final ResumeWorkExpConverter workExpConverter;
  private final ResumeEdExpConverter edExpConverter;

  private final SideProjectsConverter sideProjectsConverter;

  /**
   * Converts {@link ResumeBuilderRequest} to {@link Resume}.
   *
   * @param request the {@link ResumeBuilderRequest}.
   * @return the {@link Resume}.
   */
  public Resume toEntity(ResumeBuilderRequest request) {
    Resume resume = new Resume();
    updateEntity(request, resume);
    return resume;
  }

  /**
   * Updates the {@link Resume} from {@link ResumeBuilderRequest}.
   *
   * @param request the {@link ResumeBuilderRequest}.
   * @param resume the {@link Resume}.
   */
  public void updateEntity(ResumeBuilderRequest request, Resume resume) {
    resume.setKeyCloakId(request.getKeyCloakId());
    resume.setAbout(request.getAbout());
    resume.setCountry(request.getCountry());
    resume.setCurrentDesignation(request.getCurrentDesignation());
    resume.setLinkedIn(request.getLinkedIn());
    resume.setEmail(request.getEmail());
    resume.setName(request.getName());
    resume.setPhone(request.getPhone());
    resume.setAddress(request.getAddress());
  }

  /**
   * Converts the {@link UserProfile} to {@link UserProfileResponse}.
   *
   * @param profile the {@link UserProfile}
   * @return the {@link UserProfileResponse}
   */
  public ResumeBuilderResponse toResponse(Resume profile) {
    ResumeBuilderResponse response = new ResumeBuilderResponse();
    response.setAbout(profile.getAbout());
    response.setCountry(profile.getCountry());
    response.setEmail(profile.getEmail());
    response.setLinkedIn(profile.getLinkedIn());
    response.setName(profile.getName());
    response.setPhone(profile.getPhone());
    response.setSkills(profile.getSkills());
    response.setCurrentDesignation(profile.getCurrentDesignation());

    Set<ResumeWorkExperience> experienceSet = profile.getWorkExperiences();
    if (experienceSet != null && !experienceSet.isEmpty()) {
      List<WorkExperienceResponse> workExperienceResponses =
          workExpConverter.toResponse(experienceSet.stream().collect(Collectors.toList()));
      response.setWorkExperiences(workExperienceResponses);
    }

    Set<ResumeEducationExperience> edExperienceSet = profile.getEducationExperiences();
    if (edExperienceSet != null && !edExperienceSet.isEmpty()) {
      List<EducationalExpResponse> educationalExpResponses =
          edExpConverter.toResponse(edExperienceSet.stream().collect(Collectors.toList()));
      response.setEducationExperiences(educationalExpResponses);
    }

    Set<ResumeSideProject> sideProjects = profile.getSideProjects();
    if (sideProjects != null && !sideProjects.isEmpty()) {
      List<SideProjectResponse> sideProjectResponses =
          sideProjectsConverter.toResponse(sideProjects.stream().collect(Collectors.toList()));
      response.setSideProjects(sideProjectResponses);
    }

    return response;
  }

  /**
   * Converts List of {@link Resume} to List of {@link ResumeBuilderResponse}.
   *
   * @param profiles the List of {@link Resume}
   * @return the List of {@link ResumeBuilderResponse}
   */
  public List<ResumeBuilderResponse> toResponse(Collection<Resume> profiles) {
    return profiles.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
