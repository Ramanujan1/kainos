const handlebars = require('handlebars')
const _ = require('lodash')

const CVEDA_CVEDP_PATH = 'complementparameterset/[0-9]*/keydatapair/[0-9]*/[0-9]*/'
const CED_PATH = 'complementparameterset/[0-9]*/keydatapair/[0-9]*/'

const COMPLEMENT_PARAMETER_MATCH = 'complementparameterset/[0-9]*/'
const COMPLEMENT_PARAMETER_CED_MATCH = 'complementparameterset/[0-9]*/keydatapair/[0-9]*/[[a-zA-Z]'

const slash = '/'

// map errors for more friendly array of objects to serve on front
const mapErrors = validationResult => {
  if (validationResult.error) {
    return validationResult.error.details.map(detail => {
      let path = detail.path
      if (path.length === 0) {
        path = [_.get(detail, 'context.peers[0]')]
      }
      return {
        description: detail.message,
        field: path
      }
    }) || []
  }
}

/**
 * Extract errors from a Notification and return as an array of error objects.
 * @param consignmentValidationErrors An array of error objects.
 */
const mapConsignmentValidationErrors = (consignmentValidationErrors) => {
  if (!consignmentValidationErrors) {
    return []
  }
  let containers = []
  sortAndBuildValidationErrorsList(containers, consignmentValidationErrors )
  return containers.map(e => {
    let arr = e.field.split(slash)
    let validationFieldName = arr.pop()
    let validationFieldNameSplit = arr.pop()
    let shortName = validationFieldNameSplit.concat(slash).concat(validationFieldName)
    let description = ''
    shortName = mapComplementParameter(e, arr, shortName)
    if(!isNaN(e.message)){
      description = shortName
    } else {
      description = e.message
    }
    return {
      field: [shortName],
      description: description
    }
  })
}

const sortAndBuildValidationErrorsList = (containers, consignmentValidationErrors) => {
  consignmentValidationErrors.sort((a,b)=>a.field > b.field?1:((b.field > a.field) ? -1 : 0))
  let count = 0
  for (let consignmentValidationError of consignmentValidationErrors) {
    if (consignmentValidationError.field.match(CVEDA_CVEDP_PATH)
      || consignmentValidationError.field.match(CED_PATH)) {
      if (count < 2) {
        containers.push(consignmentValidationError)
        count++
      }
      continue
    }
    containers.push(consignmentValidationError)
  }
}

const mapComplementParameter = (e, arr, shortName) => {
  if (e.field.match(COMPLEMENT_PARAMETER_MATCH)) {
    if (e.field.match(COMPLEMENT_PARAMETER_CED_MATCH)) {
      let complementID = arr.pop()
      shortName = complementID.concat(slash).concat(shortName)
    } else {
      let speciesID = arr.pop()
      let complementID = arr.pop()
      shortName = complementID.concat(slash).concat(
          speciesID).concat(slash).concat(shortName)
    }
  }
  return shortName
}

// Are there any errors
const isErrors = (errors) => {
  return !_.isNil(errors) && errors.length > 0
}
// Is there a specific error
const isError = (errors, field) => {
  return !_.isNil(errors) && errors.filter(error => error.field[0] === field).length > 0
}
// Get an error description
const getErrors = (errors, field) => {
  let errorsFound = !_.isNil(errors) && errors.filter(error => error.field[0] === field)[0]
  return errorsFound ? errorsFound.description : ''
}

const isDateErrors = (errors, searchKey) => {
  return !_.isNil(errors) && errors.filter(error => error.field[0].includes(searchKey)).length
    > 0
}

const isTimeErrors = (errors, searchKey) => {
  return !_.isNil(errors) && errors.filter(error => error.field[0].includes(searchKey)).length
    > 0
}

const registerHelpers = () => {
  handlebars.registerHelper('isErrors', function (errors, options) {
    if (isErrors(errors)) {
      return options.fn(this)
    } else {
      return options.inverse(this)
    }
  })

  handlebars.registerHelper('isError', function (errors, field, options) {
    if (isError(errors, field)) {
      return options.fn(this)
    } else {
      return options.inverse(this)
    }
  })
  handlebars.registerHelper('getErrors', getErrors)

  handlebars.registerHelper('displayDateErrorClass', function (errors, searchKey) {
    return isDateErrors(errors, searchKey) ? 'form-group-error' : ''
  })

  handlebars.registerHelper('displayErrorClass', function (errors, field) {
    return isError(errors, field) ? 'form-group-error' : ''
  })

  handlebars.registerHelper('displayFormControlErrorClass', function (errors, field) {
    return isError(errors, field) ? 'form-control-error' : ''
  })

  handlebars.registerHelper('isDateErrors', function (errors, searchKey, options) {
    if (isDateErrors(errors, searchKey)) {
      return options.fn(this)
    } else {
      return options.inverse(this)
    }
  })

  handlebars.registerHelper('errorSpan', (errors, field) => {
    if (field && isError(errors, field)) {
      let htmlString = '<span class="error-message">' + getErrors(errors, field) + '</span>'
      return new handlebars.SafeString(htmlString)
    } else {
      return ''
    }
  })

  handlebars.registerHelper('errorId', (field) => {
    let htmlString = '<span class="error-message" id='+field+'>'
    return new handlebars.SafeString(htmlString)
  })

  handlebars.registerHelper('isTimeErrors', function (errors, searchKey, options) {
    if (isTimeErrors(errors, searchKey)) {
      return options.fn(this)
    } else {
      return options.inverse(this)
    }
  })
}

module.exports = {
  isErrors: isErrors,
  isError: isError,
  getError: getErrors,
  isDateErrors: isDateErrors,
  isTimeErrors: isTimeErrors,
  registerHelpers: registerHelpers,
  mapErrors: mapErrors,
  mapConsignmentValidationErrors: mapConsignmentValidationErrors
}
