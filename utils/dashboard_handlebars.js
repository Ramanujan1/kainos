const handlebars = require('handlebars')
const constants = require('./constants')

const statusClassMap = {
  [constants.status.SUBMITTED]: 'phase-tag submitted-tag',
  [constants.status.VALIDATED]: 'phase-tag validated-tag',
  [constants.status.REJECTED]: 'phase-tag rejected-tag',
  [constants.controlStatus.REQUIRED]: 'phase-tag required-control-tag',
  [constants.controlStatus.COMPLETED]: 'phase-tag completed-control-tag'
}

const certStatusMap = {
  [constants.status.SUBMITTED]: 'NEW',
  [constants.status.VALIDATED]: 'VALID',
  [constants.status.REJECTED]: 'REJECTED'
}

const controlStatusMap = {
  [constants.controlStatus.REQUIRED]: 'CONTROL REQUIRED',
  [constants.controlStatus.COMPLETED]: 'CONTROL COMPLETE'
}

const registerHelpers = () => {
  handlebars.registerHelper('getStatusClass', status => {
    return statusClassMap[status] || ''
  })

  handlebars.registerHelper('mapCertStatus', status => {
    return certStatusMap[status] || ''
  })

  handlebars.registerHelper('mapControlStatus', status => {
    return controlStatusMap[status] || ''
  })
}

module.exports = {
  registerHelpers
}