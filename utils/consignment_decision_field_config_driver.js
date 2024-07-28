const _ = require('lodash')
const {CED} = require('./constants')

const getFieldConfigDriver = notification => {
  return [{
    searchTerms: ['a_accputrsh', 'acceptability-transhipment', 'p_acc2'],
    page: 'Acceptance',
    dataTarget: 'transhipment',
    id: 'acceptability-transhipment',
    name: 'acceptability',
    value: 'tranship',
    label: notification.type === CED ? 'Transfer' : 'Transhipment',
    checkValue: 'Acceptable for Transhipment',
    hiddenProperties: [{
      componentType: 'table',
      tableTitle: 'Transhipment destination',
      tableValues: [{
        fromStaticJson: true,
        isVisible: notification.type !== CED,
        heading: 'BIP',
        page: 'BIP',
        value: _.get(notification, 'partOne.purpose.finalBIP'),
        searchTerms: ['bip_select'],
        tableClass: '',
        id: 'transhipment-destination-bip',
        componentType: 'table'
      }, {
        fromStaticJson: false,
        heading: '3rd Country',
        page: 'Purpose',
        value: _.get(notification, 'partOne.purpose.thirdCountryTranshipment'),
        searchTerms: ['a_trashbipco', 'p_trashcoco'],
        tableClass: 'divider-bottom',
        id: 'transhipment-destination-third-country',
        componentType: 'table'
      }, {
        searchTerms: ['authOnwardTransport'],
        page: 'Acceptance',
        heading: 'Control point',
        value: _.get(notification, 'partTwo.onwardTransportation'),
        id: 'transfer-control-point',
        componentType: 'checkbox'
      }]
    }, {
      searchTerms: ['authOnwardTransport'],
      page: 'Acceptance',
      componentType: 'checkbox',
      id: 'authOnwardTransport',
      value: 'onward',
      selected: _.get(notification, 'partTwo.onwardTransportation'),
      label: 'Consignment authorised for onward transportation (pending results of laboratory tests - consignment not to be released)'
    }]
  }, {
    searchTerms: ['a_accputrst', 'acceptability-transit'],
    page: 'Acceptance',
    dataTarget: 'transit',
    id: 'acceptability-transit',
    name: 'acceptability',
    value: 'transit',
    label: 'Transit to a third country',
    checkValue: 'Acceptable for Transit',
    hiddenProperties: [{
      componentType: 'table',
      tableTitle: 'Transit destination',
      tableValues: [{
        fromStaticJson: false,
        heading: '3rd country',
        page: 'Purpose',
        value: _.get(notification, 'partOne.purpose.thirdCountry'),
        searchTerms: ['a_trashbipco', 'p_trashcoco'],
        tableClass: '',
        id: 'transit-destination-third-country',
        componentType: 'table'
      }, {
        fromStaticJson: true,
        heading: 'Exit BIP',
        page: 'BIP',
        value: _.get(notification, 'partOne.purpose.exitBIP'),
        searchTerms: ['bip_select'],
        tableClass: 'divider-bottom',
        id: 'transit-destination-exit-bip',
        componentType: 'table'
      }]
    }]
  }, {
    searchTerms: ['a_accpuimp'],
    page: 'Acceptance',
    dataTarget: 'import',
    id: 'acceptability-import',
    name: 'acceptability',
    value: 'import',
    label: 'Definitive import',
    checkValue: 'Acceptable for Internal Market',
    hiddenProperties: [{
      page: 'Acceptance',
      componentType: 'radio',
      heading: 'Why is the consignment being imported?',
      searchTerms: ['acceptImport'],
      name: 'acceptImport',
      selected: mapDefinitiveImportPurpose[_.get(notification, 'partTwo.decision.definitiveImportPurpose')]
    }, {
      componentType: 'static_table',
      id: 'definitive-import'
    }]
  }, {
    searchTerms: ['a_accputmp'],
    page: 'Acceptance',
    dataTarget: 'temporary',
    id: 'acceptability-temporary',
    name: 'acceptability',
    value: 'temporary',
    label: 'Temporary admission',
    checkValue: 'Acceptable for Temporary Import',
    hiddenProperties: [{
      componentType: 'static_date',
      id: 'deadline-date',
      wrappedId: 'deadline',
      individualId: 'temp-deadline',
      title: 'Deadline',
      value: _.get(notification, 'partTwo.decision.temporaryDeadline')
    }, {
      componentType: 'static_table',
      id: 'temporary-admission'
    }]
  }, {
    searchTerms: ['acceptability-channeled'],
    page: 'Acceptance',
    dataTarget: 'channelled',
    id: 'acceptability-channelled',
    name: 'acceptability',
    value: 'channelled',
    label: 'To be channelled',
    checkValue: 'Acceptable if Channeled',
    hiddenProperties: [{
      page: 'Acceptance',
      componentType: 'radio',
      heading: 'How will the consignment be channelled?',
      searchTerms: ['acceptChannelledAction'],
      name: 'acceptChannelledAction',
      selected: _.get(notification, 'partTwo.decision.ifChanneledOption')
    }, {
      componentType: 'static_table',
      id: 'channelled'
    }]
  }, {
    searchTerms: ['acceptability-internalmarket'],
    page: 'Acceptance',
    dataTarget: 'internal',
    id: 'acceptability-internalmarket',
    name: 'acceptability',
    value: 'internalmarket',
    label: 'UK internal market',
    checkValue: 'Acceptable for Internal Market',
    hiddenProperties: [{
      page: 'Acceptance',
      componentType: 'radio',
      heading: 'How will the consignment be used for free circulation in the UK internal market?',
      searchTerms: ['acceptMarketFreeCirculation'],
      name: 'acceptMarketFreeCirculation',
      selected: mapInternalFreeCirculationPurpose[_.get(notification, 'partTwo.decision.freeCirculationPurpose')]
    }]
  }, {
    searchTerms: ['acceptability-nonconforming'],
    page: 'Acceptance',
    dataTarget: 'warehouse',
    id: 'acceptability-nonconforming',
    name: 'acceptability',
    value: 'nonconforming',
    label: 'Specific warehouse procedure',
    checkValue: 'Acceptable for Specific Warehouse',
    hiddenProperties: [{
      page: 'Acceptance',
      componentType: 'radio',
      heading: 'How will the consignment be used for free circulation in the UK internal market?',
      searchTerms: ['acceptSpecificWarehouse'],
      name: 'acceptSpecificWarehouse',
      selected: mapSpecificWarehouseNonConformingConsignment[_.get(notification, 'partTwo.decision.specificWarehouseNonConformingConsignment')]
    }, {
      componentType: 'static_table',
      id: 'non-conforming'
    }]
  }, {
    searchTerms: ['p_acc5'],
    page: 'Acceptance',
    dataTarget: 'internal',
    id: 'acceptability-internalmarket',
    name: 'acceptability',
    value: 'internalmarket',
    label: 'Free circulation',
    checkValue: 'Acceptable for Internal Market',
    hiddenProperties: [{
      page: 'Acceptance',
      componentType: 'radio',
      heading: 'Release for free circulation',
      searchTerms: ['acceptReleaseFreeCirculation'],
      name: 'acceptReleaseFreeCirculation',
      selected: mapReleaseFreeCirculationPurpose[_.get(notification, 'partTwo.decision.freeCirculationPurpose')]
    }]
  }]
}

