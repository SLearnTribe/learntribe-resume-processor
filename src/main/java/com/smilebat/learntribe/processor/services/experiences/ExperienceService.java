package com.smilebat.learntribe.processor.services.experiences;

import com.smilebat.learntribe.dataaccess.jpa.entity.Resume;
import com.smilebat.learntribe.processor.services.experiences.context.EducationExperienceContext;
import com.smilebat.learntribe.processor.services.experiences.context.SideProjectContext;
import com.smilebat.learntribe.processor.services.experiences.context.WorkExperienceContext;
import com.smilebat.learntribe.processor.services.experiences.strategy.EducationExperienceStartegy;
import com.smilebat.learntribe.processor.services.experiences.strategy.SideProjectStrategy;
import com.smilebat.learntribe.processor.services.experiences.strategy.WorkExperienceStartegy;
import com.smilebat.learntribe.resume.ResumeBuilderRequest;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Helper Service class for executing the contexts.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Service
@RequiredArgsConstructor
public class ExperienceService {
  private final WorkExperienceStartegy workExperienceStrategy;

  private final WorkExperienceContext workExperienceContext;

  private final EducationExperienceStartegy educationExperienceStartegy;
  private final EducationExperienceContext educationExperienceContext;

  private final SideProjectContext sideProjectContext;

  private final SideProjectStrategy sideProjectStrategy;

  /**
   * Executes all experience contexts.
   *
   * @param request the {@link ResumeBuilderRequest}
   * @param profile the {@link Resume}
   */
  @Transactional
  public void saveAllExperiences(ResumeBuilderRequest request, Resume profile) {
    saveWorkExperiences(request, profile);
    saveEducationExperiences(request, profile);
    saveSideProjects(request, profile);
  }

  /**
   * Executes work experience context.
   *
   * @param request the {@link ResumeBuilderRequest}
   * @param profile the {@link Resume}
   */
  private void saveWorkExperiences(ResumeBuilderRequest request, Resume profile) {
    workExperienceContext.setProfile(profile);
    workExperienceContext.setRequest(request.getWorkExperiences());
    workExperienceStrategy.updateExperiences(workExperienceContext);
  }

  /**
   * Executes Education experience contexts.
   *
   * @param request the {@link ResumeBuilderRequest}
   * @param profile the {@link Resume}
   */
  private void saveEducationExperiences(ResumeBuilderRequest request, Resume profile) {
    educationExperienceContext.setProfile(profile);
    educationExperienceContext.setRequest(request.getEducationExperiences());
    educationExperienceStartegy.updateExperiences(educationExperienceContext);
  }

  /**
   * Executes Side Project experience contexts.
   *
   * @param request the {@link ResumeBuilderRequest}
   * @param profile the {@link Resume}
   */
  private void saveSideProjects(ResumeBuilderRequest request, Resume profile) {
    sideProjectContext.setProfile(profile);
    sideProjectContext.setRequest(request.getSideProjects());
    sideProjectStrategy.updateExperiences(sideProjectContext);
  }
}
