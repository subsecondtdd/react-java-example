const jsdom = require('jsdom')
const React = require('react')
const ReactDOM = require('react-dom')

const JSDOM = jsdom.JSDOM
const dom = new JSDOM('<!DOCTYPE html><div id="root">')
const document = dom.window.document

function render() {
  const $domNode = document.createElement('div')
  $domNode.id = 'root'
  document.body.appendChild($domNode)

  ReactDOM.render(
    React.createElement('div', null, 'Hello World'),
    $domNode
  )

  return $domNode.innerHTML
}

module.exports = { render }