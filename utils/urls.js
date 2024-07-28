const requestUtils = require('./request_utils')

const getManagementReturnUrl = request => {
  const base = request.path
  const query = requestUtils.stringifyQueryParams(request.query)
  return encodeURIComponent(`${base}${query ? '?' + query : ''}`)
}

module.exports = {
  getManagementReturnUrl
}
