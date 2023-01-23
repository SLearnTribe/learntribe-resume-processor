package com.smilebat.learntribe.processor.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.EducationExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeEducationExperience;
import com.smilebat.learntribe.inquisitve.EducationalExpRequest;
import com.smilebat.learntribe.inquisitve.WorkExperienceRequest;
import com.smilebat.learntribe.inquisitve.response.EducationalExpResponse;
import com.smilebat.learntribe.processor.util.Commons;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converter for the Resume Education experience.
 *
 * <p>Copyright &copy; 2022 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Component
@RequiredArgsConstructor
public class ResumeEdExpConverter {
  private final Commons commons;

  /**
   * Converts {@link EducationalExpRequest} to {@link ResumeEducationExperience}.
   *
   * @param request the {@link WorkExperienceRequest}
   * @return the {@link EducationExperience}
   */
  public ResumeEducationExperience toEntity(EducationalExpRequest request) {
    ResumeEducationExperience edExperience = new ResumeEducationExperience();
    edExperience.setId(request.getId());
    edExperience.setDegree(request.getDegree());
    edExperience.setCollegeName(request.getCollegeName());
    edExperience.setFieldOfStudy(request.getFieldOfStudy());
    String endDate = request.getDateOfCompletion();
    if (endDate != null) {
      edExperience.setDateOfCompletion(commons.toInstant(endDate));
    }
    return edExperience;
  }

  /**
   * Converts List of {@link EducationalExpRequest} to Set of {@link ResumeEducationExperience}.
   *
   * @param experiences the list of {@link WorkExperienceRequest}.
   * @return the set of {@link ResumeEducationExperience}.
   */
  public Set<ResumeEducationExperience> toEntities(Collection<EducationalExpRequest> experiences) {
    return experiences.stream().map(this::toEntity).collect(Collectors.toSet());
  }

  /**
   * Converts {@link ResumeEducationExperience} to {@link EducationalExpResponse}.
   *
   * @param edExperience the {@link ResumeEducationExperience}
   * @return the {@link EducationalExpResponse}
   */
  public EducationalExpResponse toResponse(ResumeEducationExperience edExperience) {
    EducationalExpResponse response = new EducationalExpResponse();
    response.setId(edExperience.getId());
    response.setDegree(edExperience.getDegree());
    response.setCollegeName(edExperience.getCollegeName());
    response.setFieldOfStudy(edExperience.getFieldOfStudy());
    Instant dateOfCompletion = edExperience.getDateOfCompletion();
    if (dateOfCompletion != null) {
      response.setDateOfCompletion(commons.formatInstant.apply(dateOfCompletion));
    }
    return response;
  }

  /**
   * Converts List of {@link ResumeEducationExperience} to List of {@link EducationalExpResponse}.
   *
   * @param educationExperiences the list of {@link ResumeEducationExperience}
   * @return the list of {@link EducationalExpResponse}
   */
  public List<EducationalExpResponse> toResponse(
      List<ResumeEducationExperience> educationExperiences) {
    return educationExperiences.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
