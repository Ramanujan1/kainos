const _ = require('lodash')
const moment = require('moment')

const DATE_FMT = 'YYYY-M-D'
const DATE_FMT_SHORT = 'YY-M-D'
const SHORT_READABLE_DATE_FMT = 'DD MM YYYY'
const DATE_FMT_ISO = 'YYYY-MM-DD'
const READABLE_DATE_FMT = 'DD MMM YYYY'
const READABLE_DATE_TIME_FMT = 'DD MMM YYYY, HH:mm'
const TIME_FMT = 'H:m'
const FULL_READABLE_DATE_FMT = 'Do MMMM YYYY'
const DISPLAYABLE_DATE_FMT = 'DD/MM/YYYY'
const MINUTES_IN_A_DAY = 1440
const MINUTES_IN_AN_HOUR = 60
const DATE_TIME_WITH_UTC_OFFSET = 'YYYY-MM-DDTHH:mm:ss.SSSSZ'
const DATE_SEARCH_FMT = 'YYYY-MM-DD'
const ZERO = '0'

const getDate = dateStr => {
  let d = moment(dateStr, [DATE_FMT, DATE_FMT_SHORT], true)
  return d.isValid() ? d.format('DD') : ''
}

const getMonth = dateStr => {
  let d = moment(dateStr, [DATE_FMT, DATE_FMT_SHORT], true)
  return d.isValid() ? d.format('MM') : ''
}

const getYear = dateStr => {
  let d = moment(dateStr, [DATE_FMT, DATE_FMT_SHORT], true)
  return d.isValid() ? d.format('YYYY') : ''
}

const getHour = dateStr => {
  let d = moment(dateStr, TIME_FMT)
  return d.isValid() ? d.format('HH') : ''
}

const getMin = dateStr => {
  let d = moment(dateStr, TIME_FMT)
  return d.isValid() ? d.format('mm') : ''
}

/**
 * Returns a formatted time given hour and minute otherwise returns null.
 * @param hour, string "23"
 * @param minute, string "59"
 * @returns {string|null}, "23:59" or null
 */
const getTime = (hour, minute) => {
  const result = formatTime(hour + ':' + minute)
  return result === '' ? null : result
}

const getTimeInRFC3339Format = (hour, minute) => {
  const result = formatTime(hour + ':' + minute)
  return result === '' ? null : result + ':00'
}

const formatDate = dateStr => {
  let d = moment(dateStr, DATE_FMT)
  return d.isValid() ? d.format(READABLE_DATE_FMT) : ''
}

const formatDateFull = dateStr => {
  let d = moment(dateStr, DATE_FMT)
  return d.isValid() ? d.format(FULL_READABLE_DATE_FMT) : ''
}

const formatDateForDisplay = dateStr => {
  let d = moment(dateStr, DATE_FMT)
  return d.isValid() ? d.format(DISPLAYABLE_DATE_FMT) : ''
}

const formatDateToRequired = (date, format) => {
  let d = moment(date, DATE_FMT)
  return d.isValid() ? d.format(format) : ''
}

const formatDateShort = dateStr => {
  let d = moment(dateStr, DATE_FMT_ISO)
  return d.isValid() ? d.format(SHORT_READABLE_DATE_FMT) : ''
}

const formatTimestamp = timestamp => {
  let d = moment(timestamp)
  return d.isValid() ? d.format(READABLE_DATE_FMT) : ''
}

const formatTime = time => {
  let t = moment(time, TIME_FMT)
  return t.isValid() ? t.format('HH:mm') : ''
}

const formatDateAndTime = dateTime => {
  let dt = moment.utc(dateTime, DATE_TIME_WITH_UTC_OFFSET)
  return dt.isValid() ? dt.format(READABLE_DATE_TIME_FMT) : ''
}

const getCurrentYear = () => {
  return moment().format('YYYY')
}

const validateDate = (year, month, day) => {

  // Empty values are allowed.
  if (_.isEmpty(year) && _.isEmpty(month) && _.isEmpty(day)) {
    return true
  }

  // White space is allowed.
  if (_.reduce([year, month, day], _.trim) === '') {
    return true
  }

  // Values must be positive integers.
  if (!_.every([year, month, day], elem => _.toInteger(elem) > 0)) {
    return false
  }

  // Given 1970-1-2 returns 1970-01-02 (see DATE_FMT_ISO).
  const padded = () => _.reduce([year, month, day], (result, elem) => _.join([result, _.padStart(elem, 2, '0')], '-'))
  return moment(padded(), DATE_FMT_ISO, true).isValid()
}

