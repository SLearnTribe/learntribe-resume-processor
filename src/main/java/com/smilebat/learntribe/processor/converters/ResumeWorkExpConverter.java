package com.smilebat.learntribe.processor.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeEducationExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.ResumeWorkExperience;
import com.smilebat.learntribe.dataaccess.jpa.entity.WorkExperience;
import com.smilebat.learntribe.inquisitve.EducationalExpRequest;
import com.smilebat.learntribe.inquisitve.WorkExperienceRequest;
import com.smilebat.learntribe.inquisitve.response.WorkExperienceResponse;
import com.smilebat.learntribe.processor.util.Commons;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converter for the Resume Work Experience.
 *
 * <p>Copyright &copy; 2022 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Component
@RequiredArgsConstructor
public class ResumeWorkExpConverter {
  private final Commons commons;

  /**
   * Converts {@link WorkExperienceRequest} to {@link ResumeWorkExperience}.
   *
   * @param request the {@link WorkExperienceRequest}
   * @return the {@link WorkExperience}
   */
  public ResumeWorkExperience toEntity(WorkExperienceRequest request) {
    ResumeWorkExperience workExperience = new ResumeWorkExperience();
    workExperience.setId(request.getId());
    workExperience.setDesignation(request.getDesignation());
    workExperience.setOrgName(request.getOrgName());
    String startDate = request.getStartDate();
    String endDate = request.getEndDate();
    if (startDate != null) {
      workExperience.setStartDate(commons.toInstant(startDate));
    }
    if (endDate != null) {
      workExperience.setEndDate(commons.toInstant(endDate));
    }
    workExperience.setYears(request.getYears());
    workExperience.setLocation(request.getLocation());
    workExperience.setDescription(request.getDescription());
    return workExperience;
  }

  /**
   * Converts List of {@link EducationalExpRequest} to Set of {@link ResumeEducationExperience}.
   *
   * @param experiences the list of {@link WorkExperienceRequest}.
   * @return the set of {@link ResumeWorkExperience}.
   */
  public Set<ResumeWorkExperience> toEntities(Collection<WorkExperienceRequest> experiences) {
    return experiences.stream().map(this::toEntity).collect(Collectors.toSet());
  }

  /**
   * Converts {@link ResumeWorkExperience} to {@link WorkExperienceResponse}.
   *
   * @param workExperience the {@link ResumeWorkExperience}
   * @return the {@link WorkExperienceResponse}
   */
  public WorkExperienceResponse toResponse(ResumeWorkExperience workExperience) {
    WorkExperienceResponse response = new WorkExperienceResponse();
    response.setId(workExperience.getId());
    response.setDesignation(workExperience.getDesignation());
    response.setLocation(workExperience.getLocation());
    Instant startDate = workExperience.getStartDate();
    Instant endDate = workExperience.getEndDate();
    if (startDate != null) {
      response.setStartDate(commons.formatInstant.apply(startDate));
    }
    if (endDate != null) {
      response.setEndDate(commons.formatInstant.apply(endDate));
    }
    response.setYears(workExperience.getYears());
    response.setOrgName(workExperience.getOrgName());
    response.setDescription(workExperience.getDescription());
    return response;
  }

  /**
   * Converts List of {@link ResumeWorkExperience} to List of {@link WorkExperienceResponse}.
   *
   * @param workExperiences the list of {@link ResumeWorkExperience}
   * @return the list of {@link WorkExperienceResponse}
   */
  public List<WorkExperienceResponse> toResponse(List<ResumeWorkExperience> workExperiences) {
    return workExperiences.stream().map(this::toResponse).collect(Collectors.toList());
  }
}
