const axiosFactory = require('./jwks_key_client_axios_factory')
const https = require('https')
const httpsAgent = new https.Agent()
const winston = require('winston')
const logger = winston.loggers.get('logger')
const Boom = require('boom')
const _ = require('lodash')

const fetchWellKnowns = async url => {
  return axiosFactory.getInstance()({method: 'GET', url: url, httpsAgent: httpsAgent})
      .then(handleResponse)
      .catch(handleError)
}

const fetchJwks = async wellKnownsObj => {
  if (!_.has(wellKnownsObj, 'jwks_uri') || _.isEmpty(wellKnownsObj['jwks_uri'])) {
    logger.error('The .wellknowns do not contain a jwks_uri')
    throw Boom.internal('Unable to validate user credentials')
  }
  return axiosFactory.getInstance()({method: 'GET', url: wellKnownsObj['jwks_uri'], httpsAgent: httpsAgent})
      .then(handleResponse)
      .catch(handleError)
}

const handleResponse = response => {
  return response.data
}

const handleError = error => {
  logger.error('Error occurred while fetching new keys', error)
  throw Boom.internal('Unable to validate user credentials')
}

module.exports = {
  fetchWellKnowns,
  fetchJwks
}
