function instantiate(clazz) {
  return new clazz(...Array.prototype.slice.call(arguments, 1))
}

module.exports = { instantiate }