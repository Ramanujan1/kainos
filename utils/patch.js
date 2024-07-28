const jsonpatch = require('fast-json-patch')

const notificationService = require('../services/notification')
const requestMetadataService = require('../services/request_metadata_service')

/**
 * Takes the notification data and works out the differences(patches) between
 * the original and its updated(performed by fn) version, then finally calls the
 * patch notification service to perform these updates.
 * @param notification - The notification data that is persisted in the database.
 * @param payload - The data that is posted from the HTML form action.
 * @param fn - a function that transforms the payload into the notification data.
 * @param referenceNumber - The referenceNumber for the persisted notification data.
 * @returns Promise - promise of patching notification
 */
const doPatchOperations = async(request, notification, payload, fn, referenceNumber, ...args) => {
  const observer = jsonpatch.observe(notification)
  fn.apply(this, [payload, notification].concat(args))
  const patchOperations = jsonpatch.generate(observer)

  const requestMetadata = requestMetadataService.getRequestMetadata(request)
  const loggedInUser = requestMetadata.loggedInUser
  patchOperations.push({
    op: 'add',
    path: '/lastUpdatedBy',
    value: {
      displayName: loggedInUser.userInfo.displayName,
      userId: loggedInUser.userInfo.id,
      isControlUser: true
    }
  })
  return notificationService.patchNotification(
      request.requestMetaData,
      referenceNumber,
      payload.etag,
      patchOperations)
}

module.exports = {
  doPatchOperations
}
