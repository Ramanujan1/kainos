const aesjs = require('aes-js')
const config = require('getconfig')
const cookieNames = require('./cookie_names')

const encryptionKey = aesjs.utils.hex.toBytes(config.hexEncryptionKey)

const getState = request => {
  return request.state
}

const setAccessCookie = (request, data) => {
  request.cookieAuth.set({ data: encrypt(data) })
}

const setUserInfoCookie = (h, data, request) => {
  const encrypted = encrypt(data)
  h.state(cookieNames.USER_INFO, encrypted)
  if (request) {
    request.state[cookieNames.USER_INFO] = encrypted
  }
}

const getUserInfoCookie = request => {
  const userInfo = getState(request).userInfoCookie
  if (userInfo) {
    return decrypt(userInfo)
  }
}

const getAccessCookie = request => {
  const notificationAccess = getState(request).notificationAccess
  if (notificationAccess) {
    return decrypt(notificationAccess.data)
  } else {
    return undefined
  }
}

const encrypt = data => {
  const dataBytes = aesjs.utils.utf8.toBytes(JSON.stringify(data))
  const encryptedBytes = getNewAesCounterInstance().encrypt(dataBytes)
  return aesjs.utils.hex.fromBytes(encryptedBytes)
}

const decrypt = hex => {
  const encryptedBytes = aesjs.utils.hex.toBytes(hex)
  const decryptedBytes = getNewAesCounterInstance().decrypt(encryptedBytes)
  return JSON.parse(aesjs.utils.utf8.fromBytes(decryptedBytes))
}

const getNewAesCounterInstance = () => {
  return new aesjs.ModeOfOperation.ctr(encryptionKey, new aesjs.Counter(5))
}

module.exports = {
  getState,
  setAccessCookie,
  getAccessCookie,
  getUserInfoCookie,
  setUserInfoCookie
}
