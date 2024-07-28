const _ = require('lodash')
const staticData = require('../data/static.json')
const constants = require('./constants')

/**
 * 1. Read the 'commodity' stored within the notification.
 * 2. Find the first 'commodity complement' from the array.
 * 3. Return the commodityID of the first element.
 * @param notification
 * @returns The string id of the commodity, or null if none can be read.
 */
const getCommodityID = notification => {
  return _.get(getCommodityComplement(notification), 'commodityID', null)
}

const getCommodityComplement = notification => {
  const commodity = _.get(notification, 'partOne.commodities', {})
  return _.head(_.get(commodity, 'commodityComplement', []))
}

const getBIP = (bipCode, notificationType, defaultValue = 'n/a') => {
  if (bipCode === '') {
    return defaultValue
  }
  const bipList = staticData.DPE.location.options
  return _.get(_.find(bipList, {value: bipCode}), 'text', defaultValue)
}

const submitNotification = notification => {
  if (notification.status === constants.status.DRAFT) {
    _.set(notification, 'version', 1)
  }
  _.set(notification, 'status', constants.status.SUBMITTED)
}

const amendNotification = notification => {
  if (notification.status === constants.status.SUBMITTED) {
    _.set(notification, 'status', constants.status.AMEND)
    updateNotificationVersion(notification)
  }
}

const modifyNotification = notification => {
  if (notification.status === constants.status.MODIFY) {
    return
  }
  _.set(notification, 'status', constants.status.MODIFY)
  updateNotificationVersion(notification)
}

const updateNotificationVersion = notification => {
  let version = _.get(notification, 'version')
  if (!_.isNil(version)) {
    _.set(notification, 'version', ++version)
  }
}

const getNotification = request => {
  return _.get(request, 'pre.notification')
}

const notificationUpdateError = () => {
  return {
    error: {
      title: 'Error updating import notification',
      message: 'Please try again, another person has made changes at the same time.'
    }
  }
}

module.exports = {
  getCommodityID,
  getBIP,
  submitNotification,
  amendNotification,
  modifyNotification,
  getNotification,
  notificationUpdateError
}
