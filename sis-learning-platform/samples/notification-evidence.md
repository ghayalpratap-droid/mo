# Documented PG/NI notification processing

Classification: DOCUMENTED_CURRENT. Source: supplied Ne.md, section 3.2, lines 235–260. This is supplied documentation, not independently inspected production source.

The documented sis-notification-service receives PG/NI application notifications through POST /v1/appNotification. Its application processor selects validation strategies and configured rules. Configured rule ordering is read from RCA_GROUP_FUNCTION_MAPPINGS. Tuition period, CoC and confirmation records are persisted. CoC publication to LAP uses Oracle AQ XXAQ_SIS_COC_JMS_TXT_Q when the configured LAP toggle allows it. Registration and attendance publication goes through sis-publisher. Kafka is not the documented current message broker.

# Synthetic demo confirmation rules

Classification: DEMO_ASSUMPTION. The educational demo requires confirmed registration before attendance can be confirmed. Suspension requires an active application with confirmed attendance. Resumption is accepted only from suspended state. Invalid submitted changes retain a rejected decision and immutable history. These are synthetic rules and are not official SLC policy.

# Unknown information

Unavailable Mule specifications, stored-procedure bodies and shared-library source cannot be recovered from this document. Answers must identify missing evidence rather than invent production behavior.
