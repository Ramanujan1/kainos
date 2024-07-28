const headers = new Map()
headers.set('X-XSS-Protection', '1; mode=block')
headers.set('X-Frame-Options', 'deny')
headers.set('X-Content-Type-Options', 'nosniff')
headers.set('Strict-Transport-Security', 'max-age=31536000; includeSubDomains')
headers.set('Referrer-Policy', 'no-referrer')
headers.set('Cache-Control', 'no-store, no-cache, must-revalidate, max-age=0')
//Content-Security-Policy has also been set in import-proxy repo and its overwrite this setting
headers.set('Content-Security-Policy','default-src \'self\'; style-src \'self\'; font-src \'self\' data:; script-src \'self\';')

const getSecurityHeaders = (request) => {
  let cacheControlHeader = 'no-store, no-cache, must-revalidate, max-age=0'
  if (request && request.path.includes('public/')) {
    cacheControlHeader = 'max-age=31536000'
  }
  headers.set('Cache-Control', cacheControlHeader)

  return headers
}

module.exports = {
  getSecurityHeaders: getSecurityHeaders
}