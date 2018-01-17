Object.spawn = function(clazz){
  return new clazz(...Array.prototype.slice.call(arguments, 1))
}

class DomHello {
  constructor(hello) {
    this._hello = hello
  }

  hello() {
    // TODO: API methods should only interact with the DOM.
    // Only DOM event handlers should interact with the target,
    return this._hello.hello()
  }
}

module.exports = DomHello