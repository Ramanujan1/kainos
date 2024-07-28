const stringifyQueryParams = params => {
  if (!params) {
    return
  }
  let queryParamString = ''
  for (const key in params) {
    queryParamString = `${queryParamString}&${key}=${params[key]}`
  }
  return queryParamString.substring(1)
}

const getConversationIdAndAuthHeaders = requestMetadata => {
  return {
    'INS-ConversationId': requestMetadata.loggedInUser.conversationId,
    'Authorization': `Bearer ${requestMetadata.loggedInUser.accessToken}`
  }
}

const conversationIdAndAuthorisationHeaders = (conversationId,
  accessToken) => {
  return {
    'INS-ConversationId': conversationId,
    'Authorization': `Bearer ${accessToken}`
  }
}

const conversationIdXAuthBasicAndAuthorisationHeaders = (conversationId, accessToken, basicAuthHeaderValue) => {
  return {
    'INS-ConversationId': conversationId,
    'x-auth-basic': basicAuthHeaderValue,
    'Authorization': 'Bearer ' + accessToken
  }
}

module.exports = {
  stringifyQueryParams,
  conversationIdXAuthBasicAndAuthorisationHeaders,
  getConversationIdAndAuthHeaders,
  conversationIdAndAuthorisationHeaders
}
