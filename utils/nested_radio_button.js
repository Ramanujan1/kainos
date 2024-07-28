const _ = require('lodash')
const fieldConfig = require('./fieldconfig')
const staticJson = require('../data/static.json')

const getNestedFieldConfigDriver = (commodityFieldConfig, searchConfig) => {
  const mappedFieldConfigDriver = _.map(searchConfig, searchOption => {

    const fieldConfigDriver = getTopLevelDriver(commodityFieldConfig, searchOption)

    fieldConfigDriver.hiddenProperties = _.compact(_.map(searchOption.hiddenProperties, hiddenProperty => {

      switch (hiddenProperty.componentType) {
      case 'radio':
        return getRadioHiddenProperty(hiddenProperty)
      case 'table':
        return getTableValues(commodityFieldConfig, hiddenProperty)
      case 'dynamic_date':
        return getDynamicDataDriver(commodityFieldConfig, hiddenProperty)
      case 'checkbox':
        return getCheckBoxDriver(commodityFieldConfig, hiddenProperty)
      case 'static_table':
        return hiddenProperty
      case 'static_date':
        return hiddenProperty
      default:
        return undefined
      }
    }))

    return fieldConfigDriver
  })

  return removeInvisibleElements(mappedFieldConfigDriver)
}

const getTopLevelDriver = (commodityFieldConfig, searchOption) => {
  return {
    radioButton: {
      id: searchOption.id,
      name: searchOption.name,
      value: searchOption.value,
      dataTarget: searchOption.dataTarget,
      checkValue: searchOption.checkValue,
      label: searchOption.label,
      fieldConfig: fieldConfig.getField(commodityFieldConfig, searchOption.page, searchOption.searchTerms)
    }
  }
}

const getRadioHiddenProperty = hiddenProperty => {
  return {
    name: hiddenProperty.name,
    page: hiddenProperty.page,
    heading: hiddenProperty.heading,
    selected: hiddenProperty.selected,
    searchTerms: hiddenProperty.searchTerms.join(),
    componentType: hiddenProperty.componentType
  }
}

const getTableValues = (commodityFieldConfig, hiddenProperty) => {
  let tableValues = _.compact(_.map(hiddenProperty.tableValues, tableVal => {
    if (tableVal.componentType === 'checkbox') {
      const field = fieldConfig.getCheckbox(commodityFieldConfig, tableVal.page, tableVal.searchTerms)
      const isVisible = !_.isNil(field) && field.length > 0
      if (isVisible) {
        return {
          heading: tableVal.heading,
          value: tableVal.value,
          id: tableVal.id,
          componentType: tableVal.componentType
        }
      }
    } else {
      if (tableVal.isVisible !== false) {
        const field = tableVal.fromStaticJson ?
          fieldConfig.getField(staticJson, tableVal.page, tableVal.searchTerms) :
          fieldConfig.getField(commodityFieldConfig, tableVal.page, tableVal.searchTerms)
        const isVisible = _.get(field, 'visible', false) || false
        if (isVisible) {
          return {
            heading: tableVal.heading,
            value: tableVal.value,
            id: tableVal.id,
            tableClass: tableVal.tableClass,
            componentType: tableVal.componentType
          }
        }
      }
    }
  }))

  return {
    tableTitle: hiddenProperty.tableTitle,
    componentType: hiddenProperty.componentType,
    isVisible: tableValues.length > 0,
    tableValues: tableValues
  }
}

const getDynamicDataDriver = (commodityFieldConfig, hiddenProperty) => {
  const field = fieldConfig.getField(commodityFieldConfig, hiddenProperty.page, hiddenProperty.searchTerms)
  if (!_.isNil(field) && field.visible) {
    return {
      id: hiddenProperty.id,
      wrappedId: hiddenProperty.wrappedId,
      value: hiddenProperty.value,
      title: hiddenProperty.title,
      componentType: hiddenProperty.componentType
    }
  }
}

const getCheckBoxDriver = (commodityFieldConfig, hiddenProperty) => {
  const field = fieldConfig.getCheckbox(commodityFieldConfig, hiddenProperty.page, hiddenProperty.searchTerms)
  if (!_.isNil(field) && field.length > 0) {
    return {
      id: hiddenProperty.id,
      value: hiddenProperty.value,
      selected: hiddenProperty.selected,
      label: hiddenProperty.label,
      componentType: hiddenProperty.componentType
    }
  }
}

const removeInvisibleElements = fieldConfigDriver => {
  return _.filter(fieldConfigDriver, val => {
    return !_.isNil(val.radioButton.fieldConfig) && _.get(val, 'radioButton.fieldConfig.visible', false)
  })
}

module.exports = {
  getNestedFieldConfigDriver: getNestedFieldConfigDriver
}