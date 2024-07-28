const _ = require('lodash')

const constants = require('./constants')

const completed = data => {

  if (_.isEmpty(data)) {
    return false
  }

  const commodities = _.get(data, 'partOne.commodities', null)
  const commodityComplement = _.get(data, 'partOne.commodities.commodityComplement', []) || []
  const purpose = _.get(data, 'partOne.purpose', null)

  const isCompleted = commodityComplement.length > 0
    && _.has(commodities, 'countryOfOrigin') && _.has(data, 'partOne.purpose.purposeGroup')
    && !_.isEmpty(purpose)

  if (constants.CVEDA !== data.type) {
    return isCompleted && _.has(commodities, 'consignedCountry')
  }

  return isCompleted
}

module.exports = {
  completed: completed
}