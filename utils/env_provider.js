const config = require('getconfig')

const JWT_AUDIENCE = config.authentication.jwtClientId
const JWT_CERT_ISSUER = config.authentication.jwtCertIssuer
const JWT_WELL_KNOWNS = config.authentication.jwtWellKnowns

/**
 Encapsulates environment variable access behind functions so that modules using
 environment variables can be tested without having to reload the module.
 */

const getJwtIssuers = () => {
  return JWT_CERT_ISSUER
}

const getJwtWellKnowns = () => {
  return JWT_WELL_KNOWNS
}

const getJwtAudience = () => {
  return JWT_AUDIENCE
}

module.exports = {
  getJwtIssuers,
  getJwtWellKnowns,
  getJwtAudience
}
