const _ = require('lodash')
const Boom = require('boom')
const envProvider = require('./../utils/env_provider')

const getJwtInformation = () => {
  const JWT_WELL_KNOWNS = envProvider.getJwtWellKnowns()
  const JWT_CERT_ISSUER = envProvider.getJwtIssuers()
  const JWT_AUDIENCE = envProvider.getJwtAudience()
  validateJwtEnvVars(JWT_WELL_KNOWNS, JWT_CERT_ISSUER, JWT_AUDIENCE)
  const jwtWellKnowns = _.split(JWT_WELL_KNOWNS, ',')
  const jwtCertIssuers = _.split(JWT_CERT_ISSUER, ',')
  const jwtAudiences = _.split(JWT_AUDIENCE, ',')

  if (!(jwtWellKnowns.length === jwtCertIssuers.length && jwtCertIssuers.length === jwtAudiences.length)) {
    throw Boom.internal('The JWT_AUDIENCE, JWT_CERT_ISSUER, JWT_WELL_KNOWNS environment variables are not the same length')
  }

  const jwtInformation = []
  for (let i = 0; i < jwtWellKnowns.length; i++) {
    jwtInformation.push({
      issuer: jwtCertIssuers[i],
      wellKnown: jwtWellKnowns[i],
      audience: jwtAudiences[i]
    })
  }

  return jwtInformation
}

const validateJwtEnvVars = (jwtWellKnowns, jwtCertIssuer, jwtAudience) => {

  if (_.isUndefined(jwtWellKnowns) || _.isEmpty(jwtWellKnowns)) {
    throw Boom.internal('The JWT_WELL_KNOWNS environment variable must be defined')
  }

  if (_.isUndefined(jwtCertIssuer) || _.isEmpty(jwtCertIssuer)) {
    throw Boom.internal('The JWT_CERT_ISSUER environment variable must be defined')
  }

  if (_.isUndefined(jwtAudience) || _.isEmpty(jwtAudience)) {
    throw Boom.internal('The JWT_AUDIENCE environment variable must be defined')
  }
}

module.exports = {getJwtInformation}