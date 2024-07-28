const fs = require('fs')
const Path = require('path')
const Handlebars = require('handlebars')

const userManager = require('../services/user_manager')
const dashboardUrlHelper = require('./dashboard_url_helper')

const propositionHeaderPartialPath = ['..', 'views', 'partials', 'common', 'headers', 'propositionHeader.html']

const loadPartial = pathArray => {
  return String(fs.readFileSync((Path.join(__dirname, ...pathArray))))
}

const compilePartial = partialContent => {
  return Handlebars.compile(partialContent)
}

const compilePropositionHeaderPartial = () => {
  const partialContent = loadPartial(propositionHeaderPartialPath)
  return compilePartial(partialContent)
}

const propositionNavHeader = compilePropositionHeaderPartial()

const createViewContextForPropositionHeader = request => {
  const dashboardUrl = dashboardUrlHelper.getDashboardUrl(request)
  const hasNotificationReadPermission = userManager.loggedInUserHasPart1ReadPermission(request)
  const hasDecisionReadPermission = userManager.loggedInUserHasPart2ReadPermission(request)
  const hasNotificationOrDecisionReadPermission = hasDecisionReadPermission || hasNotificationReadPermission
  const partialViewModel = {
    dashboardUrl,
    hasNotificationReadPermission,
    hasDecisionReadPermission,
    hasNotificationOrDecisionReadPermission
  }
  const renderedPartial = propositionNavHeader(partialViewModel)
  return {
    propositionHeader: renderedPartial
  }
}

module.exports = {
  createViewContextForPropositionHeader
}
