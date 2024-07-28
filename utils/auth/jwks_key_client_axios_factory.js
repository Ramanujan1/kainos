const axiosInstance = require('axios').create()

const getInstance = () => axiosInstance

module.exports = {getInstance}
