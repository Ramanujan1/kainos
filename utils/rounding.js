const roundEven = (num) =>
{
  if (isNaN(num)) {
    return null
  }
  if (num === null || num === 0) {
    return num
  }

  var m = 100
  var n = +(num * m)
  var i = Math.floor(n), f = n - i
  var e = 1e-8
  var r = (f > 0.5 - e && f < 0.5 + e) ? ((i % 2 == 0) ? i : i + 1)
    : Math.round(n)
  return r / m
}

module.exports = {
  roundEven: roundEven
}