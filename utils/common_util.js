const constants = require('./constants')
const countries = require('i18n-iso-countries')

const getQueryParam = fromImporterReview =>
    fromImporterReview ? `?${constants.FROM_IMPORTER_REVIEW_TRUE}` :''

const getCountry = isoCode => {
  isoCode = isoCode === 'notset' ? '' : isoCode
  if (isoCode) {
    return countries.getName(isoCode, 'en') || ''
  } else {
    return ''
  }
}

module.exports = {
  getQueryParam,
  getCountry
}