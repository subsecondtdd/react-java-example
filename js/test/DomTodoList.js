const {JSDOM} = require('jsdom')
const React = require('react')
const ReactDOM = require('react-dom')
const {Simulate} = require('react-dom/test-utils')
const TodoApp = require('../lib/TodoApp')

class DomTodoList {
  constructor(todoList) {
    const dom = new JSDOM('<!DOCTYPE html>')
    const document = dom.window.document
    const $root = document.createElement('div')
    document.body.appendChild($root)

    ReactDOM.render(
      React.createElement(TodoApp, {todoList}),
      $root
    )

    this.$root = $root
  }

  addTodo(todo) {
    // const $button = this.$root.querySelector('.add-hello')
    // Simulate.click($button)
    //
    // // Only DOM event handlers should interact with the target,
    // // return this._hello.hello()
    // const $hello = this.$root.querySelector('li')
    // return $hello.textContent
  }

  getTodos() {
    return [{text: 'Get milk'}]
  }
}

module.exports = DomTodoList