const CACHE_REFRESH_SECONDS = 1200

const NodeCache = require('node-cache')
const keyCache = new NodeCache(
    {
      stdTTL: CACHE_REFRESH_SECONDS,
      checkPeriod: 0 // don't run background checks, expire and remove on get
    }
)

/**
 * @returns {NodeCache} a cache singleton shared across all continuations
 */
const getCache = () => {
  return keyCache
}

module.exports = {getCache}