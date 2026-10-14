package edu.sis.domain;

import java.util.UUID;

/**
 * Synthetic contract: no Mule RAML, BCF session protocol or Oracle AQ implementation is available.
 */
public interface ExternalReceiver {
  enum Target {
    MULE,
    CMS,
    BAS,
    CEM,
    DSFS,
    LAP,
    PREPAY,
    BCF,
    ORACLE_AQ,
    LLE_SALESFORCE
  }

  record Event(UUID id, String provider, UUID aggregateId, String eventType, Target target) {}

  record Receipt(String receiver, String status) {}

  Receipt receive(Event event);
}
