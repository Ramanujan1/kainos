const _ = require('lodash')

const constants = require('./constants')
const {INSPECTOR} = constants.userRole

const userManager = require('../services/user_manager')

const pageToReturn = options => {
  if (shouldReturnHubPage(options.request.payload)) {
    const role = userManager.getLoggedInUserRole(options.request)
    switch (role) {
    case constants.userRole.IMPORTER:
      return constants.IMPORTER_OVERVIEW_PAGE(options.referenceNumber)
    case constants.userRole.INSPECTOR:
      return constants.BIP_HUB_PAGE(options.referenceNumber)
    case constants.userRole.VETERINARIAN:
    default:
      return '/'
    }
  } else if (shouldReturnBipOverview(options.request)) {
    return constants.BIP_OVERVIEW_PAGE(options.referenceNumber)
  } else if (shouldReturnCurrentPage(options.request.payload)) {
    return options.currentPage
  }

  if( !_.isUndefined(_.get(options.request,'payload.source')) ) {
    return `${options.nextPage}?source=${_.get(options.request, 'payload.source')}`
  }
  return options.nextPage
}

const shouldReturnCurrentPage = payload => {
  return _.hasIn(payload, 'setInProgress')
    || _.hasIn(payload, 'addContainerSeal')
    || _.hasIn(payload, 'removeContainerSeal')
}

const shouldReturnHubPage = payload => {
  return !_.isEmpty(payload)
    && constants.SAVE_BUTTON_NAME in payload
    && constants.SAVE_AND_RETURN_BUTTON_VALUE === payload[constants.SAVE_BUTTON_NAME]
}

const shouldReturnBipOverview = request => {
  return !_.isEmpty(request.payload)
    && constants.SAVE_BUTTON_NAME in request.payload
    && constants.SAVE_AND_REVIEW_BUTTON_VALUE === request.payload[constants.SAVE_BUTTON_NAME]
    && userManager.getLoggedInUserRole(request) === INSPECTOR
}

module.exports = {
  pageToReturn
}