const validateStartEndDate = (startDate, endDate) => {
  let mStart = moment(startDate)
  let mEnd = moment(endDate)
  return mStart.isAfter(mEnd)
}

/**
 * Transforms year, month and day strings into a Javascript Date object.
 * @param year A string corresponding to the year.
 * @param month A string corresponding to the month (indexed starting from 0).
 * @param day A string corresponding to the day.
 * @returns A Javascript Date object, or undefined if the args cannot be
 * parsed to a valid date.
 */
const parseDate = (year, month, day) => {
  const dt = moment([year, month, day])
  return dt.isValid() ? dt.toDate() : undefined
}

/**
 * Parses the fields and returns a string in ISO8601 format ('yyyy-mm-dd').
 * Expects values as a human would input them. Months start at 1.
 * @param year, string, "1970"
 * @param month, string, "12" January=1
 * @param day, string, "30"
 * @returns {string|null}, date string or null, '1970-12-30'
 */
const toIsoDate = (year, month, day) => {
  const parse = (year, month, day) => {
    const dt = moment([year, month, day])
    return dt.isValid() ? dt.format(DATE_FMT_ISO) : null
  }
  return parse(year, parseInt(month) - 1, day)
}

/**
 * Parses payload by prefix and returns string in ISO8601 format ('yyyy-mm-dd').
 * Expects values as a human would input them.
 * @param payload, object, {'prefix-year': '1900', 'prefix-month': '10', 'prefix-day': '1' }
 * @param prefix, string
 * @returns {string}
 */
const getIsoDateByPrefix = (payload, prefix) => {
  const list = ['-year', '-month', '-day'].map(x => {
    return payload[prefix + x]
  })
  return toIsoDate(list[0], list[1], list[2])
}

const minutesToDaysHoursAndMinutes = totalTimeInMinutes => {
  if(totalTimeInMinutes != undefined) {

    var days = _.parseInt(totalTimeInMinutes / MINUTES_IN_A_DAY)
    var hours = _.parseInt((totalTimeInMinutes - (days * MINUTES_IN_A_DAY)) / MINUTES_IN_AN_HOUR)
    var minutes = totalTimeInMinutes - ((days * MINUTES_IN_A_DAY) + (hours * MINUTES_IN_AN_HOUR))

    return {
      days: days,
      hours: hours,
      minutes: minutes
    }
  }
}

const daysHoursMinutesToMinutes = (days, hours, minutes) => {
  return (days * MINUTES_IN_A_DAY) + (hours * MINUTES_IN_AN_HOUR) + minutes
}

const prefixYear = year => {
  return year && year.length == 2 ? '20' + year : year
}

const prefixDay = date => {
  return date && date.length == 1 ? ZERO + date : date
}

const prefixMonth = month => {
  return month && month.length == 1 ? ZERO + month : month
}

const today = () => {
  return moment().format(DATE_SEARCH_FMT)
}

const nextDay = () => {
  return moment().add(1, 'days').format(DATE_SEARCH_FMT)
}

const nextSevenDays = () => {
  return moment().add(7, 'days').format(DATE_SEARCH_FMT)
}

const lastSevenDays = () => {
  return moment().subtract(7, 'days').format(DATE_SEARCH_FMT)
}

const yesterday = () => {
  return moment().subtract(1, 'days').format(DATE_SEARCH_FMT)
}

module.exports = {
  getDate: getDate,
  getMonth: getMonth,
  getYear: getYear,
  getCurrentYear: getCurrentYear,
  getHour: getHour,
  getMin: getMin,
  getTime: getTime,
  formatDate: formatDate,
  formatDateFull: formatDateFull,
  formatDateForDisplay: formatDateForDisplay,
  formatDateShort: formatDateShort,
  formatTimestamp: formatTimestamp,
  formatTime: formatTime,
  formatDateAndTime: formatDateAndTime,
  validateDate: validateDate,
  validateStartEndDate: validateStartEndDate,
  parseDate: parseDate,
  toIsoDate: toIsoDate,
  formatDateToRequired: formatDateToRequired,
  getIsoDateByPrefix: getIsoDateByPrefix,
  getTimeInRFC3339Format,
  minutesToDaysHoursAndMinutes,
  daysHoursMinutesToMinutes,
  MINUTES_IN_A_DAY,
  MINUTES_IN_AN_HOUR,
  prefixYear: prefixYear,
  today: today,
  nextDay: nextDay,
  nextSevenDays: nextSevenDays,
  prefixDay: prefixDay,
  prefixMonth: prefixMonth,
  lastSevenDays,
  yesterday
}
