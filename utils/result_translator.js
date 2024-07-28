const _ = require('lodash')

const satisfactionMapping = {
  '1': 'Satisfactory',
  '2': 'Not Satisfactory',
  '3': 'Derogation',
  '4': 'Not Set',
  '7': 'Not Done'
}

const satisfactionFromServiceMapping = _.invert(satisfactionMapping)

const physicalReasonNotDoneMapping = {
  '8': 'Reduced checks regime',
  '9': 'Other'
}

const physicalReasonNotDoneFromServiceMapping = _.invert(physicalReasonNotDoneMapping)

const identityCheckTypeMapping = {
  '5': 'Seal Check',
  '6': 'Full Identity Check'
}

const identityCheckTypeMappingFromService = _.invert(identityCheckTypeMapping)

const booleanMapping = {
  '1': true,
  '2': false
}

const booleanMappingFromService = {
  'true': '1',
  'false': '2'
}

const translateToSatisfactionResult = (key) => {
  return satisfactionMapping[key]
}

const translateFromSatisfactionResult = (key) => {
  return satisfactionFromServiceMapping[key]
}

const translateToPhysicalCheckNotDoneReason = (key) => {
  return physicalReasonNotDoneMapping[key]
}

const translateFromPhysicalCheckNotDoneReason = (key) => {
  return physicalReasonNotDoneFromServiceMapping[key]
}

const translateToIdentityCheckType = (key) => {
  return identityCheckTypeMapping[key]
}

const translateFromIdentityCheckType = (key) => {
  return identityCheckTypeMappingFromService[key]
}

const translateToBoolean = (key) => {
  return booleanMapping[key]
}

const translateFromBoolean = (key) => {
  return booleanMappingFromService[key]
}

module.exports = {
  translateToSatisfactionResult: translateToSatisfactionResult,
  translateFromSatisfactionResult: translateFromSatisfactionResult,
  translateToPhysicalCheckNotDoneReason: translateToPhysicalCheckNotDoneReason,
  translateFromPhysicalCheckNotDoneReason: translateFromPhysicalCheckNotDoneReason,
  translateToIdentityCheckType: translateToIdentityCheckType,
  translateFromIdentityCheckType: translateFromIdentityCheckType,
  translateToBoolean: translateToBoolean,
  translateFromBoolean: translateFromBoolean
}