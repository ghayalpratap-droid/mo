package edu.sis.domain;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalIntegrationSimulator implements ExternalReceiver {
  final Set<String> failTargets;

  public LocalIntegrationSimulator(@Value("${SIS_SIMULATOR_FAIL_TARGETS:}") String failures) {
    failTargets = new HashSet<>(Arrays.asList(failures.split(",")));
  }

  public Receipt receive(Event event) {
    if (failTargets.contains(event.target().name()))
      throw new IllegalStateException("Configured synthetic receiver failure");
    return new Receipt("SIMULATED_" + event.target(), "ACKNOWLEDGED");
  }
}
