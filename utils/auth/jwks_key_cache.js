const Boom = require('boom')
const _ = require('lodash')
const winston = require('winston')
const logger = winston.loggers.get('logger')
const jwksKeyClient = require('./jwks_key_client')
const jwkToPem = require('jwk-to-pem')
const jwtInformationHelper = require('./jwt_information')

const cacheFactory = require('./jwks_node_cache_factory')

// key lookup function for use with hapi-auth-jwt2 module
const keyLookup = async decoded => {

  if (_.isEmpty(decoded.payload.iss)) {
    throw Boom.internal('Could not determine certificate issuer')
  }

  const keys = await scanProviders(decoded.header.kid, decoded.payload.aud, decoded.payload.iss)
  if (_.isEmpty(keys)) {
    logger.error('The jwks response contained no keys')
    throw Boom.internal('Unable to validate user credentials')
  }
  return {key: keys}
}

const scanProviders = async (kid, decodedAud, issuer) => {

  const jwtInformation = jwtInformationHelper.getJwtInformation()

  for (let jwtInfo of jwtInformation) {
    const keys = await getJwksKey(jwtInfo, kid, decodedAud, issuer)
    if (!_.isEmpty(keys)) {
      return keys
    }
  }

  return []
}

const getJwksKey = async (jwtInformation, kid, decodedAud, issuer) => {
  const wellKnownUrl = jwtInformation.wellKnown
  let keys = cacheFactory.getCache().get(wellKnownUrl)

  if (!keys) {
    const wellKnownsObj = await jwksKeyClient.fetchWellKnowns(wellKnownUrl)
    const jwks = await jwksKeyClient.fetchJwks(wellKnownsObj)
    const wellKnownIssuer = wellKnownsObj.issuer
    if (wellKnownIssuer === jwtInformation.issuer && !_.isEmpty(jwks)) {
      const keysMatchingKid = jwks.keys.filter(key => key.kid === kid && jwtInformation.audience === decodedAud)
      if (!_.isEmpty(keysMatchingKid)) {
        keys = [jwkToPem(keysMatchingKid[0])]
        cacheFactory.getCache().set(wellKnownUrl, keys, () => {})
      }
    } else {
      return []
    }

  } else {
    //Verify cached key
    if (!(jwtInformation.issuer === issuer && jwtInformation.audience === decodedAud)) {
      return []
    }
  }

  return keys
}

module.exports = {keyLookup}
