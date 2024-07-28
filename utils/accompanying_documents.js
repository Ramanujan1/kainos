const _ = require('lodash')

const fieldConfigUtils = require('./fieldconfig')
const constants = require('./constants')

const TRACES_ACCOMPANYING_DOCUMENT_PAGE = 'References'
const TRACES_ACCOMPANYING_DOCUMENT_QUERY = ['a_vedonu0']

const hasAccompanyingDocuments = fieldConfig => {
  return fieldConfigUtils.getField(fieldConfig,
      TRACES_ACCOMPANYING_DOCUMENT_PAGE,
      TRACES_ACCOMPANYING_DOCUMENT_QUERY) !== null
}

const getAccompanyingDocuments = payload => _.compact(_.values(_.pickBy(payload,
    (value, key) => constants.ACCOMPANYING_DOCUMENT_NUMBER_REGEX.test(key))))

const addAccompanyingDocument = (payload, notification) => {
  const accompanyingDocumentNumbers = getAccompanyingDocuments(payload)
  if (accompanyingDocumentNumbers.length < constants.MAX_ACCOMPANYING_DOCUMENTS) {
    accompanyingDocumentNumbers.push('')
  }

  if (!notification.partOne) {
    notification.partOne = {}
  }

  if (!notification.partOne.veterinaryInformation) {
    notification.partOne.veterinaryInformation = {}
  }
  notification.partOne.veterinaryInformation.accompanyingDocumentNumbers = accompanyingDocumentNumbers
}

const removeAccompanyingDocument = (payload, key) => {
  const accompanyingDocumentKey = `accompanying-document-${payload[key]}`
  delete payload[accompanyingDocumentKey]
}

module.exports = {
  hasAccompanyingDocuments: hasAccompanyingDocuments,
  getAccompanyingDocuments: getAccompanyingDocuments,
  addAccompanyingDocument: addAccompanyingDocument,
  removeAccompanyingDocument: removeAccompanyingDocument
}