# Documentary data dictionary

Extracted sections preserve all supplied fields, types, keys and relations. These are not implementation claims. Candidate inventory requires reconciliation; UNKNOWN means absent evidence.

## Part 3 – pg-coc-service, sis-coc-service, sis-application-service (Ne.md:3241–3242)

# Part 3 – pg-coc-service, sis-coc-service, sis-application-service


## 3.1 pg-coc-service (Ne.md:3243–3246)

## 3.1 pg-coc-service

`@EntityScan`: `uk.co.slc.sis.model`, `uk.co.slc.service.model`. Model library: `sis-model-library:2.0.24`. The service defines **no own entities**.


## Repositories (`uk.co.slc.service.dao`) (Ne.md:3247–3265)

### Repositories (`uk.co.slc.service.dao`)
| Repository | Base | Entity → Table | Custom queries |
|---|---|---|---|
| CocRepository | JpaRepository | Coc → COCS | `findByCsrIdAndColHeiCodeAndAcademicYearAndCocCategoryType`, `findByCocCategoryTypeAndColHeiCodeAndHayIdAndStudentSupportNumber` |
| TuitionPeriodRepository | JpaRepository | TuitionPeriod → TUITION_PERIODS | `findByAutoWithdrawalStatusAndWorkStageAndSource` |
| TuitionPeriodHistoryRepository | JpaRepository | TuitionPeriodHistory → TUITION_PERIOD_HISTORIES | `findByTuitionPeriodIdOrderByCreatedDateTimeDesc` |
| PglConfirmationRequestRepository | JpaRepository | PglConfirmationRequest → PGL_CONFIRMATION_REQUESTS | – |
| PglConfirmationRequestHistRepository | JpaRepository | PglConfirmationRequestHist → PGL_CONFIRMATION_REQUEST_HISTS | `findByPglConfirmationRequestIdOrderByCreatedDateTimeDesc` |
| AttendanceConfRequestRepository | JpaRepository | AttendanceConfRequest → ATTENDANCE_CONF_REQUESTS | – |
| CocStageDetailRepository | JpaRepository | CocStageDetail → COC_STAGE_DETAILS | – |
| CourseRepository | JpaRepository | Course → XXSL_COURSES | `findByCourseCode` |
| CustomerRepository | JpaRepository | Customer → XXSL_CUSTOMERS | – |
| EventRepository | JpaRepository | Event → EVENTS | – |
| HesAcademicYearRepository | JpaRepository | HesAcademicYear → HES_ACADEMIC_YEARS | `findById` |
| AcademicYearRepository | JpaRepository | AcademicYear → XXSL_ACADEMIC_YEARS | – |
| XxslSyncExceptionRepository | CrudRepository | XxslSyncException → XXSL_SYNC_EXCEPTIONS | – |

The service also uses these entities directly, without a repository: TuitionFinanceHistory, TuitionFinanceDetail, RegistrationConfRequest, RegistrationConfReqHist.


## POJOs / DTOs (Ne.md:3266–3279)

### POJOs / DTOs
| Class | Fields |
|---|---|
| model.PgCocProcessData | id String, cocType Integer, ssn, crn, forenames, surname String, dateOfBirth Date, ucasCourseCode, courseName String, dateStudentLeftCourse, returnDate, dateOfResumption, repeatFromDate Date, mainReasonToRepeat, furtherExplanation, resumptionPlacementDetails, resumptionCondition String, resumptionConditionType Integer, colHeiCode, heiCodeTo, crsCourseCode String, withdrawalReasonType, suspensionReasonType Integer, methodOfAttendance String, transferType Integer, academicYear, methodOfStudy, academicYearStartDate String, changeReasonType Integer, dateOfTransfer Date, cbfGuid String |
| model.PgCocProcessDataDTO | Same fields as PgCocProcessData, except dates are String and there is no cbfGuid |
| model.PgCocProcessData1920DTO | Same as PgCocProcessDataDTO, with id renamed to applicationId |
| model.OaqCocData | Same as PgCocProcessData, with id renamed to applicationId and no cbfGuid |
| model.PgCocProcessDataResult | pgCocProcessData PgCocProcessData |
| dto.PgCocRequest | 30 fields, the same as PgCocProcessDataDTO. All are String except cocType, withdrawalReasonType and changeReasonType (Integer) |
| dto.PgCocResponse | status String, message String |
| AutoWithdrawalDto | tuitionPeriod, tuitionPeriodHistory, pglConfirmationRequest, pglConfirmationRequestHist, event, tuitionFinanceHistory (entities) |
| dao.PgCocProcessDataRequest | pgCocProcessData PgCocProcessData, validateOnly boolean |
| dao.PgCocProcessDataResponse | id Long, errorMessage String |


## Stored procedures (Ne.md:3280–3285)

### Stored procedures
| Class | Procedure | Parameters / cursor columns |
|---|---|---|
| PGetPgCocProcessDataDetails | `KIFPK032.p_get_pgl_coc_records` | IN/OUT P_GROUP_NAME. Cursor P_PGL_COC_DATA returns: CBF_GUID, COC_CATEGORY_TYPE, STUDENT_SUPPORT_NUMBER, ART_ID, FORENAMES, SURNAME, DATE_OF_BIRTH, UCAS_COURSE_CODE, COURSE_NAME, DATE_STUDENT_LEFT_COURSE, DATE_OF_RETURN, DATE_OF_RESUMPTION, REPEAT_FROM_DATE, MAIN_REASONS_TO_REPEAT, FURTHER_EXPLANATION, RESUMPTION_PLACEMENT_DETAILS, RESUMPTION_CONDITIONS, RESUMPTION_CONDITION_TYPE, COL_HEI_CODE, COL_HEI_CODE_TRANSFER_INTO, CRS_COURSE_CODE, WITHDRAWAL_REASON_TYPE, SUSPENSION_REASON_TYPE, METHOD_OF_ATTENDANCE, TRANSFER_TYPE, ACADEMIC_YEAR, MODE_OF_STUDY, CURRENT_ACY_COURSE_START_DATE, CHANGE_REASON_TYPE, DATE_OF_TRANSFER, SOURCE_APPLICATION_REFERENCE |
| PGetPgCocResponseDataDetails | `KIFPK032.p_call_back_response` | P_RESPONSE_STATUS, P_GUID, P_RESPONSE_CODE, P_RESPONSE_MESSAGE |


## 3.2 sis-coc-service (Ne.md:3286–3289)

## 3.2 sis-coc-service

`@EntityScan`: `uk.co.slc.sis.model`, `uk.co.slc.sis.app.data`. Model libraries: `sis-model-library:2.0.23` and `lle-model-library:1.0.82`.


## Own entity (Ne.md:3290–3290)

### Own entity

## XxslProtocolProperties → XXSL_PROTOCOL_PROPERTIES (Ne.md:3291–3296)

#### XxslProtocolProperties → XXSL_PROTOCOL_PROPERTIES
| Field | Column | Type | Notes |
|---|---|---|---|
| xsslProtocolPropertiesId | PROPERTY + set_name | XsslProtocolPropertiesId | `@EmbeddedId` |
| value | VALUE | String | |


## XsslProtocolPropertiesId (`@Embeddable`) (Ne.md:3297–3302)

#### XsslProtocolPropertiesId (`@Embeddable`)
| Field | Column | Type |
|---|---|---|
| property | PROPERTY | String |
| setName | set_name | String |


## Repositories (Ne.md:3303–3324)

### Repositories
| Repository | Entity → Table | Custom queries |
|---|---|---|
| TuitionPeriodRepository | TuitionPeriod → TUITION_PERIODS | 5 native queries: `getTuitionPeriodsByKeys`, 2× `getActiveTuitionPeriodList`, 2× `getTuitionPeriods`. Subqueries use XXHE.HES_ACADEMIC_YEARS, TUITION_PERIOD_APPLICATIONS and XXSL_CUSTOMERS |
| TuitionPeriodApplicationRepository | TuitionPeriodApplication → TUITION_PERIOD_APPLICATIONS | native `findByExternalId` |
| StageFileUploadRepository | StageFileUpload → STAGE_FILE_UPLOADS | JPQL `fetchStageFileUploadsList`; `findByBulkFileSubmission` |
| ImportCocFailureDetailRepository | ImportCocFailureDetail → IMPORT_COC_FAILURE_DETAILS | – |
| CocFailureDetailRepository | utils.CocFailureDetails → COC_FAILURE_DETAILS | – |
| BulkFileSubmissionRepository | BulkFileSubmission → BULK_FILE_SUBMISSIONS | JPQL `fetchBulkFileSubmissionList` (importFileType=5, importStatusType=4) |
| HesAcademicYearRepository | HesAcademicYear → HES_ACADEMIC_YEARS | native `getHESAcademicYear`, `getHESAcademicYearByAcyIdAndServiceType` |
| CustomerRepository | Customer → XXSL_CUSTOMERS | native `getCustomer` (SSN), `getCustomerWithCRN` (ART_ID), `findCustomerBySSN` |
| CourseYearRepository | CourseYear → XXSL_COURSE_YEARS | native `getCourseYearByCourseCodeYearAndAcy` |
| CourseRepository | Course → XXSL_COURSES | `findByCourseCode`, `findByUcasCourse` |
| CocStageDetailRepository | CocStageDetail → COC_STAGE_DETAILS | – |
| CocRepository | Coc → COCS | native `retrieveCocs` |
| XsslProtocolPropertiesRepository | XxslProtocolProperties → XXSL_PROTOCOL_PROPERTIES | `findByXsslProtocolPropertiesId` |
| XxslSyncExceptionRepository | XxslSyncException → XXSL_SYNC_EXCEPTIONS | – |
| lle.LleCourseRepository | XxlcCourseVariant → XXLC_COURSE_VARIANTS | native `findCourseStartDate` (xxlc_course_variants / var_acad_yrs / var_years / study_periods), `findModuleStartDate` (xxlc_module_* / study_periods) |
| lle.LleCocRepository | XxleCoc → XXLE_COCS | – |
| lle.LleBulkFileSubmissionsRepository | XxleBulkFileSubmissions → XXLE_BULK_FILE_SUBMISSIONS | – |
| lle.AppCourseRepository | XxleApplicationCourse → XXLE_APPLICATION_COURSES | `findBySlcCourseCode`, `findByCsrIdAndColHeiCodeAndSlcCourseCode`, `findByXxleApplicationSourceApplicationRefAndAcyIdAndSlcCourseCodeAndCsrId`; native `getAutowithdrawalRecords` |


## POJOs / DTOs (Ne.md:3325–3354)

### POJOs / DTOs
| Class | Fields |
|---|---|
| CocRequest (abstract) | applicationKey ApplicationKey, sapId String, cocNotificationId Long |
| FeeWaiverCocRequest (extends CocRequest) | feeWaiverApprovedAmount BigDecimal |
| CocCreditRequest | tupId Long, newCreditValue Long, errorMsg String |
| CocServiceData | tuitionPeriodApplication, tuitionPeriod, course, customer, coc (entities), cocNotificationId Long, errorMessages List&lt;String&gt;, pageId String. *Lines 60–249 not read* |
| ApplicationKey | customerId Long, heiCode String, academicYearId Integer, courseCode Long, courseYear Integer |
| CocContact | contactId Long, title, foreName, surName, jobTitle, workPhoneNo, workExtensionNo, workEmailAddress String |
| BatchStatusData | batchFailures List&lt;XxleImportFailureDetails&gt;, batchResults List&lt;XxleImportResults&gt;, processedCount, numberBlanks, numberInvalid int |
| CocCsvUploadNotificationDetails | bfsId Long, heiCode String, numberOfRecords Integer, hco_id String |
| LleImportResultCount | failCount int, successCount int |
| LleFeeWaiverCocPublishData | cocRef, artId, sourceApplicationId, effectiveDate String; changeReasonType, cocCategoryType Integer; feeWaiverApprovedAmount BigDecimal; courses List&lt;LleCocCoursePublishData&gt;; contactDetails List&lt;LleCocContactDetails&gt; |
| StudentInformation | bursaryConsentInd, childcareGrantInd, sponsor1BursaryConsentInd, sponsor2BursaryConsentInd String; numberOfSponsors Integer; nhsInd, leaId String |
| TuitionFee | courseFee, loan, grant BigDecimal, … *partially read* |
| TuitionFeeSchedule | scheduleEntries Map&lt;Integer, TuitionFeeScheduleEntry&gt; |
| MaintenanceFee | grant, loan BigDecimal, loanAppliedFor String |
| validator.ValidationRequest | pageid String, rcaId Long, commonValidationObject CommonValidationObject, errorMessage List&lt;String&gt; |
| validator.ValidationResponse | responseMessage String, validationMessages List&lt;String&gt;, httpCode int |
| validator.CommonValidationObject | customerId Long, heiCode String, academicYearId Integer, courseCode Long, courseYear Integer, studentSupportNumber String, feeWaiverApprovedAmount BigDecimal, hayId Long, modeOfStudy String, externalId BigDecimal, sourceApplicationReference String, dateOfBirth Date, tuitionPeriodId Long, source SourceEnum, cocType, ucasCode, courseYearType, tuitionFeeAmount, tuitionLiabilityAmount, otherCourseYear String, … *only lines 1–30 of 481 read* |
| lle.data.LleCreateCocPublishData | cocRef, artId, sourceApplicationId, effectiveDate String; changeReasonType, cocCategoryType Integer; courses, contactDetails lists |
| lle.data.LleCreateCocOAQRequest | createCocPublishData LleCreateCocPublishData |
| lle.data.LleCocCoursePublishData | courseCode Long, colHeiCode String, acyId, courseYear Integer, moduleInd, courseEndDate, transferType, furtherExplanation String, feeCredits List&lt;LleFeeCreditDetails&gt; |
| lle.data.LleCocContactDetails | forename, title, surname, jobTitle, email, extension, telephone String |
| lle.data.LleFeeCreditDetails | feeCreditType String, credits Integer, tuitionFeeChargedAmount, tuitionFeeSLCAmount BigDecimal |
| oaq.request.CocOAQMessage | cocId Long, artId, forenames, surname, dateOfBirth, ucasCourseCode String, crsCourseCode Long, courseName, dateLastAttended, dateStudentLeftCourse String, originalDurationOfCourse Integer, dateOfReturn, dateOfResumption, periodCourseToRepeat, repeatFromDate, repeatToDate, mainReasonsToRepeat, academicApproval, approvalDate String, … *only lines 1–40 of 553 read* |
| integration.CocNotificationResponse | feeWaiverCocRequest FeeWaiverCocRequest, errorMessage String, httpCode int |
| integration.ApplicationNotificationResponse | cocCreditRequestList List&lt;CocCreditRequest&gt;, errorMessage String, httpCode int |
| Enums | TuitionFeeScheduleEntry (YES, NO, SUSPENDED), SentToSisViaEdsInd, ResumptionConditionTypeEnum (*not read*) |


## Raw SQL (JDBC DAOs) (Ne.md:3355–3365)

### Raw SQL (JDBC DAOs)
| DAO.method | Tables |
|---|---|
| JdbcToggleDAO.getPGNIPrePayLapRangeForCoc | XXSL_REF_CODES (rv_domain='LAP_UPLIFT_SIS_AY_RANGE') |
| JdbcContactsDAO.getHcoId | HEI_HES_CONTACTS, HEI_SERVICE_CONTACTS, SERVICE_CONTACT_TYPES |
| JdbcCommonDAO.findBySapId / getApplicationBySapId | XXLE_APPLICATIONS |
| JdbcCommonDAO.findByApplicationKeyValues / getApplicationCourseId | XXLE_APPLICATIONS, XXLE_APPLICATION_COURSES |
| JdbcCommonDAO.isValidHeiCode | XXSL_COLLEGES |
| JdbcCommonDAO.getModuleCourseStartDate | XXLC_COURSE_VARIANTS, XXLC_COURSE_VAR_ACAD_YRS, XXLC_COURSE_VAR_YEARS, XXLC_STUDY_PERIODS, UNION XXLC_MODULE_VARIANTS, XXLC_MODULE_VAR_ACAD_YRS, XXLC_MODULE_VAR_YEARS |
| JdbcCocCsvImportDAO.fetchStageFileUploadsDataList | STAGE_FILE_UPLOADS, IMPORT_COC_FAILURE_DETAILS |


