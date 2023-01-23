package com.smilebat.learntribe.processor.services.experiences.context;

import com.smilebat.learntribe.dataaccess.ResumeSideProjectRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.Resume;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeSideProject;
import com.smilebat.learntribe.inquisitve.SideProjectRequest;
import com.smilebat.learntribe.processor.converters.SideProjectsConverter;
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
public final class SideProjectContext
    extends ExperienceContext<ResumeSideProject, ResumeSideProjectRepository> {

  private final SideProjectsConverter converter;
  private final ResumeSideProjectRepository repository;

  @Getter @Setter private Collection<SideProjectRequest> request;

  @Setter @Getter private Resume profile;

  @Override
  public ResumeSideProjectRepository getRepository() {
    return this.repository;
  }

  @Override
  public Set<ResumeSideProject> getRequestExperiences() {
    return request == null ? Collections.emptySet() : converter.toEntities(request);
  }

  @Override
  public Set<ResumeSideProject> getExistingExperiences() {
    return profile.getSideProjects();
  }
}
