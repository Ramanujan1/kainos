const config = require('getconfig')
const makeBasicAuthorization = (user, password) => {
  // FIXME: basic auth constants are deprecated, see options in /services
  return 'Basic ' + Buffer.from(user + ':' + password).toString('base64')
}

const PERMISSIONS_SERVICE_USER = config.services.permissions.connection.login
const PERMISSIONS_SERVICE_PASSWORD = config.services.permissions.connection.password

module.exports = Object.freeze({
  GENERIC_PAGE_TITLE: ' - Import and export applications - GOV.UK',
  FEEDBACK_EMAIL: 'BFPEUExit.ImportsExports@defra.gov.uk',
  MAX_CONTAINERS: 70,
  MAX_ACCOMPANYING_DOCUMENTS: 20,
  ACCOMPANYING_DOCUMENT_NUMBER_REGEX: /accompanying-document-(\d{1,2})/,
  IMPORTER_URL_PATH: '/protected/notifications',
  BIP_URL_PATH: '/protected/bip-notifications',
  OV_URL_PATH: '/protected/ov-notifications',
  FSA_URL_PATH: '/protected/fsa-dashboard',
  PATCH_CONTENT_TYPE: 'application/json-patch+json;charset=utf-8',
  CVEDA: 'CVEDA',
  CVEDP: 'CVEDP',
  CED: 'CED',
  HOME_PATH: '/',
  BACK_LINK_NAME: 'Back',
  pageConfig: {
    EU_STANDARD_ID: 'euStandard',
    NATIONAL_REQUIREMENTS_ID: 'nationalRequirements',
    ADDITIONAL_GUARANTEES_ID: 'additionalGuarantees',
    DOCUMENTARY_CHECK_ID: 'documentaryCheck',
    IDENTITY_CHECK_ID: 'identityCheck',
    NUMBER_OF_ANIMALS_CHECKED_ID: 'numberOfAnimalsChecked',
    PHYSICAL_CHECK_ID: 'physicalCheckId',
    PHYSICAL_CHECK_REASON_ID: 'physicalCheckReason',
    WELFARE_CHECK_ID: 'welfareCheckId',
    DEAD_ANIMALS_ID: 'deadAnimalsId',
    UNFIT_ANIMALS_ID: 'unfitAnimalsId',
    BIRTHS_OR_ABORTIONS_ID: 'birthsOrAbortionsId'
  },
  applicationType: {
    CONTROL: 'CONTROL'
  },
  PERMISSIONS_SERVICE_AUTH: makeBasicAuthorization(PERMISSIONS_SERVICE_USER, PERMISSIONS_SERVICE_PASSWORD),
  organisationAddress: {
    street: 'Animal and Plant Health Agency (APHA), Eden Bridge House, Lowther Street',
    city: 'Carlisle',
    postcode: 'CA3 8DX',
    country: 'GB'
  },
  userRole: {
    INSPECTOR: 'inspector',
    IMPORTER: 'importer',
    VETERINARIAN: 'veterinarian'
  },
  status: {
    IN_PROGRESS: 'IN_PROGRESS',
    AMEND: 'AMEND',
    DRAFT: 'DRAFT',
    SUBMITTED: 'SUBMITTED',
    VALIDATED: 'VALIDATED',
    REJECTED: 'REJECTED',
    MODIFY: 'MODIFY',
    CANCELLED: 'CANCELLED',
    DELETED: 'DELETED',
    REPLACED: 'REPLACED'
  },
  controlStatus: {
    REQUIRED: 'REQUIRED',
    COMPLETED: 'COMPLETED'
  },
  pagePaths: {
    relative: {
      DECISION_PATH: '/decisions',
      DECLARATION_PATH: '/declaration',
      LAB_TESTS_REQUIRED_PATH: '/lab-tests-required',
      LAB_TESTS_TO_BE_PERFORMED_PATH: '/lab-test/lab-tests-to-be-performed',
      LAB_TEST_CHOOSE_COMMODITY_PATH: '/lab-test/choose-commodity',
      LAB_TEST_REASON_PATH: '/lab-test/reason',
      OVERVIEW_PATH: '/overview',
      REVIEW_PATH: '/review',
      SEALS_PATH: '/seals',
      CONSIGNMENT_PAGE_1: '/consignment/page-1',
      CONSIGNMENT_COUNTRY_OF_ORIGIN: '/consignment/page-2',
      CONSIGNMENT_PURPOSE: '/consignment/page-5',
      CONSIGNMENT_DETAILS: '/consignment/details',
      COMMODITY_DETAILS: '/commodity/details',
      TRANSPORT_BEFORE_BIP: '/transport/before-bip',
      VETERINARY_DOCUMENTS: '/documents/page-1',
      TRADERS: '/traders',
      TRANSPORT: '/transport',
      TRANSPORT_DETAILS: '/transport/details',
      ROUTE: '/route',
      CONSIGNEE_SEARCH: '/traders/consignee/search',
      CONSIGNOR_SEARCH: '/traders/consignor/search',
      IMPORTER_SEARCH: '/traders/importer/search',
      FINAL_DESTINATION_SEARCH: '/traders/final-destination/search',
      TRANSPORTER_SEARCH: '/transport/transporter/search',
      CONSIGNEE_NEW: '/traders/consignee/new',
      CONSIGNOR_NEW: '/traders/consignor/new',
      IMPORTER_NEW: '/traders/importer/new',
      FINAL_DESTINATION_NEW: '/traders/final-destination/new',
      TRANSPORTER_NEW: '/transport/transporter/new'
    }
  },
  pageTitles: {
    DECLARATION_TITLE: 'Declaration',
    LAB_TEST_APPLICANT_INFORMATION_TITLE: 'Lab Test Applicant Information',
    LAB_TEST_REASON_TITLE: 'Lab Test Reason',
    LAB_TEST_REQUIRED_TITLE: 'Laboratory Tests Required',
    LAB_TESTS_TO_PERFORM_TITLE: 'Laboratory Tests To Be Performed',
    LAB_TEST_RESULTS_TITLE: 'Record Results',
    BAD_REQUEST_TITLE: 'Bad request',
    PAGE_NOT_FOUND_TITLE: 'Page not found',
    INTERNAL_SERVER_ERROR_TITLE: 'Internal server error',
    REVIEW_TITLE: 'Review Notification',
    TRADERS: 'Traders',
    COOKIES_TITLE: 'Cookies',
    TRANSPORT: 'Transport',
    TRANSPORTER: 'Transporter',
    CONSIGNEE: 'Consignee',
    CONSIGNOR: 'Consignor or Exporter',
    FINAL_DESTINATION: 'Final Destination',
    IMPORTER: 'Importer',
    FEEDBACK_TITLE: 'Get in touch'
  },
  fieldIds: {
    labTestResults: {
      LAB_TEST_METHOD: 'lab-test-method',
      LAB_TEST_RESULTS: 'lab-test-results',
      RELEASED_DATE_DAY: 'released-date-day',
      RELEASED_DATE_MONTH: 'released-date-month',
      RELEASED_DATE_YEAR: 'released-date-year',
      SAMPLE_USE_BY_DATE_DAY: 'sample-use-by-date-day',
      SAMPLE_USE_BY_DATE_MONTH: 'sample-use-by-date-month',
      SAMPLE_USE_BY_DATE_YEAR: 'sample-use-by-date-year'
    }
  },
  TRADERS_PAGE_SIZE: 25,
  SAVE_BUTTON_NAME: 'save-button',
  SAVE_AND_CONTINUE_BUTTON_VALUE: 'Save and continue',
  SAVE_AND_RETURN_BUTTON_VALUE: 'Save and return',
  SAVE_AND_REVIEW_BUTTON_VALUE: 'Save and review',
  ETAG: 'etag',
  IMPORTER_OVERVIEW_PAGE: referenceNumber => `/protected/notifications/${referenceNumber}/overview`,
  BIP_HUB_PAGE: referenceNumber => `/protected/bip-notifications/${referenceNumber}/hub`,
  BIP_OVERVIEW_PAGE: referenceNumber => `/protected/bip-notifications/${referenceNumber}/overview`,
  EXISTING_NOTIFICATIONS: '/protected/notifications#existing-notifications',
  DEFAULT_PHYSICAL_CHECK_RATE: 100,
  FROM_IMPORTER_REVIEW_TRUE: 'fromImporterReview=true',
  RE_IMPORT_PURPOSE_GROUP: 'For Re-Import',
  traders: {
    CONSIGNEE: 'consignee',
    DESTINATION: 'destination',
    IMPORTER: 'importer',
    COMMERCIAL_TRANSPORTER: 'commercial transporter',
    PRIVATE_TRANSPORTER: 'private transporter',
    EXPORTER: 'exporter',
    status: {
      APPROVED: 'approved',
      NONAPPROVED: 'nonapproved',
      SUSPENDED: 'suspended'
    }
  },
  economicOperators: {
    addressType: {
      UK: 'uk',
      TRANSPORTER: 'transporter',
      CONSIGNOR: 'consignor'
    },
    type: {
      CONSIGNEE: 'consignee',
      DESTINATION: 'destination',
      EXPORTER: 'exporter',
      IMPORTER: 'importer'
    }
  },
  EXISTING_BIP_NOTIFICATIONS: '/protected/bip-notifications',
  CONSIGNMENT_CHECK_NOT_SET: 'Not Set',
  user: {
    type:{
      B2C: 'B2C',
      B2B: 'B2B'
    },
    role: {
      B2C_NOTIFIER: '29072a8c-73b6-e811-a954-000d3a29b5de',
      B2C_INSPECTOR: 'e46b89c2-cd02-e911-a847-000d3ab4ffef',
      B2B_INSPECTOR: 'inspector',
      B2B_COMPETENT_AUTHORITY: 'importsadministrator',
      B2B_CONTROL_AUTHORITY: 'lvu',
      B2B_REENFORCEMENT_CONTROL: 'reenforcementcontrol'
    }
  },
  REQUEST_META_DATA_VAR_NAME: 'requestMetaData',
  REQUEST_META_DATA_VIEW_CONTEXT_VAR_NAME: 'requestMetaDataInViewContext',
  permissions: {
    PART1_READ_PERMISSION: 'frontend-notification.read',
    PART2_READ_PERMISSION: 'frontend-decision.read',
    NOTIFICATION_UPDATE: 'notification.update'
  },
  encoding: {
    BASE64: 'base64',
    UTF8: 'utf8'
  },
  decisionDropDownValues: {
    ACCEPTABLE_FOR_TRANSIT_PROCEDURE: {
      labelValue: 'Acceptable for transit procedure',
      entityValue: 'Acceptable for Transit',
      subOption: ''
    },
    ACCEPTABLE_FOR_DEFINITIVE_IMPORT_SLAUGHTER: {
      labelValue: 'Acceptable for definitive Import – for controlled destination – slaughter',
      entityValue: 'Definitive import',
      subOption: 'slaughter'
    },
    ACCEPTABLE_FOR_DEFINITIVE_IMPORT_APPROVED_BODIES: {
      labelValue: 'Acceptable for definitive Import – for controlled destination – approved bodies',
      entityValue: 'Definitive import',
      subOption: 'approvedbodies'
    },
    ACCEPTABLE_FOR_DEFINITIVE_IMPORT_QUARANTINE: {
      labelValue: 'Acceptable for definitive import – for controlled destination – quarantine',
      entityValue: 'Definitive import',
      subOption: 'quarantine'
    },
    ACCEPTABLE_FOR_TEMPORARY_ADMISSION: {
      labelValue: 'Acceptable for temporary admission',
      entityValue: 'Acceptable for Temporary Import',
      subOption: ''
    },
    ACCEPTABLE_IF_CHANNELLED_ARTICLE_8: {
      labelValue: 'Acceptable if channelled – article 8 procedure',
      entityValue: 'Acceptable if Channeled',
      subOption: 'article8'
    },
    ACCEPTABLE_IF_CHANNELLED_ARTICLE_15: {
      labelValue: 'Acceptable if channelled – re-import (article 15)',
      entityValue: 'Acceptable if Channeled',
      subOption: 'article15'
    },
    ACCEPTABLE_FOR_SPECIFIC_WAREHOUSE_PROCEDURE: {
      labelValue: 'Acceptable for specific warehouse procedure',
      entityValue: 'Acceptable for Specific Warehouse',
      subOption: ''
    },
    NOT_ACCEPTABLE_REASON_REDISPATCH: {
      labelValue: 'Not acceptable – re-dispatching ',
      entityValue: 'Non Acceptable',
      subOption: 'redispatching'
    },
    NOT_ACCEPTABLE_REASON_SLAUGHTER: {
      labelValue: 'Not acceptable – slaughter',
      entityValue: 'Non Acceptable',
      subOption: 'slaughter'
    },
    NOT_ACCEPTABLE_REASON_EUTHANASIA: {
      labelValue: 'Not acceptable – euthanasia',
      entityValue: 'Non Acceptable',
      subOption: 'euthanasia'
    },
    NOT_ACCEPTABLE_REASON_RE_EXPORT: {
      labelValue: 'Not acceptable – re-export',
      entityValue: 'Non Acceptable',
      subOption: 'reexport'
    },
    NOT_ACCEPTABLE_REASON_DESTRUCTION: {
      labelValue: 'Not acceptable – destruction',
      entityValue: 'Non Acceptable',
      subOption: 'destruction'
    },
    NOT_ACCEPTABLE_REASON_TRANSFORMATION: {
      labelValue: 'Not acceptable – transformation',
      entityValue: 'Non Acceptable',
      subOption: 'transformation'
    },
    NOT_ACCEPTABLE_REASON_OTHER_PURPOSE: {
      labelValue: 'Not acceptable – use for other purpose',
      entityValue: 'Non Acceptable',
      subOption: 'other'
    }
  }
})