## 3.3 sis-application-service (Ne.md:3366–3369)

## 3.3 sis-application-service

`@EntityScan`: `uk.co.slc.sis.model`, `uk.co.slc.sis.app.entity`. The `entity` package is empty, so the service has **no own entities**.


## Repositories (Ne.md:3370–3396)

### Repositories
| Repository | Entity → Table | Custom queries |
|---|---|---|
| TuitionPeriodRepository | TuitionPeriod → TUITION_PERIODS | `findByCsrIdAndColHeiCodeAndHayId`, `findByCsrIdAndColHeiCodeAndHayIdAndCourseYearAndCrsCourseCode`, `findByCsrIdAndHayId` |
| TuitionPeriodApplicationRepository | TuitionPeriodApplication → TUITION_PERIOD_APPLICATIONS | `findByExternalId`, `findBysourceApplicationReferenceAndAcyId`, `existsBySourceApplicationReference` |
| RuleConfigAttributeRepository | RuleConfigAttribute → RULE_CONFIG_ATTRIBUTES | – |
| RegistrationConfRequestRepository | RegistrationConfRequest → REGISTRATION_CONF_REQUESTS | native `getRegConfByThreePartKey`, `getRegConfsAtOtherHeis`; `findByCsrIdAndHayId` |
| RcaGroupMappingRepository | RcaGroupMapping → RCA_GROUP_MAPPINGS | – |
| RcaGroupFunctionMappingRepository | RcaGroupFunctionMapping → RCA_GROUP_FUNCTION_MAPPINGS | native (RGP_ID, ACTIVE_IND, WORK_STAGE) |
| RCAGroupAttributeRepository | RCAGroupAttribute → RCA_GROUP_ATTRIBUTES | native join to RCA_GROUP_MAPPINGS |
| PublisherConfigAttributeRepository | PublisherConfigAttribute → PUBLISHER_CONFIG_ATTRIBUTES | native (message_type) |
| PglConfirmationRequestRepository | PglConfirmationRequest → PGL_CONFIRMATION_REQUESTS | – |
| NotificationTypeConfigRepository | NotificationTypeConfig → NOTIFICATION_TYPE_CONFIGS | native select NOTIFICATION_TYPE_VALUE |
| HesAcademicYearRepository | HesAcademicYear → HES_ACADEMIC_YEARS | native (ACY_ID, SERVICE_TYPE) |
| HebssNotificationStageRepository | HebssNotificationStage → HEBSS_NOTIFICATION_STGS | – |
| HandlerConfigAttributeRepository | HandlerConfigAttribute → HANDLER_CONFIG_ATTRIBUTES | native |
| CustomerRepository | Customer → XXSL_CUSTOMERS | – |
| CourseYearRepository | CourseYear → XXSL_COURSE_YEARS | 2 native queries; `existsByCourseYearPkCrsCourseCodeAndCourseYearPkAyrAcadYr`, `existsByCourseYearPkCrsCourseCodeAndCourseYearPkCourseYear` |
| CourseRepository | Course → XXSL_COURSES | `existsByCourseCode` |
| CocStageDetailRepository | CocStageDetail → COC_STAGE_DETAILS | – |
| CocRepository | Coc → COCS | native `retrieveCocs` |
| AwardAuthorityRepository | AwardAuthority → XXSL_AWARD_AUTHORITIES (id String) | – |
| AttendanceNotificationsRepository | AttendanceNotifications → ATTENDANCE_NOTIFICATIONS | native (5- and 6-part key incl. SAP_ID) |
| AdditionalCustomerDetailsRepository | AdditionalCustomerDetail → ADDITIONAL_CUSTOMER_DETAILS (id AdditionalCustomerDetailPK) | – |
| XxslSyncExceptionRepository | XxslSyncException → XXSL_SYNC_EXCEPTIONS | – |
| dao.PublisherFailureDetailsRepository | PublisherFailureDetails → PUBLISHER_FAILURE_DETAILS | – |


## POJOs / DTOs (Ne.md:3397–3425)

### POJOs / DTOs
| Class | Fields |
|---|---|
| CoreApplication (abstract) | tuitionPeriodId Long, applicationKey ApplicationKey, changeEffectiveDate Date, studentCohortCategory Integer, modeOfStudy, bankDetailsHeldInd String, courseTransfer Integer, autoCocInd String, cocId Long, issueDate Date, clientId Long, fsnStatus String, tuitionPeriodApplication (@JsonIgnore) |
| Application (extends CoreApplication) | Long: pngId, paymentNotificationId, chtId, pncId, sapId, acyId · Components: tuitionFee, tuitionFeeSchedule, maintenanceFee, courseDetails, studentInformation · String: otherLocation, firstPaymentMadeInd, ucasId, studentStatus, newRecord, assessmentType, autoApprovalInd, rejectedRecordInd, internalTransferInd, externalTransferInd, feeRevisionInd, reassessmentInd, deletionFlag, failureReason, source, sourceApplicationReference · Integer: workStageId, withdrawReasonId, multipleAcademicYear, assessmentHistorySequence, changeSourceId, deletionAshSeq, currentAyCourseCredits · Enums: sentToSisViaEdsInd, blockManualRegReasonEnum |
| PglApplication / PgdApplication | chtId, workStageId String, contributionLoanAmount BigDecimal, leaCode, changeSource, source, sourceApplicationId String. PglApplication also has transferTpExists boolean |
| PgLegacyApplication | sourceApplicationId String, customerId Long, source, applicationStatus, assessmentDateTime String, assessmentSequenceNumber Long, leaCode, bankDetailsHeldIndicator, heiCode String, courseCode, academicYear Long, courseYear Integer, maintenanceLoanAmount Long, changeType, cocRef, changeSource String, transferTpExists boolean |
| AbstractApplicationNotificationRequest&lt;T&gt; | applications List&lt;T&gt;, validateOnly boolean |
| ApplicationServiceData | tuitionPeriod, tuitionPeriodCopy, application, tuitionPeriodKey, rcaGroupAttribute, existingRegConfRequest, resetManualRegRcr, existingRegConfCopy, event, courseTermOneStartDate Date, valueBasedHandlerRunCount Integer, applicationSourceEnum, rcaId Long, transferTpExists boolean |
| ApplicationKey | customerId Long, heiCode String, academicYearId Integer, courseCode Long, courseYear Integer |
| CourseDetails | currentAcyCourseStartDate Date |
| RegistrationPublish | customerId Long, sourceApplicationId String, registrationConfirmed boolean, registrationConfirmedDate Date, registrationAcademicYear Integer, messageTimestamp Date |
| RcaBackPopData | tpaId Long, studentCohort BigDecimal, modeOfStudy String, courseCode Long, heiCode String, academicYear, courseYear Integer, source, leaCode String, csrId, rcaId Long |
| RcaBackPopRequest | source String, acyId Integer, rowNum Long |
| TpaRcaData | tpaId Long, rcaId Long |
| HandlerConfigQueryParams | notificationTypeValue, transferCode, feeRevisionInd String |
| ruleconfig.StudyDomicileData | heiCode String, courseCode Long, franchiseInd String, studyDomicile Integer |
| ruleconfig.RuleConfigKey | studentCohort BigDecimal, modeOfStudy String, domicileType Integer, source String |
| ruleconfig.RuleConfigAwardAuthority | leaCode, leaName, country String, domicileType Integer |
| gdpr.LapsedApplicationRequestData | applicationId, productId, loanPackId String, loanAccountId, customerId Long, crn String, academicYear Integer, applicationSource, modeOfStudy, awardingBody String |
| gdpr.LapsedApplicationResponseData | Same fields plus dispositionResult String |
| gdpr.UnattachedCustRequestData / ResponseData | customerId Long, crn String (Response also has dispositionResult String) |
| courseClosure.CourseClosureRequest / Response | courseResourcePath, providerResourcePath, messageId, version String, courses List |
| courseClosure.CourseCodeResponse | courseCode Long, applicationCount Long |
| service.model.RegAndAtnConfirmStatusRequest | sourceApplicationReference String, csrId, acadYear Long, heiCode String, courseCode Long, courseYear Integer, source, studyMode String, registration, attendanceConfirmation, cem, classSystem, lleSalesforce boolean |
| integration.AppResponse | sourceApplicationId, errorMessage String, httpStatusCode int |

*Not read:* TuitionFee, TuitionFeeSchedule, MaintenanceFee, StudentInformation and SentToSisViaEdsInd (likely the same as the sis-coc versions); LapsedApplicationRequest/Response; UnattachedCustRequest/Response; CourseCodeRequest; RegistrationConfirmation; AttendanceConfirmation; SisNotificationResponse; AbstractTuitionPeriod; PgmResponse; the *NotificationResponse classes.


## Raw SQL / stored procedures (Ne.md:3426–3443)

### Raw SQL / stored procedures
| Class.method | Tables / procedure |
|---|---|
| JdbcRuleConfigDAO.getRuleConfigMap | RULE_CONFIG_ATTRIBUTES ⟕ RCA_QUALIFICATIONS |
| JdbcRuleConfigDAO.getStudyDomicileDataForEurope | XXSL_COURSES, XXSL_COURSE_YEARS, XXSL_COURSE_LOCATIONS, XXSL_HEI_LOCNS, XXSL_FRANCHISE_ADDRESSES, XXSL_COLLEGES |
| JdbcRcaBackPopDao.retrieveTpaParameters / updateBatchRCAs | XXSS.TUITION_PERIOD_APPLICATIONS, TUITION_PERIODS, XXHE.HES_ACADEMIC_YEARS, XXSS.ADDITIONAL_CUSTOMER_DETAILS |
| JdbcCurrentAcyYearDao.getCurrentAcademicId | HES_ACADEMIC_YEARS |
| JdbcBursarySisDao.getBursaryApprovalStatus | APPLICATIONS, ASSESSMENTS, ASSESSMENT_ENTITLEMENTS, ENTITLEMENTS, ENT_PAYMENT_INSTALMENTS, PAYMENT_INSTALMENTS |
| JdbcAcdBackPopDao.insertBatchAdditionalCustomerDetail | ADDITIONAL_CUSTOMER_DETAILS (INSERT, 17 cols) |
| UnattachedCustStoredProcedure | `KIFPK059.P_DISPOSE_UNATTACHED_CUSTS` (types XXBE.UNATTCHD_CUST_*) |
| LapsedApplicationStoredProcedure | `KIFPK052.P_DISPOSE_APPS` (types XXBE.LAPSED_APP_*) |
| PGetAppCountPerCourse | `kifpk053.p_get_course_records` (cursor courseCode, applicationCount) |


--------------------------------------

---


## Part 4 – JLBSIS (Hibernate `hbm.xml`) (Ne.md:3444–3469)

# Part 4 – JLBSIS (Hibernate `hbm.xml`)

No JPA annotations are used; every mapping is a Hibernate `*.hbm.xml` under `src/conf/hibernate/...`. No mapping sets an explicit schema; raw SQL uses `XXSS.` once.

**Sub-projects with persistence:**
- `sis-data-service`: 25 mappings and the JDBC DAOs.
- `sis-coc-data-service`: 3 mappings and `JdbcCocDAO`.
- `sis-data-api` and `sis-coc-data-api`: interfaces and POJOs.
- `sis-csv-service`: CSV POJOs.

**Shared audit component `auditInfo`** (`uk.co.slc.hei.audit.interceptor.data.HAuditInfo`). "auditInfo" in any table below means these four columns:

| Field | Column | Type |
|---|---|---|
| createdBy | CREATED_BY | String(30) nn |
| createdDate | CREATED_DATE_TIME | Timestamp nn |
| lastUpdatedBy | LAST_UPDATED_BY | String(30) nn |
| lastUpdatedDate | LAST_UPDATED_DATE_TIME | Timestamp nn |

**Enum type prefixes:**
- `sis.rc.h` = `uk.co.slc.hei.sis.referencecodes.hibernate`
- `hei.rc.h` = `uk.co.slc.hei.referencecodes.hibernate`
- `rc.h` = `uk.co.slc.referencecodes.hibernate`

**Abbreviations:** nn = not-null.


## 4.1 sis-data-service (package `uk.co.slc.hei.sis.data.service.data`) (Ne.md:3470–3471)

## 4.1 sis-data-service (package `uk.co.slc.hei.sis.data.service.data`)


## DefaultTuitionPeriod → TUITION_PERIODS (Ne.md:3472–3539)

### DefaultTuitionPeriod → TUITION_PERIODS
- **Id:** ID (seq `XXSS_TUP_ID_SEQ`).
- **Version:** VERSION_NUMBER.
- **Extends:** AbstractTuitionPeriod.

| Field | Column | Type |
|---|---|---|
| attendanceNotificationId | ATTENDANCE_NOTIFICATION_ID | Long |
| startDate | START_DATE | Date nn |
| endDate | END_DATE | Date |
| feeRevision | FEE_REVISION_IND | hei.rc.h.FeeRevisionEnumType nn |
| classHandshake | CLASS_HANDSHAKE | Date |
| changeSource | CHANGE_SOURCE | hei.rc.h.ChangeSourceEnumType |
| cocId | COC_ID | Long |
| autoCocInd | AUTO_COC_IND | yes_no |
| specialLocation | ALTERNATIVE_LOCATION_TYPE | sis.rc.h.OtherLocationEnumType |
| fsnStatus | FSN_STATUS_TYPE | sis.rc.h.FSNStatusEnumType nn |
| studentStatus | STUDENT_STATUS_TYPE | String nn |
| transferCode | TRANSFER_CODE | sis.rc.h.TransferCodeEnumType |
| bursaryApproved | APPROVED_BURSARY_IND | yes_no nn |
| cocReminderStatus | COC_REMINDER_STATUS_TYPE | sis.rc.h.CocReminderStatusEnumType |
| studentUcasIdentifier | STUDENT_UCAS_ID | String |
| bankDetailsHeld | BANK_DETAILS_HELD_IND | yes_no nn |
| externalTransfer | EXTERNAL_TRANSFER_IND | yes_no nn |
| internalTransfer | INTERNAL_TRANSFER_IND | yes_no nn |
| reassessed | REASSESSED_IND | yes_no nn |
| paymentMade | PAYMENT_MADE_IND | yes_no nn |
| cohort | STUDENT_COHORT_CATEGORY_TYPE | hei.rc.h.StudentCohortCategoryTypeEnumType |
| modeOfStudy | MODE_OF_STUDY | rc.h.ModeOfStudyEnumType |
| currentAcademicYearCourseStartDate | CURRENT_ACY_COURSE_START_DATE | Date |
| changeEffectiveDate | CHANGE_EFFECTIVE_DATE | Date |
| loanAppliedFor | LOAN_APPLIED_FOR | String |
| heiCode | COL_HEI_CODE | String nn |
| customerId | CSR_ID | Long nn |
| slcCourseCode | CRS_COURSE_CODE | Long nn |
| hesAcademicYearId | HAY_ID | Long nn |
| courseYear | COURSE_YEAR | Long nn |
| tuitionFeeAmount | COURSE_FEE_AMOUNT | big_decimal nn |
| tuitionFeeLoan | TUITION_FEE_LOAN_AMOUNT | big_decimal nn |
| tuitionFeeGrant | TUITION_FEE_GRANT_AMOUNT | big_decimal nn |
| tuitionFeeLiability | TUITION_FEE_PAYABLE_AMOUNT | big_decimal nn |
| tuitionFeeWaiverAmount | FEE_WAIVER_PROCESSED_AMOUNT | big_decimal |
| maintenanceLoanAmount | MAINTENANCE_LOAN_AMOUNT | big_decimal |
| maintenanceGrantAmount | MAINTENANCE_GRANT_AMOUNT | big_decimal |
| workStage | WORK_STAGE | sis.rc.h.WorkStageEnumType |
| autoWithdrawalStatus | AUTO_WITHDRAWAL_STATUS | sis.rc.h.AutoWithdrawalStateEnumType |
| tuitionFeeLiabilitySchedule1/2/3 | TF_LIABILITY_SCHEDULE_IND_1/2/3 | String(1) |
| assessmentType | ELIG_ASSESSMENT_TYPE | sis.rc.h.AssessmentTypeEnumType |
| chtId | CHT_ID | Long |
| rejectedInd | REJECTED_IND | sis.rc.h.RejectedEnumType |
| multipleAcademicYear | MULTIPLE_ACADEMIC_YEAR | Integer |
| clientId | PNS_ID | Long |
| source | SOURCE | sis.rc.h.SourceEnumType |
| blockManualRegReason | BLOCK_MANUAL_REG_REASON_TYPE | sis.rc.h.BlockManualRegReasonEnumType |
| failureReason | FAILURE_REASON | String |
| deletedAsmntInd | DELETED_ASSESSMENT_IND | yes_no |
| withdrawReasonType | WITHDRAWAL_REASON_TYPE | sis.rc.h.WithdrawReasonTypeEnumType |
| compellingReasonInd | COMPELLING_REASON_IND | yes_no |
| handlerId | NOTIFICATION_HANDLER_TYPE | Integer |
| auditInfo | audit cols | HAuditInfo |
| application | TPA_ID | many-to-one DefaultTuitionPeriodApplication |
| defaultRegistrationConfirmationRequest | RCR_ID | many-to-one DefaultRegistrationConfirmationRequest |
| attendanceConfirmationRequests | key TUP_ID | bag → DefaultAttendanceConfirmationRequest |
| postGraduateConfirmationRequest | property-ref tuitionPeriod | one-to-one DefaultPostGraduateConfirmationRequest |
| histories | key TUP_ID | bag → DefaultTuitionPeriodHistory |
| edsCancellationLogs | key TUP_ID | bag → EdsCancellationLogs |
| tuitionFinanceDetail | property-ref tuitionPeriod | one-to-one DefaultTuitionFinanceDetail |


