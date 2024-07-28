const Joi = require('joi')

const datetime = require('../utils/datetime')
const {SAVE_BUTTON_NAME, ETAG} = require('../utils/constants')
const {SEARCH_STRING_KEY, SEARCH_STRING_LABEL, CONSIGNEE_STRING_KEY, CONSIGNEE_STRING_LABEL,
  START_DATE_DAY_KEY, START_DATE_DAY_LABEL, START_DATE_MONTH_KEY, START_DATE_MONTH_LABEL,
  START_DATE_YEAR_KEY, START_DATE_YEAR_LABEL, END_DATE_DAY_KEY, END_DATE_DAY_LABEL,
  END_DATE_MONTH_KEY, END_DATE_MONTH_LABEL, END_DATE_YEAR_KEY, END_DATE_YEAR_LABEL,
  SELECTED_BIP_KEY, SELECTED_STATUS_KEY, SELECTED_CONTROL_STATUS_KEY, COUNTRY_OF_ORIGIN_KEY,
  DATE_RANGE_KEY, SELECTED_CERTIFICATE_TYPE_KEY, DECISION_KEY} = require('../utils/validation_constants')
const {getOptions, MAX_LENGTH_VALIDATION_MESSAGE} = require('../validation/validation_messages')

const preparePayloadWithDates = (payload, dateConfig) => {
  if (payload[dateConfig.year.id]) {
    payload[dateConfig.year.id] = datetime.prefixYear(payload[dateConfig.year.id])
  }
  if (payload[dateConfig.day.id]) {
    payload[dateConfig.day.id] = datetime.prefixDay(payload[dateConfig.day.id])
  }
  if (payload[dateConfig.month.id]) {
    payload[dateConfig.month.id] = datetime.prefixMonth(payload[dateConfig.month.id])
  }
  return payload
}

const searchSchema = {
  [SEARCH_STRING_KEY]: Joi.string().allow('').max(255).options(
      getOptions('string', 'max', MAX_LENGTH_VALIDATION_MESSAGE(255))).label(SEARCH_STRING_LABEL),
  [CONSIGNEE_STRING_KEY]: Joi.string().allow('').max(255).options(
      getOptions('string', 'max', MAX_LENGTH_VALIDATION_MESSAGE(255))).label(CONSIGNEE_STRING_LABEL),
  [START_DATE_DAY_KEY]: Joi.any().label(START_DATE_DAY_LABEL),
  [START_DATE_MONTH_KEY]: Joi.any().label(START_DATE_MONTH_LABEL),
  [START_DATE_YEAR_KEY]: Joi.any().label(START_DATE_YEAR_LABEL),
  [END_DATE_DAY_KEY]: Joi.any().label(END_DATE_DAY_LABEL),
  [END_DATE_MONTH_KEY]: Joi.any().label(END_DATE_MONTH_LABEL),
  [END_DATE_YEAR_KEY]: Joi.any().label(END_DATE_YEAR_LABEL),
  [SELECTED_BIP_KEY]: Joi.any(),
  [SELECTED_STATUS_KEY]: Joi.any(),
  [SELECTED_CONTROL_STATUS_KEY]: Joi.any(),
  [COUNTRY_OF_ORIGIN_KEY]: Joi.any(),
  [DATE_RANGE_KEY]: Joi.any(),
  [SAVE_BUTTON_NAME]: Joi.any().optional(),
  [ETAG]: Joi.any(),
  [SELECTED_CERTIFICATE_TYPE_KEY]: Joi.any(),
  [DECISION_KEY]: Joi.any()
}

const startDateConfig = {
  day: {
    id: START_DATE_DAY_KEY
  },
  month: {
    id: START_DATE_MONTH_KEY
  },
  year: {
    id: START_DATE_YEAR_KEY
  }
}

const endDateConfig = {
  day: {
    id: END_DATE_DAY_KEY
  },
  month: {
    id: END_DATE_MONTH_KEY
  },
  year: {
    id: END_DATE_YEAR_KEY
  }
}

module.exports = {
  searchSchema,
  startDateConfig,
  endDateConfig,
  preparePayloadWithDates
}