const getNotAcceptableFieldConfigDriver = notification => {
  return [{
    searchTerms: ['acceptabilityrefused', 'a_noacc'],
    page: 'Refusal',
    dataTarget: 'refusal',
    id: 'acceptability-refused',
    name: 'acceptability',
    value: 'refused',
    label: 'Not acceptable',
    checkValue: 'Non Acceptable',
    hiddenProperties: [{
      page: 'Refusal',
      componentType: 'radio',
      heading: '',
      searchTerms: ['notAcceptAction'],
      name: 'notAcceptAction',
      selected: mapRefusalReason[_.get(notification, 'partTwo.decision.notAcceptableAction')]
    }, {
      componentType: 'dynamic_date',
      page: 'Refusal',
      searchTerms: ['p_nadat'],
      id: 'not-acceptable',
      wrappedId: 'not-acceptable-date',
      value: _.get(notification, 'partTwo.decision.notAcceptableActionByDate'),
      title: 'By date'
    }]
  }]
}

const mapDefinitiveImportPurpose = {
  'approvedbodies': 'approved',
  'quarantine': 'quarantine',
  'slaughter': 'slaughter'
}

const mapSpecificWarehouseNonConformingConsignment = {
  'CustomWarehouse': 'customs',
  'FreeZoneOrFreeWarehouse': 'free',
  'ShipSupplier': 'supplier',
  'Ship': 'ship'
}

const mapReleaseFreeCirculationPurpose = {
  'Animal Feeding Stuff': 'feedingstuff',
  'Human Consumption': 'human',
  'Pharmaceutical Use': 'pharma',
  'Technical Use': 'technical',
  'Further Process': 'further',
  'Other': 'other'
}

const mapInternalFreeCirculationPurpose = {
  'Animal Feeding Stuff': 'animal',
  'Human Consumption': 'human',
  'Pharmaceutical Use': 'pharma',
  'Technical Use': 'technical',
  'Further Process': 'further',
  'Other': 'other'
}

const mapRefusalReason = {
  'slaughter': 'slaughter',
  'reexport': 'reexport',
  'euthanasia': 'euthanasia',
  'redispatching': 'redispatch',
  'destruction': 'destruction',
  'transformation': 'transformation',
  'other': 'other'
}

module.exports = {
  getFieldConfigDriver: getFieldConfigDriver,
  getNotAcceptableFieldConfigDriver: getNotAcceptableFieldConfigDriver
}