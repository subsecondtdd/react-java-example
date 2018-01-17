const React = require('react')
const hyperx = require('hyperx')
const hx = hyperx((Component, props, children) => {
  return React.createElement(Component, props, ...(children || []))
})

module.exports = class HelloApp extends React.Component {
  constructor(props) {
    super(props)
    this.state = {messages: []}

    this.addHello = () => {
      const messages = this.state.messages
      messages.push(this.props.hello.hello())
      this.setState({messages})
    }
  }

  render() {
    return hx`
      <div>
        <button class="add-hello" onClick=${this.addHello}>
        <ul>
           ${this.state.messages.map((message, i) => Message(message, i))}
        </ul>
      </div>
    `
  }
}

// Functional React component
const Message = (message, i) => hx`<li key="key-${i}">${message}</li>`
