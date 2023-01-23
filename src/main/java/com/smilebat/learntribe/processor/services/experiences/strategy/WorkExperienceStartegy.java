package com.smilebat.learntribe.processor.services.experiences.strategy;

import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeWorkExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.WorkExperience;
import com.smilebat.learntribe.processor.services.experiences.DefaultExperienceStrategy;
import com.smilebat.learntribe.processor.services.experiences.context.WorkExperienceContext;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Business Logic implementation for computing work experiences
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Service
public final class WorkExperienceStartegy
    extends DefaultExperienceStrategy<WorkExperienceContext, WorkExperience> {
  @Override
  public void updateExperiences(WorkExperienceContext context) {
    log.info("Updating Work Experiences for User Resumes");
    evaluateExperiences(context);
    evaluateCurrentRole(context);
  }

  private void evaluateExperiences(WorkExperienceContext context) {
    super.updateExperiences(context);
    context.getProfile().setWorkExperiences(new TreeSet<>(context.getUpdatedExperiences()));
  }

  private void evaluateCurrentRole(WorkExperienceContext context) {
    Set<ResumeWorkExperience> updatedExperiences = context.getProfile().getWorkExperiences();
    if (updatedExperiences != null && !updatedExperiences.isEmpty()) {
      final ResumeWorkExperience latestWorkExperience =
          updatedExperiences.stream().findFirst().get();
      context.getProfile().setCurrentDesignation(latestWorkExperience.getDesignation());
    }
  }
}
