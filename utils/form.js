/**
 * Convert a single element into an array.
 * There is a hapi quirk in that if a 'multi' form is posted with a single value,
 * then the value is not held in an array, but is a single element.
 * This function performs a conversion so that all values are held in an array.
 * @param elements
 * @returns {*[]}
 */
const convertToArray =  (element) => {
  return [].concat.apply([], [element])
}

module.exports = {
  convertToArray: convertToArray,
}