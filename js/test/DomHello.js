const { JSDOM } = require('jsdom')
const React = require('react')
const ReactDOM = require('react-dom')
const { Simulate } = require('react-dom/test-utils')
const HelloApp = require('../lib/HelloApp')

class DomHello {
  constructor(hello) {
    const dom = new JSDOM('<!DOCTYPE html>')
    const document = dom.window.document
    const $root = document.createElement('div')
    document.body.appendChild($root)

    ReactDOM.render(
      React.createElement(HelloApp, { hello }),
      $root
    )

    this.$root = $root
  }

  hello() {
    const $button = this.$root.querySelector('.add-hello')
    Simulate.click($button)

    // Only DOM event handlers should interact with the target,
    // return this._hello.hello()
    const $hello = this.$root.querySelector('li')
    return $hello.textContent
  }
}

module.exports = DomHello