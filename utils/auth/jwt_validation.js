const _ = require('lodash')
const jwtInformationHelper = require('./jwt_information')

/* eslint-disable no-unused-vars */
const validate = async (decoded, request) => {
/* eslint-enable no-unused-vars */
  let jwtInformation = jwtInformationHelper.getJwtInformation()
  jwtInformation = _.filter(jwtInformation, {issuer: decoded.iss})
  jwtInformation = _.filter(jwtInformation, {audience: decoded.aud})

  const isValid = jwtInformation.length > 0

  return {isValid: isValid}
}

module.exports = {validate}