## DefaultTuitionPeriodHistory → TUITION_PERIOD_HISTORIES (Ne.md:3540–3552)

### DefaultTuitionPeriodHistory → TUITION_PERIOD_HISTORIES
- **Id:** ID (seq `XXSS_TPH_ID_SEQ`).
- **Version:** VERSION_NUMBER.
- **Columns:** every column of TUITION_PERIODS above, with the same names and types, plus auditInfo.

| Relation | Column | Type |
|---|---|---|
| application | TPA_ID | many-to-one DefaultTuitionPeriodApplication |
| defaultTuitionPeriod | TUP_ID | many-to-one DefaultTuitionPeriod |
| events | key TPH_ID | bag → DefaultEvent |
| defaultRegistrationConfirmationRequest | RCR_ID | many-to-one |
| tuitionFinanceHistory | property-ref tuitionPeriodHistory | one-to-one DefaultTuitionFinanceHistory |


## DefaultTuitionPeriodApplication → TUITION_PERIOD_APPLICATIONS (Ne.md:3553–3565)

### DefaultTuitionPeriodApplication → TUITION_PERIOD_APPLICATIONS
Id: ID (seq `XXSS_TPA_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| externalId | EXTERNAL_ID | Long |
| applicationSource | APPLICATION_SOURCE_TYPE | sis.rc.h.ApplicationSourceEnumType nn |
| sourceApplicationId | SOURCE_APPLICATION_REFERENCE | String |
| acyId | ACY_ID | Long |
| ruleConfigAttribute | RCA_ID | many-to-one RuleConfigAttributes |
| auditInfo | audit cols | HAuditInfo |
| tuitionPeriods | key TPA_ID | bag → DefaultTuitionPeriod |


## DefaultXxssApplications → XXSS_APPLICATIONS (Ne.md:3566–3577)

### DefaultXxssApplications → XXSS_APPLICATIONS
Id: ID (seq `XXSS_APN_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| applicationStatusType | APPLICATION_STATUS_TYPE | sis.rc.h.ApplicationStatusEnumType nn |
| changeEffectiveDate | CHANGE_EFFECTIVE_DATE | Date nn |
| colHeiCode | COL_HEI_CODE | String nn |
| csrId | CSR_ID | Long nn |
| sourceApplicationId | SOURCE_APPLICATION_REFERENCE | String nn |
| courseEndDate | COURSE_END_DATE | Date nn |


## DefaultTuitionFinanceDetail → TUITION_FINANCE_DETAILS (Ne.md:3578–3588)

### DefaultTuitionFinanceDetail → TUITION_FINANCE_DETAILS
Id: ID (seq `XXSS_TFD_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| currentAyCourseCredits | CURRENT_AY_COURSE_CREDITS | Integer |
| childCareGrantInd | CHILD_CARE_GRANT_IND | yes_no |
| tuitionPeriod | TUP_ID | many-to-one DefaultTuitionPeriod nn |
| tuitionFinanceHistories | key TFD_ID | bag → DefaultTuitionFinanceHistory |
| auditInfo | audit cols | HAuditInfo |


## DefaultTuitionFinanceHistory → TUITION_FINANCE_HISTORIES (Ne.md:3589–3599)

### DefaultTuitionFinanceHistory → TUITION_FINANCE_HISTORIES
Id: ID (seq `XXSS_TFS_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| currentAyCourseCredits | CURRENT_AY_COURSE_CREDITS | Integer |
| childCareGrantInd | CHILD_CARE_GRANT_IND | yes_no |
| tuitionFinanceDetail | TFD_ID | many-to-one nn |
| tuitionPeriodHistory | TPH_ID | many-to-one nn |
| auditInfo | audit cols | HAuditInfo |


## RuleConfigAttributes (`ruleconfig.api.data`) → RULE_CONFIG_ATTRIBUTES (Ne.md:3600–3610)

### RuleConfigAttributes (`ruleconfig.api.data`) → RULE_CONFIG_ATTRIBUTES
Id: ID (seq `XXSS_RCA_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| studentCohort | STUDENT_COHORT_CATEGORY_TYPE | BigDecimal |
| modeOfStudy | MODE_OF_STUDY | String |
| source | SOURCE | String |
| domicileType | DOMICILE_TYPE | Integer |
| auditInfo | audit cols | HAuditInfo |


## PublisherConfigAttributes (`ruleconfig.api.data`) → PUBLISHER_CONFIG_ATTRIBUTES (Ne.md:3611–3622)

### PublisherConfigAttributes (`ruleconfig.api.data`) → PUBLISHER_CONFIG_ATTRIBUTES
Id: ID (seq `XXSS_PCA_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| rcaId | RCA_ID | Long nn |
| acyId | ACY_ID | Long |
| messageType | MESSAGE_TYPE | String |
| configurationMode | CONFIGURATION_MODE | String |
| configClassInd | CONFIG_CLASS_IND | String |
| auditInfo | audit cols | HAuditInfo |


## DefaultRegistrationConfirmationRequest → REGISTRATION_CONF_REQUESTS (Ne.md:3623–3643)

### DefaultRegistrationConfirmationRequest → REGISTRATION_CONF_REQUESTS
- **Id:** ID (seq `XXSS_RCR_ID_SEQ`).
- **Version:** VERSION_NUMBER.

| Field | Column | Type |
|---|---|---|
| hesAcademicYearId | HAY_ID | Long nn |
| heiCode | COL_HEI_CODE | String nn |
| customerId | CSR_ID | Long nn |
| stateEnum | REGISTRATION_STATUS_TYPE | sis.rc.h.RegistrationStateTypeEnumType nn |
| worklistDate | WORKLIST_DATE | Date nn |
| confirmationDate | CONFIRMATION_DATE | Date |
| code | REGISTRATION_CODE_TYPE | sis.rc.h.RegistrationCodeTypeEnumType |
| courseCode | CRS_COURSE_CODE | Long |
| manualRegistration | MANUAL_REGISTRATION_INDICATOR | yes_no |
| creditCocInd | CREDIT_COC_IND | String nn |
| guidReferenceId | GUID_REFERENCE | String (insert=false) |
| auditInfo | audit cols | HAuditInfo |
| histories | key RCR_ID | bag → DefaultRegistrationConfirmationRequestHistory |
| tuitionPeriods | key RCR_ID | bag → DefaultTuitionPeriod |


## DefaultRegistrationConfirmationRequestHistory → REGISTRATION_CONF_REQ_HISTS (Ne.md:3644–3649)

### DefaultRegistrationConfirmationRequestHistory → REGISTRATION_CONF_REQ_HISTS
- **Id:** ID (seq `XXSS_RCH_ID_SEQ`).
- **Version:** VERSION_NUMBER.
- **Columns:** same as REGISTRATION_CONF_REQUESTS but without GUID_REFERENCE: REGISTRATION_STATUS_TYPE, WORKLIST_DATE, CONFIRMATION_DATE, REGISTRATION_CODE_TYPE, CRS_COURSE_CODE, MANUAL_REGISTRATION_INDICATOR, HAY_ID, COL_HEI_CODE, CSR_ID, CREDIT_COC_IND, plus auditInfo.
- **Relations:** defaultRegistrationConfirmation (RCR_ID, many-to-one) and events (key RCH_ID, bag → DefaultEvent).


## DefaultPostGraduateConfirmationRequest → PGL_CONFIRMATION_REQUESTS (Ne.md:3650–3667)

### DefaultPostGraduateConfirmationRequest → PGL_CONFIRMATION_REQUESTS
- **Id:** ID (seq `XXSS_PCR_ID_SEQ`).
- **Version:** VERSION_NUMBER.

| Field | Column | Type |
|---|---|---|
| tuitionPeriod | TUP_ID | many-to-one nn |
| heiCode | COL_HEI_CODE | String nn |
| hesAcademicYearId | HAY_ID | Long nn |
| period | PERIOD | Integer nn |
| stateEnum | REGISTRATION_STATUS_TYPE | RegistrationStateTypeEnumType nn |
| worklistDate | WORKLIST_DATE | Date nn |
| confirmationDate | CONFIRMATION_DATE | Date |
| code | REGISTRATION_CODE_TYPE | RegistrationCodeTypeEnumType |
| guidReference | GUID_REFERENCE | String (insert=false) |
| auditInfo | audit cols | HAuditInfo |
| histories | key PCR_ID | bag → DefaultPostGraduateConfirmationRequestHistory |


## DefaultPostGraduateConfirmationRequestHistory → PGL_CONFIRMATION_REQUEST_HISTS (immutable) (Ne.md:3668–3673)

### DefaultPostGraduateConfirmationRequestHistory → PGL_CONFIRMATION_REQUEST_HISTS (immutable)
- **Id:** ID (seq `XXSS_PCH_ID_SEQ`).
- **Version:** VERSION_NUMBER.
- **Columns:** PERIOD, REGISTRATION_STATUS_TYPE, WORKLIST_DATE, CONFIRMATION_DATE, REGISTRATION_CODE_TYPE, COL_HEI_CODE, HAY_ID, plus auditInfo.
- **Relations:** tuitionPeriod (TUP_ID), postGraduateConfirmationRequest (PCR_ID), events (key PCH_ID, bag → DefaultEvent).


## DefaultAttendanceConfirmationRequest → ATTENDANCE_CONF_REQUESTS (Ne.md:3674–3696)

### DefaultAttendanceConfirmationRequest → ATTENDANCE_CONF_REQUESTS
- **Id:** ID (seq `XXSS_ACQ_ID_SEQ`).
- **Version:** VERSION_NUMBER.

| Field | Column | Type |
|---|---|---|
| period | PERIOD | Integer nn |
| stateEnum | ATTENDANCE_STATUS_TYPE | sis.rc.h.AttendanceStateEnumType nn |
| worklistDate | WORKLIST_DATE | Date |
| confirmationDate | CONFIRMATION_DATE | Date |
| code | ATTENDANCE_CODE_TYPE | sis.rc.h.AttendanceCodeTypeEnumType |
| classCode | CLASS_ATTENDANCE_CODE_TYPE | AttendanceCodeTypeEnumType |
| saved | SAVED_IND | yes_no nn |
| savedAndReassessed | SAVED_REASSESSED_IND | yes_no nn |
| previouslyConfirmed | PREVIOUSLY_CONFIRMED_IND | yes_no nn |
| previouslySuspended | PREVIOUSLY_SUSPENDED_IND | yes_no nn |
| clawbackRequiredFrom | CLAWBACK_REQUIRED_FROM | Timestamp |
| confirmationType | CONFIRMATION_TYPE | sis.rc.h.ImportFileTypeEnumType |
| guidReferenceId | GUID_REFERENCE | String (insert=false) |
| auditInfo | audit cols | HAuditInfo |
| defaultTuitionPeriod | TUP_ID | many-to-one nn |
| histories | key ACQ_ID | bag → DefaultAttendanceConfirmationRequestHistory |


## DefaultAttendanceConfirmationRequestHistory → ATTENDANCE_CONF_REQUEST_HISTS (immutable) (Ne.md:3697–3702)

### DefaultAttendanceConfirmationRequestHistory → ATTENDANCE_CONF_REQUEST_HISTS (immutable)
- **Id:** ID (seq `XXSS_ACH_ID_SEQ`).
- **Version:** VERSION_NUMBER.
- **Columns:** same as ATTENDANCE_CONF_REQUESTS except GUID_REFERENCE, plus auditInfo.
- **Relations:** defaultTuitionPeriod (TUP_ID), defaultAttendanceConfirmationRequest (ACQ_ID), eventAttendanceConfirmationRequestHistories (key ACH_ID).


## DefaultEvent → EVENTS (immutable) (Ne.md:3703–3714)

### DefaultEvent → EVENTS (immutable)
Id: ID (seq `XXSS_EVE_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| eventTypeEnum | EVENT_TYPE | sis.rc.h.EventTypeEnumType |
| auditInfo | audit cols | HAuditInfo |
| tuitionPeriodHistory | TPH_ID | many-to-one |
| defaultRegistrationConfirmationHistory | RCH_ID | many-to-one |
| defaultPostGraduateConfirmationHistory | PCH_ID | many-to-one |
| eventAttendanceConfirmationRequestHistories | key EVE_ID | bag |


## DefaultEventAttendanceConfirmationRequestHistory → EVENT_CONF_REQUEST_HISTS (immutable) (Ne.md:3715–3719)

### DefaultEventAttendanceConfirmationRequestHistory → EVENT_CONF_REQUEST_HISTS (immutable)
- **Id:** ID (seq `XXSS_ECH_ID_SEQ`).
- **Columns:** auditInfo only.
- **Relations:** defaultEvent (EVE_ID, nn) and defaultAttendanceConfirmationRequestHistory (ACH_ID, nn).


## EdsCancellationLogs → EDS_CANCELLATION_LOGS (Ne.md:3720–3730)

### EdsCancellationLogs → EDS_CANCELLATION_LOGS
Id: ID (seq `XXSS_ECL_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| tupId | TUP_ID | Long nn |
| edsPnsId | EDS_PNS_ID | Long |
| workStage | WORK_STAGE | sis.rc.h.WorkStageEnumType |
| auditInfo | audit cols | HAuditInfo |
| tuitionPeriod | TUP_ID | many-to-one (read-only) |


## PglRejection (`api.data`) → PAYMENT_REJECTION_NOTIFS (Ne.md:3731–3747)

### PglRejection (`api.data`) → PAYMENT_REJECTION_NOTIFS
Id: ID (seq `XXSS_PRN_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| pnsId | PNS_ID | Long nn |
| csrId | CSR_ID | Long nn |
| academicYearId | ACY_ID | Long |
| heiCode | COL_HEI_CODE | String |
| courseCode | CRS_COURSE_CODE | String |
| courseYear | COURSE_YEAR | Integer |
| paymentNotificationType | PAYMENT_NOTIFICATION_TYPE | String |
| failureReason | FAILURE_REASON | String |
| failureDateTime | FAILURE_DATE_TIME | Date |
| processedInd | PROCESSED_IND | yes_no |
| auditInfo | audit cols | HAuditInfo |


## BulkFileSubmissionPOJO → BULK_FILE_SUBMISSIONS (Ne.md:3748–3767)

### BulkFileSubmissionPOJO → BULK_FILE_SUBMISSIONS
- **Id:** ID (seq `BFS_ID_SEQ`).
- **Named query:** `BulkFileSubmission.expired.by.todaysDate`.

| Field | Column | Type |
|---|---|---|
| filename | FILENAME | String nn |
| heiCode | HEI_CODE | String nn |
| checksum | CHECKSUM | String |
| numberOfRecords | NUMBER_OF_RECORDS | Integer nn |
| lastRecordProcessed | LAST_RECORD_PROCESSED | Integer nn |
| numberOfBlanks | NUMBER_BLANKS | Integer |
| numberOfInvalid | NUMBER_INVALID | Integer |
| importFileType | IMPORT_FILE_TYPE | sis.rc.h.ImportFileTypeEnumType nn |
| importStatusType | IMPORT_STATUS_TYPE | sis.rc.h.ImportStatusTypeEnumType nn |
| academicYear | ACY_ID | Long |
| fileSubmitted | FILE_SUBMITTED_IND | sis.rc.h.ImportTypeEnumType nn |
| auditInfo | audit cols | HAuditInfo |
| uploadFileRecords | key BFS_ID | bag → DefaultFileImportRecord |


## DefaultFileImportRecord → STAGE_FILE_UPLOADS (Ne.md:3768–3778)

### DefaultFileImportRecord → STAGE_FILE_UPLOADS
- **Id:** ID (seq `xxss_sfu_id_seq`).
- **Named queries:** `FileImportRecord.getHeader`, `getContent`, `getFooter`.

| Field | Column | Type |
|---|---|---|
| sequenceNumber | RECORD_SEQUENCE_NUMBER | Long nn |
| content | RECORD_DETAILS | String |
| bulkFileSubmission | BFS_ID | many-to-one nn |
| auditInfo | audit cols | HAuditInfo |


## ImportResultPOJO → IMPORT_RESULTS (Ne.md:3779–3790)

### ImportResultPOJO → IMPORT_RESULTS
Id: ID (seq `XXSS_IMR_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| numberSuccessful | NUMBER_SUCCESSFUL | Integer nn |
| numberFailed | NUMBER_FAILED | Integer nn |
| bulkFileSubmissionId | BFS_ID | Long nn |
| attendanceCodeType | ATTENDANCE_CODE_TYPE | AttendanceCodeTypeEnumType |
| cocCategoryType | COC_CATEGORY_TYPE | sis.rc.h.CocCategoryTypeEnumType |
| auditInfo | audit cols | HAuditInfo |


## ImportFailureDetailPOJO → IMPORT_FAILURE_DETAILS (Ne.md:3791–3802)

### ImportFailureDetailPOJO → IMPORT_FAILURE_DETAILS
Id: ID (seq `XXSS_IFD_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| fileRecordNumber | FILE_RECORD_NUMBER | Long |
| importFailureReasonType | IMPORT_FAILURE_REASON_TYPE | sis.rc.h.ImportFailureReasonTypeEnumType nn |
| bulkFileSubmissionId | BFS_ID | Long nn |
| importFailureType | IMPORT_FAILURE_TYPE | sis.rc.h.ImportFailureTypeEnumType nn |
| studentSupportNumber | STUDENT_SUPPORT_NUMBER | String |
| auditInfo | audit cols | HAuditInfo |


## ImportCocFailureDetailPOJO → IMPORT_COC_FAILURE_DETAILS (Ne.md:3803–3814)

### ImportCocFailureDetailPOJO → IMPORT_COC_FAILURE_DETAILS
Id: ID (seq `XXSS_ICF_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| bulkFileSubmissionId | BFS_ID | Long nn |
| fileRecordNumber | FILE_RECORD_NUMBER | Long |
| studentSupportNumber | STUDENT_SUPPORT_NUMBER | String |
| importFailureType | IMPORT_FAILURE_TYPE | ImportFailureTypeEnumType nn |
| importCocFailureReason | IMPORT_COC_FAILURE_REASON | String nn |
| auditInfo | audit cols | HAuditInfo |


## HebssNotificationStagesPojo → HEBSS_NOTIFICATION_STGS (Ne.md:3815–3828)

### HebssNotificationStagesPojo → HEBSS_NOTIFICATION_STGS
Id: ID (seq `XXSS_HNS_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| acqId | ACQ_ID | Long |
| rcrId | RCR_ID | Long |
| classPublishStatusType | CLASS_PUBLISH_STATUS_TYPE | Integer |
| sendToHebssInd | SENT_TO_HEBSS_IND | String |
| sourceApplicationId | SOURCE_APPLICATION_REFERENCE | String nn |
| confirmationDate | CONFIRMATION_DATE | Date |
| attendanceCodeType | ATTENDANCE_CODE_TYPE | AttendanceCodeTypeEnumType |
| auditInfo | audit cols | HAuditInfo |


## AdditionalCustomerDetailPOJO → ADDITIONAL_CUSTOMER_DETAILS (Ne.md:3829–3848)

### AdditionalCustomerDetailPOJO → ADDITIONAL_CUSTOMER_DETAILS
- **Composite id:** `AdditionalCustomerDetailPK` (CSR_ID Long, ACY_ID Long).
- **Named query:** `AdditionalCustomerDetail.forCustomerId`.

| Field | Column | Type |
|---|---|---|
| numberOfSponsors | NUMBER_OF_SPONSORS | Long |
| sponsorOneConsent | SPONSOR1_CONSENT_IND | String nn |
| sponsorTwoConsent | SPONSOR2_CONSENT_IND | String nn |
| studentBursaryConsent | STUDENT_CONSENT_IND | String nn |
| ccgSupportIndicator | CCG_SUPPORT_IND | String nn |
| nhsBursaryIndicator | NHS_BURSARY_IND | String nn |
| cohortYear | COHORT_YEAR | Long |
| leaCode | AWA_LEA_CODE | String nn |
| erasmusSchemeFlag | ERASMUS_SCHEME_FLAG | String(1) |
| abroadPlacementFlag | ABROAD_PLACEMENT_FLAG | String(1) |
| abroadPaidPlacementFlag | ABROAD_PAID_PLACEMENT_FLAG | String(1) |
| auditInfo | audit cols | HAuditInfo |
| awardAuthority | AWA_LEA_CODE | many-to-one `commonstakeholder.api.data.AwardAuthority` |


## 4.2 sis-coc-data-service (package `uk.co.slc.hei.sis.coc.data.service.data`) (Ne.md:3849–3850)

## 4.2 sis-coc-data-service (package `uk.co.slc.hei.sis.coc.data.service.data`)


## CocPOJO → COCS (single-table inheritance) (Ne.md:3851–3911)

### CocPOJO → COCS (single-table inheritance)
- **Discriminator:** COC_CATEGORY_TYPE.
- **Id:** ID (seq `COC_ID_SEQ`).
- **Version:** VERSION_NUMBER.

| Field | Column | Type |
|---|---|---|
| heiCode | COL_HEI_CODE | String nn |
| cocCategoryType | COC_CATEGORY_TYPE | CocCategoryTypeEnumType (read-only) |
| cocStatusType | COC_STATUS_TYPE | sis.rc.h.CocStatusTypeEnumType nn |
| studentSupportNumber | STUDENT_SUPPORT_NUMBER | String nn |
| academicApproval | ACADEMIC_APPROVAL | yes_no |
| academicYear | ACADEMIC_YEAR | Integer |
| bulkFileSubmissionId | BFS_ID | Long |
| contactId | HCO_ID | Long |
| courseCode | CRS_COURSE_CODE | Long |
| courseEndDate | COURSE_END_DATE | Date |
| courseName | COURSE_NAME | String |
| courseYearType | COURSE_YEAR_TYPE | sis.rc.h.CocCourseYearTypeEnumType |
| customerId | CSR_ID | Long |
| dateOfBirth | DATE_OF_BIRTH | calendar_date |
| dateLastAttended | DATE_LAST_ATTENDED | Date |
| dateStudentLeftCourse | DATE_STUDENT_LEFT_COURSE | Date |
| forenames | FORENAMES | String |
| furtherExplanation | FURTHER_EXPLANATION | String |
| hesAcademicYearId | HAY_ID | Long |
| methodOfAttendance | METHOD_OF_ATTENDANCE | hei.rc.h.AttendanceMethodTypeEnumType |
| otherCourseYear | OTHER_COURSE_YEAR | String |
| surname | SURNAME | String |
| tuitionFeeAmount | TUITION_FEE_AMOUNT | Double |
| tuitionLiabilityAmount | TUITION_LIABILITY_AMOUNT | Double |
| ucasCourseCode | UCAS_COURSE_CODE | String |
| attendanceCode | ATTENDANCE_CODE_TYPE | AttendanceCodeTypeEnumType |
| modeOfStudy | MODE_OF_STUDY | rc.h.ModeOfStudyEnumType |
| levelOfFunding | LEVEL_OF_FUNDING_TYPE | sis.rc.h.LevelOfStudyEnumType nn |
| courseCodeFrom | CRS_COURSE_CODE_ON_SUBMISSION | Long |
| tpAppId | TPA_ID | Long |
| currentAyCourseCredits | CURRENT_AY_COURSE_CREDITS | Long |
| cocStageDetails | key COC_ID | bag → CocStageDetails |
| auditInfo | audit cols | HAuditInfo |

**COC subclasses** (discriminator value, extra columns):

| Subclass (value) | Extra fields → columns |
|---|---|
| WithdrawalCocPOJO (1) | withdrawReasonType → WITHDRAWAL_REASON_TYPE |
| SuspensionCocPOJO (2) | dateOfReturn → DATE_OF_RETURN; originalDurationOfCourse → ORIGINAL_DURATION_OF_COURSE; suspensionReasonType → SUSPENSION_REASON_TYPE |
| ResumptionCocPOJO (3) | dateOfResumption → DATE_OF_RESUMPTION; resumptionConditions → RESUMPTION_CONDITIONS; resumptionConditionType → RESUMPTION_CONDITION_TYPE; resumptionPlacementDetails → RESUMPTION_PLACEMENT_DETAILS |
| RepetitionCocPOJO (4) | mainReasonsToRepeat → MAIN_REASONS_TO_REPEAT; periodCourseToRepeat → PERIOD_COURSE_TO_REPEAT; repeatFromDate → REPEAT_FROM_DATE; repeatToDate → REPEAT_TO_DATE |
| TransferCocPOJO (5) | approvalDate → APPROVAL_DATE; firstDateOfAttendance → FIRST_DATE_OF_ATTENDANCE; heiCodeTransferInto → COL_HEI_CODE_TRANSFER_INTO; transferType → TRANSFER_TYPE; heiName → HEI_NAME; heiUcasCode → HEI_UCAS_CODE; importHeiCode → IMPORT_HEI_CODE; ChangeReason → CHANGE_REASON_TYPE; currentAcyCourseStartDate → CURRENT_ACY_COURSE_START_DATE; transferReason → TRANSFER_REASON_TYPE |
| FeeChangeCocPOJO (6) | ChangeReason → CHANGE_REASON_TYPE |
| ReminderCocPOJO (7) | – |
| FeeWaiverCocPOJO (8) | feeWaiverApprovedAmount → FEE_WAIVER_APPROVED_AMOUNT |
| CourseTransferCocPOJO (10) | Same as TransferCoc, without transferReason |
| ProviderTransferCocPOJO (11) | Same as CourseTransferCoc |
| CreditCocPOJO (12) | ChangeReason → CHANGE_REASON_TYPE |
| CrossAYTransferCocPOJO (13) | Same as CourseTransferCoc |
| ThesisSubmissionCocPOJO (14) | thesisSubmissionDate → THESIS_SUBMISSION_DATE |

`CreditChangeCocPOJO.java` exists but is not mapped.


## CocStageDetails → COC_STAGE_DETAILS (Ne.md:3912–3938)

### CocStageDetails → COC_STAGE_DETAILS
Id: ID (seq `XXSS_CSD_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| customerId | CSR_ID | Long |
| academicYearId | ACY_ID | Long nn |
| heiCode | COL_HEI_CODE | String nn |
| courseCode | CRS_COURSE_CODE | Long |
| courseYear | COURSE_YEAR | Long |
| deletedCocInd | DELETED_COC_IND | yes_no |
| changeSource | CHANGE_SOURCE | hei.rc.h.ChangeSourceEnumType |
| cocCreated | COC_CREATED_DATE_TIME | Timestamp |
| cocUpdated | COC_UPDATED_DATE_TIME | Timestamp |
| assessmentHistoryCreated | ASSESSMENT_HIST_CREATED_DATE | Timestamp |
| assessmentHistoryUpdated | ASSESSMENT_HIST_UPDATED_DATE | Timestamp nn |
| changeType | CHT_ID | sis.rc.h.ChangeTypeEnumType nn |
| autoCocInd | AUTO_COC_IND | yes_no |
| reasonCode | REASON_CODE | String |
| eligabilityAssessmentType | ELIG_ASSESSMENT_TYPE | AssessmentTypeEnumType |
| workstage | WORK_STAGE | Integer nn |
| sisCocFailureReason | COC_FAILURE_REASON_CODE | Integer |
| cocStatusType | COC_STATUS_TYPE | Integer nn |
| tpAppId | TPA_ID | Long |
| coc | COC_ID | many-to-one CocPOJO nn |
| auditInfo | audit cols | HAuditInfo |


## CocNotificationMessage → COC_NOTIFICATIONS (Ne.md:3939–3952)

### CocNotificationMessage → COC_NOTIFICATIONS
Id: ID (seq `XXSS_CCN_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| cocCategoryType | COC_CATEGORY_TYPE | CocCategoryTypeEnumType nn |
| customerId | CSR_ID | Long nn |
| academicYearId | ACY_ID | Integer nn |
| heiCode | COL_HEI_CODE | String nn |
| slcCourseCode | SLC_COURSE_CODE | Long nn |
| courseYear | COURSE_YEAR | Integer nn |
| feeWaiverApprovedAmount | FEE_WAIVER_APPROVED_AMOUNT | Double |
| auditInfo | audit cols | HAuditInfo |


## 4.3 JLBSIS unmapped POJOs (Ne.md:3953–3978)

## 4.3 JLBSIS unmapped POJOs
| Class (project) | Fields |
|---|---|
| ImportResultCocPojo (sis-data-service) | date, time, uploadedUser, fileName String; totalRecords, successRecordsCount, totalfailRecordsCount int; importStatusType String; daysLeft int; bfsId long |
| AdditionalCustomerDetailPK | customerId Long, academicYearId Long |
| NiaOutcome (enum) | PROCESS, UNLOCK, REJECT, DESCHEDULE, RESCHEDULE_NEVER_ATTENDED, RESCHEDULE_NOT_REGISTERED |
| ScheduledPaymentDetail (sis-data-api) | scheduledPaymentDate Date, tuitionFeeType PaymentInstalmentSupportTypeEnum, amountToBePaid BigDecimal |
| TuitionFeeForecast (sis-data-api) | academicYearQuarter AttendanceSeasonTypeEnum, liabilityPeriod PaymentInstalmentTypeEnum, studentsUnconfirmed Long, studentsUnconfirmedValue BigDecimal, feePayingStudentsConfirmed Long, feePayingStudentsConfirmedValue BigDecimal, nonFeePayingStudentsConfirmed Long, nonFeePayingStudentsConfirmedValue BigDecimal, studentsPaid Long, studentsPaidValue BigDecimal, interimPaymentsStudents Long, interimPaymentsStudentsValue BigDecimal |
| TuitionFeeBalance (sis-data-api) | id Long, heiCode String, academicYear Long, modeOfStudy String, reportingDate Date, expectedFees Double, expectedStudents Long, feesPaid, feesScheduled, feesUnconfirmedStudents, feesAwaitingReassessment, feesBlocked Double, temporaryCourses Long, recoveries Double, levelOfFunding String |
| RemittanceRecord | student Student, course Course |
| RemittanceRecord.Student | ssn, forename, surname String, dateOfBirth Date, ucasNumber String, yearOfCourse Integer, tuitionFeeAmount Double, revised Boolean |
| RemittanceRecord.Course | startDate Date, startPeriod, slcCourseCode, shortName, description String |
| BlockedPaymentsReportDetailRow | studentSupportNumber, fullName String, registrationConfirmedDate Date, tuitionPeriodId Long |
| HandlerConfigDetail | handlerId Integer, alwaysRunInd String |
| PglRejectionMessage | data Map (pns_id, csr_id, acad_year, hei_code, course_code, course_year, payment_notification_type, failure_reason, failure_date_time) |
| ReportDataPOJO (sis-coc-data-service) | categoryType CocCategoryTypeEnum, totalNumber Integer, percentage Integer, dbCategoryType CocCategoryType |
| ReportTotalSubmissionDataPOJO | label String, reportData Map, totalSubmissions Long |

*Not read (fields unknown):*
- RegistrationFilterResultRowImpl, CombinedFilterResultRowImpl, AttendanceFilterResultRow*Impl, AttendanceNotificationMessage, BursaryApprovalAttendanceNotificationAdaptor.
- `sis.data`: Registration, RegistrationPublish, RegAndAtnConfirmStatusRequest, AttendanceConfirmation.
- `api.data`: TuitionFeeForecastScheduledPayment, RemittanceReport*, RejectionNotification, OutstandingOnlineDeclarationReportRow, CancellationAttendanceNotification, BulkFileUpload, BlockedPaymentsReportSummaryRow.
- `ruleconfig.api.data`: StudyDomicileData, RuleConfigKey, RuleConfigAwardAuthority.
- CocWorklistFilterResultRowImpl.
- `sis-csv-service` row/header classes.


## 4.4 JLBSIS tables referenced only in raw SQL (Ne.md:3979–4004)

## 4.4 JLBSIS tables referenced only in raw SQL

This list is based on search results capped at 20 hits each, so it may be incomplete.

| Table / object | Used in |
|---|---|
| HES_ACADEMIC_YEARS | JdbcCocDAO, JdbcSisRegistrationDAO |
| XXSL_COLLEGES | JdbcCocDAO, JdbcSisRegistrationDAO, JdbcSisDAO, JdbcSisCombinedDAO, JdbcReportDao |
| XXSL_CUSTOMERS | JdbcCocDAO, JdbcSisRegistrationDAO, JdbcSisDAO |
| XXSL_COURSES, XXSL_COURSE_YEARS, XXSL_COURSE_LOCATIONS, XXSL_HEI_LOCNS, XXSL_FRANCHISE_ADDRESSES | JdbcCocDAO, JdbcSisDAO, JdbcSisRegistrationDAO, JdbcSisCombinedDAO |
| XXSL_REF_CODES | SpringCocReportService, JdbcSisDAO |
| XXSL_PROTOCOL_PROPERTIES, XXSL_HEI_TUITION_FEE_TYPES | JdbcSisDAO |
| XXSL_AWARD_AUTHORITIES | JdbcLeaCodeSisDao, JdbcSisStudentDetailsForExportPsc |
| XXSN_APP_NOTIFICATIONS, XXSN_APP_CONFIRMATIONS, XXSN_APP_COURSES | JdbcSisDAO |
| ATTENDANCE_NOTIFICATIONS | JdbcSisDAO |
| RCA_GROUPS, RCA_GROUP_MAPPINGS, RCA_QUALIFICATIONS | JdbcSisDAO, JdbcComponentConfigDAO, JdbcSisCombinedDAO |
| RCA_REG_CONFIGS, XXSS_COMPONENT_CONFIGS, HEI_PAGES, COMPONENTS, COMPONENT_TYPES, PAGE_COMPONENT_MAPPINGS, PAGE_COMPONENT_PROPERTIES, PAGE_COMPONENT_GROUP_MAPPINGS | JdbcComponentConfigDAO, SpringCocReportService |
| RGP_COC_CATEGORY_MAPPINGS | SpringCocReportService |
| APPROVED_AWAITING_SIG_APPS | JdbcReportDao |
| `kifpk041.f_coc_submission_rep`, `kifpk041.f_hep_coc_submission_rep` (table functions) | SpringCocReportService |


-----------------------------------

---


## Part 5 – JLBHEI (Hibernate `hbm.xml`) (Ne.md:4005–4010)

# Part 5 – JLBHEI (Hibernate `hbm.xml`)

There are 37 sub-projects. 7 contain Hibernate mappings (33 mapping files, one table each) and 3 call stored procedures. There are no JPA entities. Only one mapping sets a schema: `XXSL.XXSL_ACADEMIC_YEARS`.

**Shared `auditInfo`** (`uk.co.slc.hei.audit.interceptor.data.HAuditInfo`, which extends `VAuditInfo`) maps four columns: CREATED_BY, CREATED_DATE_TIME, LAST_UPDATED_BY and LAST_UPDATED_DATE_TIME.


## 5.1 hei-bank-details-hibernate-service (`uk.co.slc.hei.heibankdetails.service.data`) (Ne.md:4011–4012)

## 5.1 hei-bank-details-hibernate-service (`uk.co.slc.hei.heibankdetails.service.data`)


## ServiceBankAccountUsageTypePOJO → SERVICE_BANK_USAGES (Ne.md:4013–4022)

### ServiceBankAccountUsageTypePOJO → SERVICE_BANK_USAGES
- **Id:** ID (assigned).
- **Named query:** `ServiceBankAccountUsageType.getAll`.

| Field | Column | Type |
|---|---|---|
| serviceTypeEnum | SERVICE_TYPE | ServiceTypeEnumType |
| bankAccountUsageTypeEnum | BANK_ACCOUNT_USAGE_TYPE | BankAccountUsageTypeEnumType |
| createdBy / createdOn / updatedBy / updatedOn | CREATED_BY / CREATED_DATE_TIME / LAST_UPDATED_BY / LAST_UPDATED_DATE_TIME | string / date |


## HEIBankUsagePOJO → HEI_SERVICE_BANK_ACCOUNTS (Ne.md:4023–4034)

### HEIBankUsagePOJO → HEI_SERVICE_BANK_ACCOUNTS
- **Id:** ID (seq `HSB_ID_SEQ`).
- **Named queries:** `HEIBankUsages.searchByHeiBankDetailsId`, `searchForOverlap`.

| Field | Column | Type |
|---|---|---|
| heiBankDetailsPOJO | HBA_ID | many-to-one HEIBankDetailsPOJO |
| serviceBankAccountUsageTypePOJO | SBU_ID | many-to-one |
| effectiveFrom | EFFECTIVE_FROM_DATE | date |
| effectiveTo | EFFECTIVE_TO_DATE | date |
| auditInfo | audit cols | HAuditInfo |


## HEIBankDetailsPOJO → HEI_BANK_ACCOUNTS (Ne.md:4035–4049)

### HEIBankDetailsPOJO → HEI_BANK_ACCOUNTS
- **Id:** ID (seq `HBA_ID_SEQ`).
- **Named queries:** `HEIBankDetails.searchByHEICode`, `fetchById`.

| Field | Column | Type |
|---|---|---|
| accountHolderName | ACCOUNT_HOLDERNAME | string |
| sortCode | FBR_SORT_CODE | long |
| accountNumber | ACCOUNT_NUMBER | long |
| rollNumber | ROLL_NUMBER | string |
| accountCodeType | ACCOUNT_CODE_TYPE | string |
| HEICode | COL_HEI_CODE | string |
| auditInfo | audit cols | HAuditInfo |
| bankUsages | key HBA_ID | set → HEIBankUsagePOJO |


## 5.2 hei-common-stakeholder-api (`uk.co.slc.hei.commonstakeholder.api.data`) (Ne.md:4050–4051)

## 5.2 hei-common-stakeholder-api (`uk.co.slc.hei.commonstakeholder.api.data`)


## Qualification → QUALIFICATIONS (Ne.md:4052–4061)

### Qualification → QUALIFICATIONS
Id: ID (seq `QUA_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| code | QUALIFICATION_CODE | String(8) |
| description | DESCRIPTION | String(30) |
| activeInd | ACTIVE_IND | yes_no |
| auditInfo | audit cols | HAuditInfo |


## HeiLocation → XXSL_HEI_LOCNS (Ne.md:4062–4089)

### HeiLocation → XXSL_HEI_LOCNS
Id: LOCN_ID (assigned).

| Field | Column | Type |
|---|---|---|
| startDate | START_DATE | Timestamp nn |
| name | NAME | String(32) nn |
| locnType | LOCN_TYPE | String(1) nn |
| createUser | CREATE_USER | String(30) nn |
| createDate | CREATE_DATE | Timestamp nn |
| addrLine1..addrLine5 | ADDR_LINE_1..ADDR_LINE_5 | String(60) (line 1 nn) |
| activeInd | ACTIVE_IND | String(1) nn |
| updateUser | UPDATE_USER | String(30) |
| updateDate | UPDATE_DATE | Timestamp |
| postCode | POST_CODE | String(8) |
| phoneNum | PHONE_NUM | String(20) |
| faxNum | FAX_NUM | String(60) |
| endDate | END_DATE | Timestamp |
| emailAddress | EMAIL_ADDRESS | String(60) |
| colHeiCode | COL_HEI_CODE | String(4) nn |
| deliveryPointSuffix | DELIVERY_POINT_SUFFIX | String(2) |
| rm4sccCheckChar | RM4SCC_CHECK_CHAR | String(1) |
| mailsortCode | MAILSORT_CODE | String(5) |
| pafMatched | PAF_MATCHED | String(1) |
| campusId | CAMPUS_ID | String(2) |
| bacsFirstParticipation | BACS_FIRST_PARTICIPATION | String(1) |
| rescheduleInd | RESCHEDULE_IND | String(1) |


## FranchiseAddress → XXSL_FRANCHISE_ADDRESSES (Ne.md:4090–4099)

### FranchiseAddress → XXSL_FRANCHISE_ADDRESSES
Id: ID (assigned).

| Field | Column | Type |
|---|---|---|
| campusId | CAMPUS_ID | String(2) |
| name | NAME | String(32) nn |
| colHeiCode | COL_HEI_CODE | String(4) nn |
| endDate | END_DATE | Timestamp |


## Customer → XXSL_CUSTOMERS (Ne.md:4100–4158)

### Customer → XXSL_CUSTOMERS
Id: ID (assigned).

| Field | Column | Type |
|---|---|---|
| aliasId | ALIAS_ID | String(15) nn |
| partyId | PARTY_ID | Long |
| maritalStatus | MARITAL_STATUS | String(1) |
| existsInClassFlag | EXISTS_IN_CLASS_FLAG | String(1) nn |
| enrolledFlag | ENROLLED_FLAG | String(1) nn |
| bankruptFlag | BANKRUPT_FLAG | String(1) nn |
| currentLeaCode | CURRENT_LEA_CODE | String(4) |
| latestApplicationYear | LATEST_APPLICATION_YEAR | Integer |
| classMigratedFlag | CLASS_MIGRATED_FLAG | String(1) nn |
| leaMigratedFlag | LEA_MIGRATED_FLAG | String(1) nn |
| migrationBatchNumber | MIGRATION_BATCH_NUMBER | Integer |
| leaPartyId | LEA_PARTY_ID | String(30) |
| restrictSecondaryAuthInd | RESTRICT_SECONDARY_AUTH_IND | String(1) |
| parentalEducationFlag | PARENTAL_EDUCATION_FLAG | String(1) |
| corrFailedFlag | CORR_FAILED_FLAG | String(1) |
| fyfcAcadYear | FYFC_ACAD_YEAR | Integer |
| fyfcCourseCode | FYFC_COURSE_CODE | Long |
| fyfcCourseYear | FYFC_COURSE_YEAR | Integer |
| irMatchingStatus | IR_MATCHING_STATUS | Integer |
| artId | ART_ID | String(30) nn |
| activeInd | ACTIVE_IND | String(1) nn |
| title | TITLE | String(10) |
| forenames | FORENAMES | String(50) |
| surname | SURNAME | String(50) nn |
| sex | SEX | String(1) nn |
| studentSupportNumber | STUDENT_SUPPORT_NUMBER | String(13) |
| nationalInsuranceNumber | NATIONAL_INSURANCE_NUMBER | String(9) |
| ninoStatusType | NINO_STATUS_TYPE | String(2) |
| dateOfBirth | DATE_OF_BIRTH | Timestamp nn |
| townOfBirth | TOWN_OF_BIRTH | String(30) |
| countryOfBirth | COUNTRY_OF_BIRTH | String(30) |
| declaredEthnicityType | DECLARED_ETHNICITY_TYPE | String(2) |
| sensitiveInd | SENSITIVE_IND | String(1) |
| customerCareInd | CUSTOMER_CARE_IND | String(1) |
| suspectInd | SUSPECT_IND | String(1) |
| suspectReasonType | SUSPECT_REASON_TYPE | String(1) |
| suppressionInd | SUPPRESSION_IND | String(1) |
| suppressionReasonType | SUPPRESSION_REASON_TYPE | String(1) |
| preferredLanguageType | PREFERRED_LANGUAGE_TYPE | String(3) |
| brailleType | BRAILLE_TYPE | String(1) |
| preferredContactType | PREFERRED_CONTACT_TYPE | String(1) |
| preferredPhoneType | PREFERRED_PHONE_TYPE | String(9) |
| homePhoneNumber | HOME_PHONE_NUMBER | String(20) |
| workPhoneNumber | WORK_PHONE_NUMBER | String(20) |
| mobilePhoneNumber | MOBILE_PHONE_NUMBER | String(20) |
| emailAddress | EMAIL_ADDRESS | String(80) |
| preferredAddressType | PREFERRED_ADDRESS_TYPE | String(5) |
| previousSfdLacInd | PREVIOUS_SFD_LAC_IND | String(1) |
| versionNumber | VERSION_NUMBER | int nn |
| remoteApplication | REMOTE_APPLICATION | String(30) |
| remoteLastUpdatedBy | REMOTE_LAST_UPDATED_BY | String(30) |
| remoteLastUpdatedDateTime | REMOTE_LAST_UPDATED_DATE_TIME | Timestamp |
| addresses | key CSR_ID | set → Address |


## CourseYear → XXSL_COURSE_YEARS (Ne.md:4159–4168)

### CourseYear → XXSL_COURSE_YEARS
- **Composite id:** CourseYearPK (CRS_COURSE_CODE Long, AYR_ACAD_YR Integer, TERM_NUMBER Integer, COURSE_YEAR Integer).

| Field | Column | Type |
|---|---|---|
| startDate | START_DATE | Timestamp |
| endDate | END_DATE | Timestamp |
| courseFeeCap | COURSE_FEE_CAP | Integer |
| courseLocation | CLO_LOCATION_ID | many-to-one CourseLocation |


## CourseLocation → XXSL_COURSE_LOCATIONS (Ne.md:4169–4176)

### CourseLocation → XXSL_COURSE_LOCATIONS
Id: LOCATION_ID (assigned).

| Field | Column | Type |
|---|---|---|
| franchiseId | FRA_ID | Long |
| heiLocationId | HLN_LOCN_ID | Long |


## Course → XXSL_COURSES (Ne.md:4177–4204)

### Course → XXSL_COURSES
Id: COURSE_CODE (assigned).

| Field | Column | Type |
|---|---|---|
| colHeiCode | COL_HEI_CODE | String(4) nn |
| ucasCourse | UCAS_COURSE | String(1) nn |
| shortName | SHORT_NAME | String(41) |
| title | TITLE | String(120) |
| description | DESCRIPTION | String(120) |
| courseType | COURSE_TYPE | String(11) |
| qualification | QUALIFICATION | String(8) |
| methodOfAttendance | METHOD_OF_ATTENDANCE | AttendanceMethodTypeEnumType |
| duration | DURATION | Integer |
| status | STATUS | CourseStatusEnumType nn |
| thirdPartyCode | THIRD_PARTY_CODE | String(4) |
| createdDate | CREATED_DATE | Timestamp nn |
| lastUpdatedBy | LAST_UPDATED_BY | String(30) |
| lastUpdatedDateTime | LAST_UPDATED_DATE_TIME | Timestamp |
| closeEffectDate | CLOSE_EFFECT_DATE | Date |
| ucasSuffix | UCAS_SUFFIX | String(2) |
| heiExtractCode | HEI_EXTRACT_CODE | String(6) |
| termsChangedInd | TERMS_CHANGED_IND | yes_no |
| allowDistantRegistrationInd | ALLOW_DISTANT_REGISTRATION_IND | yes_no |
| openEffectDate | OPEN_EFFECT_DATE | Date |
| jacsCode | JACS_CODE | String(4) |
| courseYears | key CRS_COURSE_CODE | set → CourseYear |


## AwardAuthority → XXSL_AWARD_AUTHORITIES (Ne.md:4205–4223)

### AwardAuthority → XXSL_AWARD_AUTHORITIES
Id: LEA_CODE (assigned).

| Field | Column | Type |
|---|---|---|
| leaName | LEA_NAME | String(100) nn |
| leaShortCode | LEA_SHORT_CODE | String(4) |
| country | COUNTRY | String(30) nn |
| participating | PARTICIPATING | String(1) nn |
| startDate | START_DATE | Timestamp nn |
| endDate | END_DATE | Timestamp |
| auditInfo | LAST_UPDATED_BY, LAST_UPDATED_DATE_TIME, CREATED_BY, **CREATION_DATE** | HAuditInfo |
| migrationBatchNumber | MIGRATION_BATCH_NUMBER | Integer |
| rotGroupId | ROT_GROUP_ID | Long |
| potentialToMoveRg | POTENTIAL_TO_MOVE_RG | String(1) |
| domicileTypeEnum | DOMICILE_TYPE | DomicileTypeEnumType |
| awardAuthority | PROCESSING_LEA_CODE | many-to-one (self) |
| awardAuthorities | key PROCESSING_LEA_CODE | set (self) |


## Address → XXSL_ADDRESSES (Ne.md:4224–4241)

### Address → XXSL_ADDRESSES
- **Composite id:** AddressPK (CSR_ID Long, ADDRESS_TYPE String(5)).
- **Cache:** read-only.

| Field | Column | Type |
|---|---|---|
| addressLine1..3 | ADDRESS_LINE_1..3 | String(240) (line 1 nn) |
| city | CITY | String(60) |
| county | COUNTY | String(60) |
| postCode | POST_CODE | String(8) |
| inactiveDate | INACTIVE_DATE | Timestamp |
| versionNumber | VERSION_NUMBER | int nn |
| auditInfo | audit cols | HAuditInfo |
| remoteApplication | REMOTE_APPLICATION | String(30) |
| remoteLastUpdatedBy | REMOTE_LAST_UPDATED_BY | String(30) |
| remoteLastUpdatedDateTime | REMOTE_LAST_UPDATED_DATE_TIME | Timestamp |
| customer | CSR_ID | many-to-one Customer |


## 5.3 hei-subscription-hibernate-service (`uk.co.slc.hei.subscription.service.data`) (Ne.md:4242–4243)

## 5.3 hei-subscription-hibernate-service (`uk.co.slc.hei.subscription.service.data`)


## SubscriptionPOJO → SUBSCRIPTIONS (Ne.md:4244–4257)

### SubscriptionPOJO → SUBSCRIPTIONS
Id: ID (seq `SUB_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| serviceTypeEnum | SERVICE_TYPE | ServiceTypeEnumType |
| suspendServiceIndicator | SUSPEND_SERVICE_IND | yes_no |
| effectiveFrom | EFFECTIVE_FROM_DATE | date |
| effectiveTo | EFFECTIVE_TO_DATE | date |
| serviceProfileId | HSP_ID | long |
| HEICode | COL_HEI_CODE | string |
| subscriptionAcademicYears | key SUB_ID | set → SubscriptionAcademicYearPOJO |
| auditInfo | audit cols | HAuditInfo |


## SubscriptionAcademicYearPOJO → SUBSCRIPTION_ACADEMIC_YEARS (Ne.md:4258–4266)

### SubscriptionAcademicYearPOJO → SUBSCRIPTION_ACADEMIC_YEARS
Id: ID (seq `SAY_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| HESAcademicYearPOJO | HAY_ID | many-to-one |
| SubscriptionPOJO | SUB_ID | many-to-one |
| auditInfo | audit cols | HAuditInfo |


## RegulatoryMaximumPOJO → REGULATORY_MAXIMUMS (Ne.md:4267–4278)

### RegulatoryMaximumPOJO → REGULATORY_MAXIMUMS
Id: ID (seq `RMX_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| amount | AMOUNT | Double |
| hesAcademicYearId | HAY_ID | Long |
| domicileTypeId | DOMICILE_TYPE | Integer |
| productTypeId | PRODUCT_TYPE | Integer |
| studentCohortCategoryTypeId | STUDENT_COHORT_CATEGORY_TYPE | Integer |
| createdBy / createdOn / updatedBy / updatedOn | audit cols | string / date |


## HESAcademicYearPOJO → HES_ACADEMIC_YEARS (Ne.md:4279–4289)

### HESAcademicYearPOJO → HES_ACADEMIC_YEARS
Id: ID (seq `HAY_SEQ`).

| Field | Column | Type |
|---|---|---|
| academicYearPOJO | ACY_ID | many-to-one AcademicYearPOJO |
| serviceTypeEnum | SERVICE_TYPE | ServiceTypeEnumType |
| active | ACTIVE_IND | yes_no |
| currentYearIndicator | CURRENT_YEAR_IND | string |
| createdBy / createdOn / updatedBy / updatedOn | audit cols | string / date |


## AcademicYearPOJO → XXSL.XXSL_ACADEMIC_YEARS (Ne.md:4290–4299)

### AcademicYearPOJO → XXSL.XXSL_ACADEMIC_YEARS
Id: ID (assigned).

| Field | Column | Type |
|---|---|---|
| yearStartDate | YEAR_START_DATE | date nn |
| yearEndDate | YEAR_END_DATE | date nn |
| currentYearFlag | CURRENT_YEAR_FLAG | string(1) nn |
| rulesAvailableFlag | RULES_AVAILABLE_FLAG | string(1) nn |


## 5.4 hei-authentication-hibernate-service (read-only mappings) (Ne.md:4300–4301)

## 5.4 hei-authentication-hibernate-service (read-only mappings)


## UserGroupPOJO → XXSL_USR_GROUPS (Ne.md:4302–4311)

### UserGroupPOJO → XXSL_USR_GROUPS
Id: CODE (assigned).

| Field | Column | Type |
|---|---|---|
| name | NAME | String(50) nn |
| implementationReference | IMPLEMENTATION_REFERENCE | String(100) nn |
| category | CATEGORY | int nn |
| createdBy / createdDateTime / lastUpdatedBy / lastUpdatedDateTime | audit cols | String / Timestamp |


## UserGroupMembershipPOJO → XXSL_USER_GROUP_MEMBERSHIPS (Ne.md:4312–4322)

### UserGroupMembershipPOJO → XXSL_USER_GROUP_MEMBERSHIPS
Id: ID (assigned).

| Field | Column | Type |
|---|---|---|
| startDateTime | START_DATE_TIME | Timestamp nn |
| endDateTime | END_DATE_TIME | Timestamp |
| usgCode | USG_CODE | Long (read-only) |
| UserGroupPOJO | USG_CODE | many-to-one nn |
| audit cols | CREATED_BY / CREATED_DATE_TIME / LAST_UPDATED_BY / LAST_UPDATED_DATE_TIME | String / Timestamp |


## UserDetailsPOJO → XXSL_USERS (Ne.md:4323–4336)

### UserDetailsPOJO → XXSL_USERS
Id: USERNAME (assigned).

| Field | Column | Type |
|---|---|---|
| forenames | FORENAMES | String(50) nn |
| surname | SURNAME | String(50) nn |
| contactNumber | CONTACT_NUMBER | String(20) |
| expiredInd | EXPIRED_IND | String(1) nn |
| versionNumber | VERSION_NUMBER | long nn |
| emailAddress | EMAIL_ADDRESS | String(80) nn |
| audit cols | CREATED_BY / CREATED_DATE_TIME / LAST_UPDATED_BY / LAST_UPDATED_DATE_TIME | String / Timestamp |
| UserGroupMemberships | key USR_USERNAME | set → UserGroupMembershipPOJO |


## 5.5 hei-identify-hei-hibernate-service (`uk.co.slc.hei.identifyhei.service.data`) (Ne.md:4337–4338)

## 5.5 hei-identify-hei-hibernate-service (`uk.co.slc.hei.identifyhei.service.data`)


## HeiTuitionFeeTypePOJO → XXSL_HEI_TUITION_FEE_TYPES (Ne.md:4339–4347)

### HeiTuitionFeeTypePOJO → XXSL_HEI_TUITION_FEE_TYPES
- **Composite id:** HeiTuitionFeeTypeId (START_ACAD_YR Integer, COL_HEI_CODE String).

| Field | Column | Type |
|---|---|---|
| heiTuitionFeeType | HEI_TUITION_FEE_TYPE | String |
| endAcadYr | END_ACAD_YR | Integer |
| higherTuitionFeeAmount | HIGHER_TUITION_FEE_AMOUNT | Double |


## HEIPOJO → XXSL_COLLEGES (Ne.md:4348–4384)

### HEIPOJO → XXSL_COLLEGES
- **Id:** HEI_CODE (assigned).
- **Audit columns:** none.

| Field | Column | Type |
|---|---|---|
| createDate | CREATE_DATE | Timestamp nn |
| rootOrAliasInd | ROOT_OR_ALIAS_IND | String(1) nn |
| status | STATUS | String(1) nn |
| createUser | CREATE_USER | String(30) nn |
| linkDate | LINK_DATE | Timestamp |
| mergerEffectDate | MERGER_EFFECT_DATE | Timestamp |
| closeDate | CLOSE_DATE | Timestamp |
| closeEffectDate | CLOSE_EFFECT_DATE | Timestamp |
| promotionDate | PROMOTION_DATE | Timestamp |
| updateUser | UPDATE_USER | String(30) |
| region | REGION | String(20) |
| shortName | SHORT_NAME | String(45) |
| fullName | FULL_NAME | String(50) |
| constituency | CONSTITUENCY | String(50) |
| parentHEICode | PARENT_HEI_CODE | String(4) |
| pmntMthd | PMNT_MTHD | String(1) |
| mergeDate | MERGE_DATE | Timestamp |
| country | COUNTRY | String(16) |
| updateDate | UPDATE_DATE | Timestamp |
| geogGrp | GEOG_GRP | String(4) |
| prevPmntMthd | PREV_PMNT_MTHD | String(1) |
| ucasCode | UCAS_CODE | String(3) |
| sssIndicator | SSS_INDICATOR | String(1) |
| chaserSentDate | CHASER_SENT_DATE | Timestamp |
| ucasCodeUseInd | UCAS_CODE_USE_IND | String(1) |
| bacsFirstParticipation | BACS_FIRST_PARTICIPATION | String(1) |
| bacsFirstWoodenDollar | BACS_FIRST_WOODEN_DOLLAR | String(1) |
| rescheduleInd | RESCHEDULE_IND | String(1) |
| vtfParticipant | VTF_PARTICIPANT | String(1) |
| depth | (not mapped) | int |


## CollegeAcademicYearPOJO → XXSL_COLL_ACAD_YRS (Ne.md:4385–4391)

### CollegeAcademicYearPOJO → XXSL_COLL_ACAD_YRS
- **Composite id:** CollegeAcademicYearId (AYR_ACAD_YR Integer, COL_HEI_CODE String).

| Field | Column | Type |
|---|---|---|
| offaFeeCap | OFFA_FEE_CAP | Integer |


## 5.6 hei-payments-hibernate-service (`uk.co.slc.hei.payments.service.data`) (Ne.md:4392–4393)

## 5.6 hei-payments-hibernate-service (`uk.co.slc.hei.payments.service.data`)


## PaymentSchedulePOJO → PAYMENT_SCHEDULES (Ne.md:4394–4402)

### PaymentSchedulePOJO → PAYMENT_SCHEDULES
Id: ID (seq `PSC_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| scheduleDate | SCHEDULE_DATE | Timestamp |
| serviceTypeEnum | SERVICE_TYPE | ServiceTypeEnumType |
| auditInfo | audit cols | HAuditInfo |


## PaymentInstalmentPOJO → PAYMENT_INSTALMENTS (Ne.md:4403–4418)

### PaymentInstalmentPOJO → PAYMENT_INSTALMENTS
Id: ID (seq `PIN_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| instalmentNumber | INSTALMENT_NUMBER | Integer |
| paymentDate | PAYMENT_DATE | Timestamp |
| statusDate | STATUS_DATE | Timestamp |
| amount | AMOUNT | Double |
| active | ACTIVE_IND | yes_no nn |
| statusTypeEnum | STATUS_TYPE | PaymentInstalmentStatusTypeEnumType |
| triggerTypeEnum | TRIGGER_TYPE | TriggerTypeEnumType |
| pendedReasonTypeEnum | PENDED_REASON_TYPE | PendedReasonTypeEnumType |
| auditInfo | audit cols | HAuditInfo |
| paymentInstalmentHistories | key PIN_ID | set → PaymentInstalmentHistoryPOJO |


## PaymentInstalmentHistoryPOJO → PAYMENT_INSTALMENT_HISTORIES (Ne.md:4419–4423)

### PaymentInstalmentHistoryPOJO → PAYMENT_INSTALMENT_HISTORIES
- **Id:** ID (seq `PIH_ID_SEQ`).
- **Columns:** the same 8 business columns as PAYMENT_INSTALMENTS, plus auditInfo.
- **Relation:** paymentInstalmentPOJO (PIN_ID, many-to-one nn).


## FundingRunPOJO → FUNDING_RUNS (Ne.md:4424–4434)

### FundingRunPOJO → FUNDING_RUNS
Id: ID (seq `FRU_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| requestDate | REQUEST_DATE | Timestamp nn |
| serviceTypeEnum | SERVICE_TYPE | ServiceTypeEnumType |
| runStatusTypeEnum | RUN_STATUS_TYPE | RunStatusTypeEnumType |
| auditInfo | audit cols | HAuditInfo |
| collegeFunds | key FRU_ID | set → CollegeFundPOJO |


## FeeWaiverInstalmentPOJO → FEE_WAIVER_INSTALMENTS (Ne.md:4435–4449)

### FeeWaiverInstalmentPOJO → FEE_WAIVER_INSTALMENTS
Id: ID (seq `FWI_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| instalmentNumber | INSTALMENT_NUMBER | Integer |
| statusDate | STATUS_DATE | Timestamp |
| amount | AMOUNT | Double |
| active | ACTIVE_IND | yes_no nn |
| feeWaiverDate | FEE_WAIVER_DATE | Timestamp |
| statusTypeEnum | STATUS_TYPE | FeeWaiverInstalmentStatusTypeEnumType |
| pendedReasonTypeEnum | PENDED_REASON_TYPE | FeeWaiverPendedReasonTypeEnumType |
| auditInfo | audit cols | HAuditInfo |
| feeWaiverInstalmentHistories | key FWI_ID | set → FeeWaiverInstalmentHistoryPOJO |


## FeeWaiverInstalmentHistoryPOJO → FEE_WAIVER_INSTAL_HISTORIES (Ne.md:4450–4454)

### FeeWaiverInstalmentHistoryPOJO → FEE_WAIVER_INSTAL_HISTORIES
- **Id:** ID (seq `FWH_ID_SEQ`).
- **Columns:** the same 7 business columns as FEE_WAIVER_INSTALMENTS, plus auditInfo.
- **Relation:** feeWaiverInstalmentPOJO (FWI_ID, nn).


## CollegeFundPOJO → COLLEGE_FUNDS (Ne.md:4455–4468)

### CollegeFundPOJO → COLLEGE_FUNDS
Id: ID (seq `CLF_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| requestAmount | REQUEST_AMOUNT | BigDecimal |
| totalPaymentAmount | TOTAL_PAYMENT_AMOUNT | BigDecimal |
| paymentDate | PAYMENT_DATE | Timestamp nn |
| statusTypeEnum | STATUS_TYPE | CollegeFundStatusTypeEnumType |
| colHeiCode | COL_HEI_CODE | String(4) nn |
| acyId | ACY_ID | int nn |
| auditInfo | audit cols | HAuditInfo |
| fundingRunPOJO | FRU_ID | many-to-one nn |


## 5.7 hei-contacts-hibernate-service (`uk.co.slc.hei.heicontacts.service.data`) (Ne.md:4469–4470)

## 5.7 hei-contacts-hibernate-service (`uk.co.slc.hei.heicontacts.service.data`)


## ServiceContactTypePOJO → SERVICE_CONTACT_TYPES (Ne.md:4471–4478)

### ServiceContactTypePOJO → SERVICE_CONTACT_TYPES
Id: ID (assigned).

| Field | Column | Type |
|---|---|---|
| serviceTypeEnum | SERVICE_TYPE | ServiceTypeEnumType |
| hesContactTypeEnum | HES_CONTACT_TYPE | HesContactTypeEnumType |


## ServiceContactPOJO → HEI_SERVICE_CONTACTS (Ne.md:4479–4487)

### ServiceContactPOJO → HEI_SERVICE_CONTACTS
Id: ID (seq `HSC_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| contactPOJO | HCO_ID | many-to-one ContactPOJO |
| serviceContactTypePOJO | SCT_ID | many-to-one |
| auditInfo | audit cols | HAuditInfo |


## ContactPOJO → HEI_HES_CONTACTS (Ne.md:4488–4506)

### ContactPOJO → HEI_HES_CONTACTS
Id: ID (seq `HCO_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| title | TITLE | string |
| forenames | FORENAMES | string |
| surname | SURNAME | string |
| jobTitle | JOB_TITLE | string |
| department | DEPARTMENT | string |
| HEIOffice | HEI_OFFICE | string |
| workPhonenumber | WORK_PHONE_NUMBER | string |
| workExtensionumber | WORK_EXTENSION_NUMBER | string |
| workEmailaddress | WORK_EMAIL_ADDRESS | string |
| deleted | DELETED | yes_no |
| HEICode | COL_HEI_CODE | string |
| auditInfo | audit cols | HAuditInfo |
| serviceContacts | key HCO_ID | set → ServiceContactPOJO |


## 5.8 JLBHEI non-mapped classes & stored procedures (Ne.md:4507–4531)

## 5.8 JLBHEI non-mapped classes & stored procedures
| Class | Fields |
|---|---|
| VAuditInfo | createdBy String, createdDate Date, lastUpdatedBy String |
| HAuditInfo (extends VAuditInfo) | lastUpdatedDate Date |
| CourseSearchCriteria | ucasCode String, slcCourseCode Long, courseName, heiCode String, modeOfStudy ModeOfStudyEnum, ucasSuffix String, closeEffectDate Date, courseFullName String |
| CourseYearSearchCriteria | crsCourseCode Long, academicYear, courseYear, courseTerm Integer |
| QualificationFilterCriteria | code, description String, activeIndicator YesNoEnum |
| CourseYearPK / AddressPK / HeiTuitionFeeTypeId / CollegeAcademicYearId | Key fields as listed above (from the mappings) |
| Instalment (interface) | id Long, instalmentNumber Integer, statusDate Date, amount Double, active boolean |

*Not read:* the `api.data` interfaces and the jsf-library beans (IdentifyHeiBean, PaginatorBean, AuthenticatedUser, AcademicYearBean, …).

| Stored procedure | Class |
|---|---|
| GUTPK004.p_req_validate_bank_details | PReqValidateBankDetails |
| GUTPK004.p_acc_accept_dd | PAcceptDD |
| GUTPK007.p_get_user_roles | JDBCUserRoleDAO |
| gutpk014.p_file_write | PLogJobDetails |


--------------------------------------------

---


## Part 6 – JAVHEP & JAVSIS (Ne.md:4532–4533)

# Part 6 – JAVHEP & JAVSIS


## 6.1 JAVHEP (Hibernate `hbm.xml`) (Ne.md:4534–4537)

## 6.1 JAVHEP (Hibernate `hbm.xml`)

JAVHEP is the shared `hep-sis-library`. It has 19 modules; the mappings are in `slc-data`, `slc-currency` and `slc-sync-exceptions-hibernate-service`.


## XxslSyncException (`uk.co.slc.sync.exceptions.service.data`) → XXSL_SYNC_EXCEPTIONS (Ne.md:4538–4559)

### XxslSyncException (`uk.co.slc.sync.exceptions.service.data`) → XXSL_SYNC_EXCEPTIONS
Id: ID (seq `SYE_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| categoryTypeEnum | CATEGORY_TYPE | CategoryTypeEnumType nn |
| recordType | RECORD_TYPE | RecordTypeEnumType nn |
| sourceTable | SOURCE_TABLE | String(30) nn |
| primarySelectionDetails | PRIMARY_SELECTION_DETAILS | String(30) nn |
| primarySelectionValue | PRIMARY_SELECTION_VALUE | String(255) nn |
| secondarySelectionDetails | SECONDARY_SELECTION_DETAILS | String(255) |
| secondarySelectionValue | SECONDARY_SELECTION_VALUE | String(255) |
| triggeringModule | TRIGGERING_MODULE | String(50) nn |
| errorDetails | ERROR_DETAILS | String(4000) nn |
| recordDetails | RECORD_DETAILS | String(4000) |
| statusType | STATUS_TYPE | StatusTypeEnumType |
| createdBy | CREATED_BY | String(30) nn |
| createdDateTime | CREATED_DATE_TIME | Timestamp nn |
| lastUpdatedBy | LAST_UPDATED_BY | String(30) nn |
| lastUpdatedDateTime | LAST_UPDATED_DATE_TIME | Timestamp nn |
| versionNumber | VERSION_NUMBER | Integer |


## PersistentExchangeRate (`uk.co.slc.currency.impl`) → XXSL_CURRENCY_RATES (Ne.md:4560–4570)

### PersistentExchangeRate (`uk.co.slc.currency.impl`) → XXSL_CURRENCY_RATES
Id: id (seq `cra_id_seq`).

| Field | Column | Type |
|---|---|---|
| effectiveFromDate | effective_from | Date nn |
| effectiveToDate | effective_to | Date |
| rate | rate | Double nn |
| currency | cur_currency_code | CurrencyEnumType nn |
| createdBy / createdDate / lastUpdatedBy / lastUpdatedDate | created_by / created_date_time / last_updated_by / last_updated_date_time | String / Date |


## ServiceTransactionLog → XXSL_SERVICE_TRANSACTION_LOGS (immutable) (Ne.md:4571–4585)

### ServiceTransactionLog → XXSL_SERVICE_TRANSACTION_LOGS (immutable)
Id: ID (seq `XXSL_STL_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| serviceType | service_type | ServiceTransactionTypeEnumType nn |
| requestDescription | request_description | String(100) nn |
| serviceUserId | service_user_id | String(30) nn |
| failureReason | failure_reason | String(4000) |
| failureIndicator | failure_indicator | String(1) |
| requestStartDateTime | request_start_date_time | Date nn |
| requestEndDateTime | request_end_date_time | Date |
| exiId | exi_id | Long |
| auditInfo | created_by / created_date_time / last_updated_by / last_updated_date_time | HAuditInfo |


## SecretQuestion → XXSL_OM_SECRET_QUESTION (immutable, read-only cache) (Ne.md:4586–4593)

### SecretQuestion → XXSL_OM_SECRET_QUESTION (immutable, read-only cache)
Id: id Integer (assigned).

| Field | Column | Type |
|---|---|---|
| available | is_available | yes_no nn |
| questionTextMap | map table XXSL_OM_SECRET_QUESTION_V (key id, index language, element question) | SortedMap |


## LondonPostcode → XXSL_LONDON_POSTCODES (immutable) (Ne.md:4594–4599)

### LondonPostcode → XXSL_LONDON_POSTCODES (immutable)
| Field | Column | Type |
|---|---|---|
| identifier.postcode | postcode | String (key) |
| identifier.year | acy_id | Integer (key) |


## ExternalIdentification → XXSL_EXTERNAL_IDENTIFICATIONS (Ne.md:4600–4613)

### ExternalIdentification → XXSL_EXTERNAL_IDENTIFICATIONS
Id: ID (seq `XXSL_EXI_ID_SEQ`).

| Field | Column | Type |
|---|---|---|
| externalPartyType | external_party_type | Integer nn |
| externalIdType | external_id_type | Integer nn |
| externalId | external_id | String(30) nn |
| tokenOfConsent | token_of_consent | String(100) nn |
| consentToDateTime | consent_to_date_time | Date |
| consentFromDateTime | consent_from_date_time | Date nn |
| csrId | csr_id | Long nn |
| auditInfo | 4 audit cols | HAuditInfo |


## Country → FND_TERRITORIES_TL (immutable) (Ne.md:4614–4621)

### Country → FND_TERRITORIES_TL (immutable)
Id: territory_code String (assigned).

| Field | Column | Type |
|---|---|---|
| shortDescriptionMap | map table XXSL_FND_TERRITORIES_TL_V (territory_short_name by language) | SortedMap |
| longDescriptionMap | map table XXSL_FND_TERRITORIES_TL_V (description by language) | SortedMap |


## AwardAuthority → XXSL_AWARD_AUTHORITIES (immutable) (Ne.md:4622–4640)

### AwardAuthority → XXSL_AWARD_AUTHORITIES (immutable)
Id: lea_code String (assigned).

| Field | Column | Type |
|---|---|---|
| LEANameMap | map table XXSL_AWARD_AUTHORITIES_V (lea_name by language) | SortedMap |
| domicile | domicile_type | DomicileEnumType |
| participating | participating | yes_no nn |
| startDate | start_date | Date nn |
| endDate | end_date | Date |
| migrationBatchNumber | migration_batch_number | Integer |
| LEAShortCode | lea_short_code | String(4) unique |
| rotGroupId | rot_group_id | Long |
| potentialToMoveRg | potential_to_move_rg | String(1) |
| processingAwardAuthority | processing_lea_code | many-to-one (self) |
| Cpu | CPU_IND | yes_no |
| auditInfo | created_by / creation_date / last_updated_by / last_updated_date_time | HAuditInfo |
| addresses | key lea_code | set → AwardAuthorityAddress |


## AwardAuthorityAddress → XXSL_AWA_ADDRESSES (immutable) (Ne.md:4641–4651)

### AwardAuthorityAddress → XXSL_AWA_ADDRESSES (immutable)
- **Composite id:** pk (address_order Integer, lea_code → AwardAuthority).

| Field | Column | Type |
|---|---|---|
| startDate | start_date | Date nn |
| endDate | end_date | Date |
| migrationBatchNumber | migration_batch_number | Integer |
| addressLineMap | map table XXSL_AWA_ADDRESSES_V (address_line by language) | SortedMap |
| auditInfo | created_by / creation_date / last_updated_by / last_updated_date_time | HAuditInfo |


## AcademicYear → XXSL_ACADEMIC_YEARS (immutable) (Ne.md:4652–4661)

### AcademicYear → XXSL_ACADEMIC_YEARS (immutable)
Id: id Integer (assigned).

| Field | Column | Type |
|---|---|---|
| yearStartDate | year_start_date | Date nn |
| yearEndDate | year_end_date | Date nn |
| currentYearFlag | current_year_flag | String(1) nn |
| rulesAvailableFlag | rules_available_flag | String(1) nn |


## JAVHEP non-mapped data classes (Ne.md:4662–4681)

### JAVHEP non-mapped data classes
| Class | Fields |
|---|---|
| data.object.HAuditInfo | createdBy String, createdDate Date, lastUpdatedBy String, lastUpdatedDate Date |
| data.object.AuditedDataObject | auditInfo AuditInfo |
| data.object.VersionedAuditedDataObject | versionedAuditInfo VersionedAuditInfo |
| data.object.Money | amount BigDecimal, currency CurrencyEnum |
| data.object.CompleteNino / customer.object.CustomerNino | nino, ninoStatus, noNinoReason String |
| data.object.IndexedObject | index int, object Object |
| lea.object.LEADetails | userGroupCode long, LEACode, LEAName, country String, participating boolean |
| bank.object.SimpleBankDetails | bankSortCode Integer, bankAccountNumber Long, buildingSocRollNumber String |
| customer.object.CompleteCustomerDetails | aliasId Long, artId String, bankrupt boolean, classMigrated boolean, contactDetails ContactDetails, countryOfBirth String, countryOfNationality AllCountriesEnum, customerId Long, customerName Name, dateOfBirth Date, enrolled, existsInClass boolean, latestApplicationYear Integer, leaMigrated boolean, maritalStatus MaritalStatusEnum, ninoDetails Nino, placeOfBirth String, profile Profile, sex SexEnum, ssn String, LEACode Lea |
| customer.object.CustomerAddress | addressLine1–4, townCity, county, postcode, country String, addressFromDate, addressToDate Date |
| customer.object.CustomerContactDetails | contactAddress, homeAddress Address, emailAddress ContactPoint, homeTel, workTel, mobileTel TelephoneContactPoint, preferredPhoneType PhoneTypeEnum |
| customer.object.CustomerName | firstName, surname String, title ContactTitleEnum, titleDescription, middleNames String |
| customer.object.CustomerProfile | preferredMedium PreferredMediumEnum, sensitive, suspect boolean, suspectReason String, alternativeFormat AlternativeFormatEnum, customerCare boolean, correspondenceToBeSentTo CorrespondenceToBeSentToEnum, correspondenceSuppressed boolean, suppressionReason String, preferredLanguage PreferredLanguageEnum, preferredLanguageDescription, ethnicOrigin, parentEducated String, termsAndConditionsAccepted boolean |
| customer.object.CustomerSearchCriteria | username, restrictToLEA, forenames, surname, artID String, dateOfBirth Date, postcode, aliasID, artIdList, studentSupportNumber String |
| customer.object.CustomerSearchSummary | customerID long, artID, forenames, surname, sex String, dateOfBirth Date, townOfBirth, studentSupportNumber, leaName, leaCode String, latestApplicationYear int |
| datasources.api.data.DataSourceStatus | datasourceName DataSourceEnum, isAvailable boolean, refreshInterval int, dateLoaded Date |


## JAVHEP raw SQL / stored procedures (Ne.md:4682–4691)

### JAVHEP raw SQL / stored procedures
| Object | Where |
|---|---|
| XXSL_PROTOCOL_PROPERTIES | slc-properties applicationContext.xml |
| XXSL_CORRESPONDENCE_REQUESTS | SpringJdbcRetrieveXMLContentSQLQuery |
| GCUPK001 (P_VAL_NINO, P_CALL_FORMAT_NINO, p_search_customers, p_set_profile, p_get_profile, P_MATCH_CUSTOMERS), GCUPK002.P_SET_ADDRESS | customer StoredProcedure classes |
| GUTPK002, GUTPK004.F_LEA_CODE_FROM_CUSTOMER_ID, GUTPK006 (p_generate_ssn, F_VALIDATE_AR_ID), GUTPK019 (f_accepted_tcs, p_customer_accept_tcs), gutpk013.p_get_data_source | util StoredProcedure classes |
| SUTPK205 (F_GET_PROCESSING_LEA_DETAILS, p_list_leas, F_GET_LEA_PARTICIPATION) | LEA StoredProcedure classes |
| scipk201.*, GCIPK001.p_java_correspondence_verbose, gcipk002.*, gcipk004.* | correspondence StoredProcedure classes |


## 6.2 JAVSIS (JSF UI – no persistence) (Ne.md:4692–4708)

## 6.2 JAVSIS (JSF UI – no persistence)

JAVSIS has no `hbm.xml` files, no `@Entity` classes and no raw SQL. Its modules are sis-application-war, sis-screens, hesc-screens, sis-lle-screens, sis-sifimport-publisher-api and sis-sifimport-publisher-service.

| Class (sis-lle-screens) | Fields |
|---|---|
| lle.data.RegHistory | eventType, regStatus, lastUpdatedBy String, lastUpdatedDateTime Date |
| lle.data.AtnHistory | eventType, atnStatus, lastUpdatedBy String, lastUpdatedDateTime Date, feeWaiverAmount, studentToPayAmount, tuitionFeeLoanAmount BigDecimal, bankDetailsHeldInd, atnStatusShortName String |
| lle.data.LleRegAtnHistory | regEvents List&lt;RegHistory&gt;, atnEvents List&lt;AtnHistory&gt;, apcId long |
| lle.beans.LleQualificationPojo | qualificationCode String, academicYear Long, heiCode String |
| lle.beans.LleCampusPojo | campusId, name String, locationId Long |
| lle.beans.LleAcademicYearPojo | id long, activeInd String, acyId, serviceType Long, currentYearInd String |

*Not read:* the JSF backing beans in sis-screens (`uk.co.slc.hei.sis.beans`, 16 classes), hesc-screens (`uk.co.slc.hei.hesc.beans`, 14 classes) and sis-lle-screens (PaginationBean, LleApplicationBean, LleAppHistoryBean, AbstractLleFilterBean). These are UI state, not persistence.

---


## Part 7 – JMBSRI & JWSSIS (Ne.md:4709–4710)

# Part 7 – JMBSRI & JWSSIS


## 7.1 JMBSRI (Registration Import MDB) (Ne.md:4711–4728)

## 7.1 JMBSRI (Registration Import MDB)

JMBSRI has no mappings and no entities. Persistence goes through JLBSIS `SisCsvService` / `BulkFileSubmissionService`.

**Branches:**
- **Active:** BAU (EAR/EJB layout), SIS_UPLIFT_21, SIS_UPLIFT_21_BAU, SIS_UPLIFT_21_BAU_DEV, lle-dsfs-common, lle-cms-uplift, lle-cms-uplift-dev-features-merge, dsfs-dev-feature-r1.
- **Ignored:** `archived/*` and `*_BKP` contain no unique model classes.

| Class (`uk.co.slc.hei.sis.registration.processor.service`) | Fields |
|---|---|
| SpringSisRegistrationImportParameters | hesAcademicYearId Long, heiCode, heiUcasCode String, bulkFileSubmissionId Long, bodySize, startRowNumber, endRowNumber int |
| RegistrationImportException (SyncExceptionDetails) | cause Exception, message javax.jms.Message, primarySelectionValue, secondarySelectionValue String. Constants: SOURCE_TABLE=`XXAQ_HEI_REG_JMS_TXT_Q`, TRIGGERING_MODULE="REG Import Processor MDB" |

| DB object | Where |
|---|---|
| Oracle AQ queues `XXAQ.XXAQ_HEI_REG_JMS_TXT_Q1` … `Q6` | sis-regimport-procr-service applicationContext.xml |
| DataSource `jdbc/SSOLTPDS` | applicationContext.xml |


## 7.2 JWSSIS (SIS Application REST resource) (Ne.md:4729–4761)

## 7.2 JWSSIS (SIS Application REST resource)

JWSSIS has no mappings and no entities. Persistence goes through JLBSIS `hei-sis-data-service` and `hei-sis-coc-data-service`. Its modules are hei-application-data, hei-application-validation, hei-application-integration and hei-application-war.

| Class (`uk.co.slc.hei.sis.data.application`) | Fields |
|---|---|
| CoreApplication (abstract) | tuitionPeriodId Long, applicationKey ApplicationKey, changeEffectiveDate Date, studentCohortCategory Integer, modeOfStudy, bankDetailsHeldInd String, courseTransfer Integer, autoCocInd String, cocId Long, issueDate Date, clientId Long, fsnStatus String, tuitionPeriodApplication (@JsonIgnore) |
| Application | pngId, paymentNotificationId Long, tuitionFee, tuitionFeeSchedule, maintenanceFee, courseDetails, studentInformation, otherLocation, firstPaymentMadeInd, ucasId, studentStatus, newRecord, assessmentType String, chtId Long, workStageId Integer, autoApprovalInd String, withdrawReasonId Integer, rejectedRecordInd String, multipleAcademicYear Integer, internalTransferInd, externalTransferInd String, assessmentHistorySequence Integer, feeRevisionInd, reassessmentInd String, sentToSisViaEdsInd, changeSourceId, deletionAshSeq Integer, deletionFlag, failureReason String, pncId, sapId Long, source String, currentAyCourseCredits Integer, links List&lt;Link&gt; |
| PgdApplication / PglApplication | chtId, workStageId String, contributionLoanAmount BigDecimal, leaCode, changeSource, source, sourceApplicationId String |
| PgdOriginalApplication | chtId, workStageId String, applicationKey PGDApplicationKey, contributionLoanAmount AcademicContributionLoanAmount, leaCode, changeSource, source, sourceApplicationId String |
| ApplicationKey | customerId Long, heiCode String, academicYearId Integer, courseCode Long, courseYear Integer |
| PGDApplicationKey | customerCRN String |
| AcademicContributionLoanAmount | academicYear1 … academicYear8 BigDecimal |
| TuitionFee | courseFee, loan, grant, liability, waiver BigDecimal |
| TuitionFeeSchedule | scheduleEntries Map&lt;Integer, TuitionFeeScheduleEntry&gt; (YES/NO/SUSPENDED) |
| MaintenanceFee | grant, loan BigDecimal, loanAppliedFor, grantProducts, loanProducts String |
| LoanProducts / GrantProducts | productId Long, productName String |
| CourseDetails | currentAcyCourseStartDate Date |
| StudentInformation | bursaryConsentInd, childcareGrantInd, sponsor1BursaryConsentInd, sponsor2BursaryConsentInd String, numberOfSponsors Integer, nhsInd, leaId String |
| BursaryApprovalDetails | applicationKey, bursaryApprovalInd, sourceApplicationId |
| CocTrackingExceptionsData | id, sapId, academicYearId Long, heiCode String, customerId, cocId Long, cocCategoryType Integer, createdDateTime, lastUpdatedDateTime Date, createdBy, lastUpdatedBy String, failureReason Integer, errorInd, errorDetails String |
| SisRegistartionRequestKey / SisRegistartionDetails | customerId Long, heiCode String, academicYearId Long |
| framework.Link | rel, uri String |
| integration.data.AbstractApplicationNotificationRequest&lt;T&gt; (+ 4 subclasses) | applications List&lt;T&gt;, validateOnly boolean |
| integration.data.AbstractApplicationNotificationResponse&lt;T&gt; (+ 4 subclasses) | applications List&lt;T&gt;, errorMessage String, erroringApplication T |
| AppServiceNotificationResponse | tuitionPeriodId Long, errorMessage String |
| CocTrackingExceptionsRequest / Response | cocExceptionsData, validateOnly / id Long, errorMessage String |
| SisRegistrationDetailsResponse | registrationStatus, registrationCode, manualRegInd String, confirmationDate Date, errorMessage String |
| SisNotificationExceptionDetails | SOURCE_TABLE=TUITION_PERIODS |
| SisCocTrackingSyncExceptionDetails | SOURCE_TABLE=XXSS.COCS |

---


## Part 8 – Cross-Project Table Index (Ne.md:4762–4802)

# Part 8 – Cross-Project Table Index

The schema owners in this index are inferred from table prefixes and the schema names used in SQL:

| Prefix | Owner |
|---|---|
| `XXLE_` / `XXLC_` | LLE |
| `XXSL_` | shared SLC reference data |
| `XXSS.` / no prefix | SIS |
| `XXHE.` | HES |
| `XXAQ.` | AQ queues |
| `XXSN_` | SN |

| Table | Mapped by |
|---|---|
| XXLE_APPLICATIONS, XXLE_APPLICATION_HISTS, XXLE_APPLICATION_COURSES, XXLE_APP_COURSE_HISTS, XXLE_REGISTRATION_CONFS, XXLE_REGISTRATION_CONF_HISTS, XXLE_ATTENDANCE_CONFS, XXLE_ATTENDANCE_CONF_HISTS, XXLE_ATT_CONF_EVENTS, XXLE_EVENTS, XXLE_COCS, XXLE_COC_HISTORIES, XXLE_COC_COURSE_CREDITS, XXLE_MANUAL_REGISTRATIONS, XXLE_BULK_FILE_SUBMISSIONS, XXLE_STAGE_FILE_UPLOADS, XXLE_IMPORT_RESULTS, XXLE_IMPORT_FAILURE_DETAILS, XXLE_HEBSS_NOTIFICATIONS, XXLE_HEBSS_NOTIFICATION_STGS, XXLE_CCG_FAILURE_DETAILS | lle-model-library (JPA) |
| XXLC_CORE_COURSES, XXLC_CORE_MODULES, XXLC_COURSE_VARIANTS, XXLC_COURSE_VAR_ACAD_YRS, XXLC_COURSE_VAR_YEARS, XXLC_MODULE_VARIANTS, XXLC_MODULE_VAR_ACAD_YRS, XXLC_MODULE_VAR_YEARS, XXLC_STUDY_PERIODS | lle-model-library (JPA) |
| TUITION_PERIODS, TUITION_PERIOD_HISTORIES, TUITION_PERIOD_APPLICATIONS, TUITION_FINANCE_DETAILS, TUITION_FINANCE_HISTORIES | sis-model-library (JPA) + JLBSIS (hbm) |
| REGISTRATION_CONF_REQUESTS, REGISTRATION_CONF_REQ_HISTS, PGL_CONFIRMATION_REQUESTS, PGL_CONFIRMATION_REQUEST_HISTS, ATTENDANCE_CONF_REQUESTS, ATTENDANCE_CONF_REQUEST_HISTS, EVENTS, EVENT_CONF_REQUEST_HISTS, EDS_CANCELLATION_LOGS | sis-model-library + JLBSIS |
| COCS, COC_STAGE_DETAILS, COC_NOTIFICATIONS | sis-model-library + JLBSIS |
| COC_BCF_MAPS, COC_BCF_HISTORIES, COC_BCF_STAGE_DETAILS, COC_FAILURE_DETAILS | sis-model-library |
| BULK_FILE_SUBMISSIONS, STAGE_FILE_UPLOADS, IMPORT_RESULTS, IMPORT_FAILURE_DETAILS, IMPORT_COC_FAILURE_DETAILS | sis-model-library + JLBSIS |
| HEBSS_NOTIFICATIONS | sis-model-library |
| HEBSS_NOTIFICATION_STGS, PAYMENT_REJECTION_NOTIFS, ADDITIONAL_CUSTOMER_DETAILS, RULE_CONFIG_ATTRIBUTES, PUBLISHER_CONFIG_ATTRIBUTES | sis-model-library + JLBSIS |
| XXSS_APPLICATIONS | JLBSIS |
| HANDLER_CONFIG_ATTRIBUTES, NOTIFICATION_TYPE_CONFIGS, RCA_GROUPS, RCA_GROUP_ATTRIBUTES, RCA_GROUP_MAPPINGS, RCA_GROUP_FUNCTION_MAPPINGS, RCA_QUALIFICATIONS, PUBLISHER_FAILURE_DETAILS, HEI_PUBLISHER_LOGS, ATTENDANCE_NOTIFICATIONS, APPROVED_AWAITING_SIG_APPS, BLOCKED_MAINTENANCE_PAYMENTS, TUITION_FEE_REPORTS, TUITION_FEE_REPORT_HISTORIES | sis-model-library |
| HES_ACADEMIC_YEARS | sis-model-library + JLBHEI |
| XXSL_ACADEMIC_YEARS | sis-model-library + JLBHEI + JAVHEP |
| XXSL_CUSTOMERS, XXSL_COURSES, XXSL_COURSE_YEARS | sis-model-library + JLBHEI |
| XXSL_AWARD_AUTHORITIES | sis-model-library + JLBHEI + JAVHEP |
| XXSL_SYNC_EXCEPTIONS | lle-model-library + sis-model-library + JAVHEP |
| XXSL_PROTOCOL_PROPERTIES | sis-coc-service (JPA) |
| XXSL_COLLEGES, XXSL_COLL_ACAD_YRS, XXSL_HEI_TUITION_FEE_TYPES, XXSL_HEI_LOCNS, XXSL_FRANCHISE_ADDRESSES, XXSL_COURSE_LOCATIONS, XXSL_ADDRESSES, XXSL_USERS, XXSL_USR_GROUPS, XXSL_USER_GROUP_MEMBERSHIPS, QUALIFICATIONS | JLBHEI |
| HEI_BANK_ACCOUNTS, HEI_SERVICE_BANK_ACCOUNTS, SERVICE_BANK_USAGES, HEI_HES_CONTACTS, HEI_SERVICE_CONTACTS, SERVICE_CONTACT_TYPES, SUBSCRIPTIONS, SUBSCRIPTION_ACADEMIC_YEARS, REGULATORY_MAXIMUMS | JLBHEI |
| PAYMENT_SCHEDULES, PAYMENT_INSTALMENTS, PAYMENT_INSTALMENT_HISTORIES, FUNDING_RUNS, FEE_WAIVER_INSTALMENTS, FEE_WAIVER_INSTAL_HISTORIES, COLLEGE_FUNDS | JLBHEI |
| XXSL_CURRENCY_RATES, XXSL_SERVICE_TRANSACTION_LOGS, XXSL_OM_SECRET_QUESTION(_V), XXSL_LONDON_POSTCODES, XXSL_EXTERNAL_IDENTIFICATIONS, FND_TERRITORIES_TL / XXSL_FND_TERRITORIES_TL_V, XXSL_AWARD_AUTHORITIES_V, XXSL_AWA_ADDRESSES(_V) | JAVHEP |
| XXSL_REF_CODES, XXSL_CORRESPONDENCE_REQUESTS, XXSN_APP_NOTIFICATIONS, XXSN_APP_CONFIRMATIONS, XXSN_APP_COURSES, RCA_REG_CONFIGS, XXSS_COMPONENT_CONFIGS, HEI_PAGES, COMPONENTS, COMPONENT_TYPES, PAGE_COMPONENT_MAPPINGS, PAGE_COMPONENT_PROPERTIES, PAGE_COMPONENT_GROUP_MAPPINGS, RGP_COC_CATEGORY_MAPPINGS | raw SQL only (JLBSIS / JAVHEP / sis-coc-service) |
| APPLICATIONS, ASSESSMENTS, ASSESSMENT_ENTITLEMENTS, ENTITLEMENTS, ENT_PAYMENT_INSTALMENTS | raw SQL only (lle-application-service, sis-application-service) |
| XXAQ_HEI_REG_JMS_TXT_Q1..Q6 | AQ queues (JMBSRI) |

------------------------------------------