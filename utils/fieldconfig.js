const jsonpath = require('jsonpath')

const getField = (data, page, queries) => {
  if (data) {
    for (let i = 0; i < queries.length; i++) {
      let field = jsonpath.query(data, `$..['${page}']..[?(@.id=="${queries[i]}" || @.name=="${queries[i]}")]`)[0]
      if (field !== undefined) {
        return field
      }
    }
  }
  return null
}

/**
 * @returns An array of objects defining a radio button, for each radio button
 * definition matching the given name.
 */
const getNestedRadioFields = (data, page, query) => {
  if (data) {
    let field = jsonpath.query(data, `$..['${page}']..[?(@.component_type=="radio_buttons" && @.name=="${query}")]`)
    if (field !== undefined) {
      return field
    }
  }
  return null
}

const getNestedCheckboxes = (data, page) => {
  if(data) {
    let field = jsonpath.query(data, `$..['${page}']..[?(@.component_type=="checkbox")]`)
    if (field !== undefined) {
      return field
    }
  }
  return null
}

const getCheckbox = (data, page, query) => {
  if(data) {
    let field = jsonpath.query(data, `$..['${page}']..[?(@.component_type=="checkbox" && @.name=="${query}")]`)
    if (field !== undefined) {
      return field
    }
  }
  return null
}

module.exports = {
  getField: getField,
  getNestedRadioFields: getNestedRadioFields,
  getNestedCheckboxes: getNestedCheckboxes,
  getCheckbox: getCheckbox
}
