const _ = require('lodash')
const version = require('../../package').version
const countries = require('i18n-iso-countries')
const handlebars = require('handlebars')
const winston = require('winston')
const logger = winston.loggers.get('logger')

const datetime = require('./datetime')
const fieldConfig = require('./fieldconfig')
const notificationHelper = require('./notification')
const config = require('getconfig')
const controlTransportType = require('../transformers/maps/control_transport_type')

const {CVEDA, CVEDP, CED, user, REQUEST_META_DATA_VIEW_CONTEXT_VAR_NAME} = require('../utils/constants')
const {getStreet} = require('./party')

const registerHelpers = () => {
  handlebars.registerHelper('getNodePackageVersion', () => {
    return version
  })

  handlebars.registerHelper('isChecked', (value, expected) => {
    return value === expected ? 'checked' : ''
  })

  handlebars.registerHelper('isInCheckedList', (list, expected) => {
    return (list && list.indexOf(expected) > -1) ? 'checked' : ''
  })

  handlebars.registerHelper('includes', (value, array) => {
    if (Array.isArray(array)) {
      return array.includes(value)
    }
    return false
  })

  /**
   * This method is expecting at least one parameter and (if more than two) should return them comma separated
   * If one of them is not defined, it shall only return the valid ones.
   * If all of them are invalid (objects that are empty except integers), it shall return an empty string.
   */
  handlebars.registerHelper('commaSeparated', (...args) => _.filter(args.slice(0, -1), value => !_.isEmpty(value) || _.isInteger(value)).join(', '))

  /**
   * This method is expecting two parameters and shall return the unit and the label separated by a space.
   * If any of these two params are undefined, the method shall an empty string.
   */
  handlebars.registerHelper('withUnit', (unit, label) => {
    const map = {number: 'Number of', percent: 'Percentage of'}
    if (_.isEmpty(_.get(map, unit)) || _.isEmpty(label)) {
      return ''
    }
    return `${map[unit]} ${label}`
  })

  handlebars.registerHelper('isSelected', (value, expected) => {
    return value === expected ? 'selected' : ''
  })

  handlebars.registerHelper('getCountry', isoCode => {
    if (countries.isValid(isoCode)) {
      return countries.getName(isoCode, 'en')
    } else {
      return isoCode
    }
  })

  handlebars.registerHelper('formatTraderName', trader => {
    if (!trader) {
      return ''
    }

    const vals = _.compact([trader.companyName, trader.individualName])
    return _.first(vals) || ''
  })

  handlebars.registerHelper('formatTraderAddress', trader => {
    if (_.isEmpty(trader)) {
      return ''
    }

    const address = trader.address
    if (_.isEmpty(address)) {
      return ''
    }
    if (address.addressLine1 === 'n/a'){
      address.addressLine1 = ''
    }
    return _.compact([address.addressLine1, address.addressLine2, address.addressLine3,
      address.city, address.postalZipCode]).join(', ')
  })

  const getAddressObject = personResponsible => {
    return _.get(personResponsible, 'addressObject',
        _.get(personResponsible, 'address', null))
  }

  handlebars.registerHelper('formatPersonResponsibleAddress', personResponsible => {
    const addressObject = getAddressObject(personResponsible)
    if (addressObject) {
      const addressArray = [
        getStreet(addressObject),
        _.get(addressObject, 'town'),
        _.get(addressObject, 'city'),
        _.get(addressObject, 'postcode'),
        _.get(addressObject, 'postalCode'),
        _.get(addressObject, 'country')
      ]
      return addressArray.filter(x => _.isString(x) && x.length > 0).join(', ')
    }
    return ''
  })

  handlebars.registerHelper('getBIP', (bipCode, notificationType) => notificationHelper.getBIP(bipCode, notificationType))

  handlebars.registerHelper('getBIPAlias', (documentType, shortForm) => {
    let bipAlias = 'BIP'
    if (documentType === 'CED') {
      bipAlias = shortForm ? 'DPE' : 'Point of Entry'
    }
    return bipAlias
  })

  handlebars.registerHelper('calculateTotals', (model, value) => {
    const parameterSet = model.complementParameterSet

    let result = 0
    for (let i = 0; i < parameterSet.length; ++i) {
      for (let j = 0; j < _.get(parameterSet[i], 'keyDataPair', []).length; ++j) {
        if (parameterSet[i].keyDataPair[j].key === value) {
          result += +(parameterSet[i].keyDataPair[j].data | 0)
        }
      }
    }
    return result
  })

  handlebars.registerHelper('listCommodities', (model, options) => {
    let result = []

    for (let i = 0; i < model.commodityComplement.length; ++i) {
      let commodity = model.commodityComplement[i]
      let dataObject = {
        commodityID: commodity.commodityID,
        //TODO: Fix this.  Commodity Name should come from reference data call, which should not happen in handlebars
        commodityName: '',
        speciesName: commodity.speciesName,
        speciesID: commodity.speciesID,
        speciesTypeName: commodity.speciesTypeName
      }
      let parameters = null
      for (let k = 0; k < model.complementParameterSet.length; ++k) {
        if (model.complementParameterSet[k].speciesID === dataObject.speciesID) {
          parameters = model.complementParameterSet[k]
        }
        if (parameters) {
          for (let j = 0; j < _.get(parameters, 'keyDataPair', []).length; ++j) {
            let kvp = parameters.keyDataPair[j]
            if (kvp.key === 'netweight') {
              dataObject.netWeight = kvp.data
            }

            if (kvp.key === 'grossweight') {
              dataObject.grossWeight = kvp.data
            }

            if (kvp.key === 'number_package') {
              dataObject.numberOfPackages = kvp.data
            }

            if (kvp.key === 'number_animals') {
              dataObject.numberOfAnimals = kvp.data
            }

            if (kvp.key === 'type_package') {
              dataObject.typeOfPackage = kvp.data
            }

            if (kvp.key === 'allowed') {
              dataObject.allowed = kvp.data
            }
          }
        }
      }

      result.push(dataObject)
    }

    let ret = ''
    for (let i = 0; i < result.length; ++i) {
      ret += options.fn(result[i])
    }
    return ret
  })

  handlebars.registerHelper('formatDateTime', (dateTime, format) => {
    return new handlebars.SafeString(datetime.formatDateToRequired(dateTime, format))
  })

  handlebars.registerHelper('formatPartyAddress', (value) => {
    if (!value) {
      return ''
    }
    let addLine = (string, property) => {
      return string + (property && property !== 'null' ? property + '<br>' : '')
    }
    let s = (value.companyName || value.name) ? `${value.companyName || value.name}<br>` : ''
    if (_.isArray(value.address)) {
      value.address.forEach(a => { s = addLine(s, a) })
    } else {
      s = addLine(s, value.address)
    }
    s = addLine(s, value.county)
    s = addLine(s, value.city)
    s = addLine(s, value.postCode)
    s = addLine(s, value.country)

    return new handlebars.SafeString(String(s).startsWith('null') ? '' : s)
  })

  handlebars.registerHelper('formatEconomicOperatorAddress', (value) => {
    if (!value) {
      return ''
    }
    let addAddressLine = (string, property) => {
      return string + (property && property !== 'null' ? property + '<br>' : '')
    }
    let s = (value.companyName || value.individualName) ? `${value.companyName || value.individualName}<br>` : ''
    if (_.isObject(value.address)) {
      s = addAddressLine(s, value.address.addressLine1)
      s = addAddressLine(s, value.address.addressLine2)
      s = addAddressLine(s, value.address.addressLine3)
      s = addAddressLine(s, value.address.city)
      s = addAddressLine(s, value.address.postalZipCode)
      s = addAddressLine(s, value.address.countryISOCode)
    } else {
      s = addAddressLine(s, value.address)
    }

    return new handlebars.SafeString(String(s).startsWith('null') ? '' : s)
  })

  handlebars.registerHelper('customCheckboxGroup', (data, page, searchQueries, options) => {
    let opts = ''

    if (_.isEmpty(data) || !page || !searchQueries) {
      logger.info('Checkbox not displayed because no config data found for fields '
        + searchQueries + ' in ' + page + ' page.')
      return opts
    }

    let queries = searchQueries.split(',')

    let config = fieldConfig.getField(data, page, queries)

    if (config && config.values) {
      config.values.forEach(checkBox => {
        if (checkBox.visible) {
          opts = opts + options.fn(checkBox)
        }
      })
    }
    return opts
  })

  handlebars.registerHelper('getDateDay', dateStr => {
    const date = datetime.getDate(dateStr)
    if (date === '' && dateStr !== undefined) {
      return dateStr.split('-')[2]
    }
    return date
  })

  handlebars.registerHelper('getDateMonth', dateStr => {
    const month = datetime.getMonth(dateStr)
    if (month === '' && dateStr !== undefined) {
      return dateStr.split('-')[1]
    }
    return month
  })

  handlebars.registerHelper('getDateYear', dateStr => {
    const year = datetime.getYear(dateStr)
    if (year === '' && dateStr !== undefined) {
      return dateStr.split('-')[0]
    }
    return year
  })

  handlebars.registerHelper('getCurrentYear', () => datetime.getCurrentYear())

  handlebars.registerHelper('getTimeHour', dateStr => datetime.getHour(dateStr))

  handlebars.registerHelper('getTimeMin', dateStr => datetime.getMin(dateStr))

  handlebars.registerHelper('formatDate',
      dateStr => datetime.formatDate(dateStr))

  handlebars.registerHelper('formatTimestamp',
      timestamp => datetime.formatTimestamp(timestamp))

  handlebars.registerHelper('formatTime', time => datetime.formatTime(time))

  handlebars.registerHelper('formatDateAndTime', time => datetime.formatDateAndTime(time))

  handlebars.registerHelper('getIndex', value => value + 1)

  handlebars.registerHelper('getRegisteredNumber', (purposeData, key) => {
    return (purposeData && purposeData.forNonConforming === key) ? purposeData.regNumber : ''
  })

  const getFormLabelText = (config, formLabel) => {
    if (formLabel || formLabel === '') {
      return formLabel
    } else {
      return config.label
    }
  }

  const isPresent = (fieldConfiguration, page, queries) => {
    if (!fieldConfiguration || !page || !queries) {
      logger.debug('Element not displayed because no config data found for the fields '
        + queries + ' in page ' + page)
      return false
    }
    return fieldConfig.getField(fieldConfiguration, page, queries.split(','))
  }

  const isVisible = (fieldConfiguration, page, queries) => {
    const fieldConfig = isPresent(fieldConfiguration, page, queries)
    return fieldConfig && fieldConfig.visible
  }

  handlebars.registerHelper('eachOption', function (fieldConfiguration, page, searchQueries, options) {
    let ret = ''
    let queries = searchQueries.split(',')
    let config = fieldConfig.getField(fieldConfiguration, page, queries).options

    for (let i = 0; i < config.length; i++) {
      ret = ret + options.fn(config[i])
    }

    return ret
  })

  handlebars.registerHelper('eachValue', (fieldConfiguration, page, searchQueries, options) => {
    let queries = searchQueries.split(',')
    let config = fieldConfig.getField(fieldConfiguration, page, queries)
    if (!_.isNull(config)) {
      for (let i = 0; i < config.values.length; i++) {
        config.values[i].customIndex = i
      }
    }
    return !_.isNull(config) ? _.map(_.filter(config.values, function (o) { return o && o.visible }), options.fn).join('') : ''
  })

  handlebars.registerHelper('getFormLabelText', (formLabel, fieldConfiguration, page, searchQueries) => {
    let queries = searchQueries.split(',')
    let config = fieldConfig.getField(fieldConfiguration, page, queries)
    return getFormLabelText(config, formLabel)
  })

  handlebars.registerHelper('ifPresent', function (fieldConfiguration, page, searchQueries, options) {
    return isPresent(fieldConfiguration, page, searchQueries) ? options.fn(this) : options.inverse(this)
  })

  handlebars.registerHelper('ifVisible', function (fieldConfiguration, page, searchQueries, options) {
    return isVisible(fieldConfiguration, page, searchQueries) ? options.fn(this) : options.inverse(this)
  })

  handlebars.registerHelper('isCheckboxGroupVisible', function (data, page, searchQueries, options) {
    if (_.isEmpty(data) || !page || !searchQueries) {
      return options.inverse(this)
    }

    let queries = searchQueries.split(',')
    let config = fieldConfig.getField(data, page, queries)

    const visible = config && config.values && config.values.some(obj => obj.visible)

    return visible ? options.fn(this) : options.inverse(this)
  })

  /**
   * Helper that is used in the commodity details page that allows us to create
   * a unique key from both the commodity and species id along with the
   * appropriate field.
   * This is required because in some pages you cannot nest handlebars
   * expressions inside handlebars helpers, but you can callout to other
   * handlebars helpers.
   */
  handlebars.registerHelper('getUniqueId', (commodityId, speciesId, field) => {
    const species = speciesId ? speciesId : ''
    return commodityId + '_' + species + '.' + field
  })

  handlebars.registerHelper('toLowercase', (property) => {
    return _.isNil(property) ? property : property.toLowerCase()
  })

  handlebars.registerHelper('toSentenceCase', (property) => {
    return _.isNil(property) ? property : property.charAt(0).toUpperCase() + property.toLowerCase().slice(1)
  })

  handlebars.registerHelper('toOnOff', (property) => {
    if (_.isNil(property)) {
      return property
    }
    return property ? 'on' : 'off'
  })

  handlebars.registerHelper('addDataTarget', function (id, dataTargetMatchers) {
    if (!dataTargetMatchers || !id) {
      return ''
    }
    const matched = dataTargetMatchers.filter(matcher => matcher.elementToMatch === id)
    const shouldMatch = matched.length > 0
    if (shouldMatch) {
      return `data-target="${matched[0].dataTarget}"`
    }
    return ''
  })

  handlebars.registerHelper('shouldAddDataTargetBlock', function (id, dataTargetMatchers, options) {
    if (!dataTargetMatchers || !id) {
      return ''
    }
    const matched = dataTargetMatchers.filter(matcher => matcher.elementToMatch === id)
    return matched.length > 0 ? options.fn(this) : options.inverse(this)
  })

  handlebars.registerHelper('encodePartyJSON', party => {
    if (!_.isEmpty(party)) {
      const partyDecoded = {
        companyName: party.companyName,
        address: party.address[0],
        postCode: party.postCode,
        country: party.country,
        city: party.city
      }
      return encodeURIComponent(JSON.stringify(partyDecoded))
    } else {
      return ''
    }
  })

  handlebars.registerHelper('labTestLink', notification => getLabTestPageLink(notification))

  const getLabTestPageLink = notification => {
    const labTestsPerformed = _.get(notification, 'partTwo.laboratoryTestsRequired')
    const labTestReasonSupplied = _.get(notification, 'partTwo.laboratoryTests.testReason')
    if (labTestsCompleted(notification.type, labTestsPerformed, labTestReasonSupplied)) {
      return 'lab-test/lab-tests-to-be-performed'
    }
    return 'lab-tests-required'
  }

  const labTestsCompleted = (notificationType, labTestsPerformed, labTestReasonSupplied) => {
    if (notificationType === CED &&  labTestsPerformed) {return true}
    if (notificationType === CVEDA && labTestsPerformed && labTestReasonSupplied) {return true}
    if (notificationType === CVEDP && labTestReasonSupplied && labTestReasonSupplied) { return true }
    return false
  }

  handlebars.registerHelper('concat', (...args) => args.slice(0, -1).join(''))

  handlebars.registerHelper('lastIndexOf', (property) => {
    if (_.isNil(property) || _.isEmpty(property)) {
      return ''
    }
    return property.length - 1
  })

  handlebars.registerHelper('isB2c', (context) => {
    const requestMetaData = _.get(context, `data.root.${REQUEST_META_DATA_VIEW_CONTEXT_VAR_NAME}`)
    return (requestMetaData && _.get(requestMetaData, 'loggedInUser.userInfo.tokenType') === user.type.B2C)
  })

  handlebars.registerHelper('getGoogleTagManagerID', () => {
    return config.google.tagManagerID
  })

  handlebars.registerHelper('getControlTransportType', transportType => {
    return _.get(controlTransportType.notificationToViewTransportType, transportType, transportType)
  })

  handlebars.registerHelper('showTrainingBanner', () => {
    return _.includes(config.trainingBannerEnvironments, process.env.ENV_DOMAIN)
  })
}

module.exports = {
  registerHelpers: registerHelpers
}
