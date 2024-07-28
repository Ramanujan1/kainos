const winston = require('winston')
const logger = winston.loggers.get('logger')

const getRadioFieldsHtml = (configList) => {
  let radioFields = ''
  if (configList) {
    configList.forEach(config => {
      radioFields += constructRadioFields(config)
    })
  } else {
    logger.info('No config found to construct radio fields')
  }
  return radioFields
}

const constructRadioFields = (config) => {
  let radioFieldsHtml = ''
  if (config.values) {
    config.values.forEach(radio => {
      if (radio.visible) {
        radioFieldsHtml += `
            <div class="multiple-choice">
            <input id="${radio.id}" type="radio" name="${radio.name}" value="${radio.value}" 
              ${radio.selected ? 'checked' : ''}>
            <label for="${radio.id}">${radio.label}</label>
            </div>`
      }
    })
  } else {
    logger.info('No radio fields can be found')
  }
  return radioFieldsHtml
}

module.exports = {
  getRadioFieldsHtml: getRadioFieldsHtml
}
