package com.smilebat.learntribe.processor.converters;

import com.smilebat.learntribe.dataaccess.jpa.entity.ProfileSummary;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Converter for the Profile Summary.
 *
 * <p>Copyright &copy; 2022 Smile .Bat.
 *
 * @author Pai,Sai Nandan.
 */
@Component
public class ProfileSummaryConverter {

  /**
   * Converts {@link Collection} of ProfileSummary to Set of Strings.
   *
   * @param summaries the {@link Collection} of ProfileSummary.
   * @return the Set of String.
   */
  public Set<String> toResponse(Collection<ProfileSummary> summaries) {
    return summaries.stream().map(ProfileSummary::getSummary).collect(Collectors.toSet());
  }
}
