/**
 * Adds together two decimal numbers given as strings.
 * Removes trailing zeroes.
 * Returns null on error.
 * @param a, string - decimal
 * @param b, string - decimal
 * @returns {Number}
 */
const sumStringDecimal = (a, b) => {
  const countDecimalPlaces = (z) => {
    return z.split('.').slice(-1)[0].length
  }
  const sum = (x, y) => {
    let decimalPlaces = Math.max(countDecimalPlaces(x), countDecimalPlaces(y))
    let total = parseFloat(a) + parseFloat(b)
    let answer = parseFloat(total.toFixed(decimalPlaces).toString())
    return Number.isNaN(answer) ? null : answer
  }
  return a && b ? sum(a, b) : null
}

/**
 * Removes commas from a string.
 * @param str, string - decimal
 * @returns {String}
 */
const removeCommas = (str) => {
  return str && str.replace(/,/g, '')
}

module.exports = {
  sumStringDecimal: sumStringDecimal,
  removeCommas: removeCommas
}
