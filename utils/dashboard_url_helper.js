const _ = require('lodash')

const {user, IMPORTER_URL_PATH, BIP_URL_PATH, OV_URL_PATH, FSA_URL_PATH} = require('./constants')
const userManagerService = require('../services/user_manager')

const dashboardMap = Object.freeze({
  [user.role.B2C_NOTIFIER]: IMPORTER_URL_PATH,
  [user.role.B2C_INSPECTOR]: BIP_URL_PATH,
  [user.role.B2B_INSPECTOR]: BIP_URL_PATH,
  [user.role.B2B_COMPETENT_AUTHORITY]: BIP_URL_PATH,
  [user.role.B2B_CONTROL_AUTHORITY]: OV_URL_PATH,
  [user.role.B2B_REENFORCEMENT_CONTROL]: FSA_URL_PATH
})

const getDashboardUrl = request => {
  const role = userManagerService.getLoggedInUserRole(request) || ''
  return _.get(dashboardMap, role.toLowerCase(), IMPORTER_URL_PATH)
}

module.exports = {
  getDashboardUrl
}
