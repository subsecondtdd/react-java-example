module.exports = class MyWidget {
  constructor(id, wobble) {
    this._id = id
    this._wobble = wobble
  }

  id() {
    return this._id
  }

  description(name) {
    return `Widget ${name}`
  }

  wobble() {
    return this._wobble.wobble()
  }
}