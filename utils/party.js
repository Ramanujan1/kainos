const _ = require('lodash')
const organisationAddress = require('./constants').organisationAddress

const getParty = loggedInUserInfo => {
  return loggedInUserInfo ?
    {
      name: `${loggedInUserInfo.firstName} ${loggedInUserInfo.lastName}`,
      email: loggedInUserInfo.email,
      phone: loggedInUserInfo.phone,
      companyName: loggedInUserInfo.companyDisplayName,
      fax: loggedInUserInfo.fax,
      address: loggedInUserInfo.address,
      country: 'GB',
      companyId: loggedInUserInfo.companyId,
      contactId: loggedInUserInfo.id,
      tracesID: 1001,
      addressObject: loggedInUserInfo.organisations ?
        _.get(loggedInUserInfo, 'organisations[0].organisationAddress')
        : organisationAddress,
    } : {}
}

const getStreet = addressObject => {
  const streetArray = [
    _.get(addressObject, 'buildingName'),
    _.get(addressObject, 'subBuildingName'),
    _.get(addressObject, 'premises'),
    _.get(addressObject, 'locality'),
    _.get(addressObject, 'street')
  ]

  return streetArray.filter(x => _.isString(x) && x.length > 0).join(', ')
}

module.exports = {
  getParty,
  getStreet
}