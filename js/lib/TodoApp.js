const React = require('react')
const hyperx = require('hyperx')
const hx = hyperx((Component, props, children) => {
  return React.createElement(Component, props, ...(children || []))
})

module.exports = class TodoApp extends React.Component {
  constructor(props) {
    super(props)
    this.state = {todos: []}

    this.addTodo = () => {
      this.props.todoList.addTodo("A STRING")
      const todos = this.state.todoList.getTodos()
      this.setState({todos})
    }
  }

  render() {
    return hx`
      <div>
        <button class="add-hello" onClick=${this.addTodo}>
        <ul>
           ${this.state.todos.map((todo, i) => Todo(todo, i))}
        </ul>
      </div>
    `
  }
}

// Functional React component
const Todo = (todo, i) => hx`<li key="key-${i}">${todo.getText()}</li>`
