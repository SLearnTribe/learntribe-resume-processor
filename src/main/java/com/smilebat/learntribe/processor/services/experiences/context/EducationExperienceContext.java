package com.smilebat.learntribe.processor.services.experiences.context;

import com.smilebat.learntribe.dataaccess.ResumeEducationExperienceRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.Resume;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeEducationExperience;
import com.smilebat.learntribe.inquisitve.EducationalExpRequest;
import com.smilebat.learntribe.processor.converters.ResumeEdExpConverter;
import com.smilebat.learntribe.processor.services.experiences.ExperienceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Context for education experience
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public final class EducationExperienceContext
    extends ExperienceContext<ResumeEducationExperience, ResumeEducationExperienceRepository> {

  private final ResumeEdExpConverter converter;
  private final ResumeEducationExperienceRepository repository;

  @Getter @Setter private Collection<EducationalExpRequest> request;

  @Setter @Getter private Resume profile;

  @Override
  public ResumeEducationExperienceRepository getRepository() {
    return this.repository;
  }

  @Override
  public Set<ResumeEducationExperience> getRequestExperiences() {
    return request == null ? Collections.emptySet() : converter.toEntities(request);
  }

  @Override
  public Set<ResumeEducationExperience> getExistingExperiences() {
    return profile.getEducationExperiences();
  }
}
