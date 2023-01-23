package com.smilebat.learntribe.processor.services.experiences.context;

import com.smilebat.learntribe.dataaccess.ResumeWorkExperienceRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.Resume;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeWorkExperience;
import com.smilebat.learntribe.inquisitve.WorkExperienceRequest;
import com.smilebat.learntribe.processor.converters.ResumeWorkExpConverter;
import com.smilebat.learntribe.processor.services.experiences.ExperienceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Context for work experience
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@RequiredArgsConstructor
public final class WorkExperienceContext
    extends ExperienceContext<ResumeWorkExperience, ResumeWorkExperienceRepository> {
  private final ResumeWorkExpConverter converter;

  private final ResumeWorkExperienceRepository repository;

  @Setter @Getter private Collection<WorkExperienceRequest> request;

  @Getter @Setter private Resume profile;

  @Override
  public ResumeWorkExperienceRepository getRepository() {
    return this.repository;
  }

  @Override
  public Set<ResumeWorkExperience> getRequestExperiences() {
    return request == null ? Collections.emptySet() : converter.toEntities(request);
  }

  @Override
  public Set<ResumeWorkExperience> getExistingExperiences() {
    return profile.getWorkExperiences();
  }
}
