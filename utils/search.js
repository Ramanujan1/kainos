const datetime = require('../utils/datetime')

const searchDateRange = (dateRange) => {
  let filterStartDate
  let filterEndDate
  if (dateRange) {
    if (dateRange === 'today') {
      filterStartDate = datetime.today()
      filterEndDate = datetime.today()
    }
    if (dateRange === 'yesterday') {
      filterStartDate = datetime.yesterday()
      filterEndDate = datetime.yesterday()
    }
    if (dateRange === 'lastSevenDays') {
      filterStartDate = datetime.lastSevenDays()
      filterEndDate = datetime.today()
    }
    if (dateRange === 'clearRange') {
      filterStartDate = ''
      filterEndDate = ''
    }
    const searchDateRange = {
      filterStartDate: filterStartDate,
      filterEndDate: filterEndDate
    }
    return searchDateRange
  }
}

module.exports = {
  searchDateRange: searchDateRange
}
