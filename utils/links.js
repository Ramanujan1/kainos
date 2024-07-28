const _ = require('lodash')

/**
 * Retrieves and URI encodes the certificate ID from the request URL otherwise
 * returns an empty string.
 * @param request, HTTP request
 * @returns string
 */
const getURIEncodedCertificateID = (request) => {
  return encodeURIComponent(_.get(request, 'params.id', ''))
}

module.exports = {
  getURIEncodedCertificateID: getURIEncodedCertificateID
}
