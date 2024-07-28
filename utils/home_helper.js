const getPagingConfig = (currentPage, pagingUrl) => {
  return {
    currentPage: currentPage,
    previousPage: currentPage - 1,
    nextPage: currentPage + 1,
    pagingUrl: `/protected/${pagingUrl}`,
    anchor: '#existing-notifications'
  }
}

module.exports = {
  getPagingConfig : getPagingConfig
}