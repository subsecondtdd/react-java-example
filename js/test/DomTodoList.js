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
    // TODO: Fill in a form with `todo.text`
    const $button = this.$root.querySelector('.add-hello')
    Simulate.click($button)
  }

  getTodos() {
    const lis = [...this.$root.querySelectorAll('li')]
    return lis.map($li => {
      return { text: $li.textContent }
    })
  }
}

module.exports = DomTodoList