const axiosFactory = require('./http_client_axios_factory')
const https = require('https')
const Boom = require('boom')
const axiosRetry = require('axios-retry')
const _ = require('lodash')
const winston = require('winston')
const logger = winston.loggers.get('logger')
const unescape = require('unescape')
const config = require('getconfig')

axiosRetry(axiosFactory.getInstance(), {
  retries: 3,
  retryDelay: config.node.env === 'test' ? undefined : axiosRetry.exponentialDelay
})

const agentOptions = {
  keepAlive: true,
  maxSockets: Infinity,
  keepAliveMsecs: 3000
}
const httpsAgent = new https.Agent(agentOptions)

const handleError = err => {
  logError(err)
  throw new Boom(err, {
    statusCode: _.get(err, 'response.status', 500)
  })
}
const logError = (err) => {
  logger.error(
      err.message,
      _.get(err, 'request._currentUrl'),
      _.get(err, 'request._options.headers')
  )
}

axiosFactory.getInstance().interceptors.request.use(function (config) {
  // before request is sent
  if (_.hasIn(config, 'axios-retry')) {
    logger.warn(JSON.stringify(_.pick(config, ['url', 'axios-retry'])))
  }
  return config
})

function deepUnescapeJSON(object) {
  for(let propName in object) {
    let val = object[propName]

    if(typeof(object[propName]) === 'string') val = unescape(object[propName])

    if(typeof(object[propName]) === 'object') val = deepUnescapeJSON(object[propName])

    object[propName] = val
  }
  return object
}

/**
 * Creates the same responses as the request library. For compatibility.
 * @param response
 * @returns {{}}
 */
const toCommonResponse = response => {
  // Some times we get an object other times we get just an id number.
  if (!_.isObject(response.data) || response.data instanceof Buffer) {
    return response.data
  }
  const commonResponse =  {...response.data}
  const etag = _.get(response, 'headers.etag')
  if (!_.isNil(etag)) {
    commonResponse.etag = etag
  }
  return deepUnescapeJSON(commonResponse)
}

const request = (verb, options) => {
  const config = {
    method: verb,
    httpsAgent: httpsAgent,
    ...options
  }
  logger.info(`making http-request: ${config.method} ${config.url}`)
  return axiosFactory.getInstance()(config)
      .then(toCommonResponse)
      .catch(handleError)
}

const GET = (options) => {
  return request('get', options)
}

const POST = (options) => {
  return request('post', options)
}

const PATCH = (options) => {
  return request('patch', options)
}

const PUT = (options) => {
  return request('put', options)
}

module.exports = {
  GET: GET,
  PATCH: PATCH,
  POST: POST,
  PUT: PUT
}
