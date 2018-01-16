const jsdom = require('jsdom')
const React = require('react')
const ReactDOM = require('react-dom')

const JSDOM = jsdom.JSDOM
const dom = new JSDOM('<!DOCTYPE html>')
const document = dom.window.document

function render() {
  const $root = document.createElement('div')
  document.body.appendChild($root)

  ReactDOM.render(
    React.createElement('div', null, 'Hello World'),
    $root
  )

  return $root.innerHTML
}

module.exports = { render }