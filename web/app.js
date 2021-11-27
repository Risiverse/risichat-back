let socket = new WebSocket("ws://127.0.0.1:8080/");
console.log("Attempting Connection...");

socket.onopen = () => {
    console.log("Successfully Connected");
};

socket.onclose = event => {
    console.log("Socket Closed Connection: ", event);
};

socket.onerror = error => {
    console.error("Socket Error: ", error);
};

socket.onmessage = message => {
    let p = document.createElement('p')
    let newMessage = JSON.parse(message.data)
    console.log(newMessage)
    p.innerHTML = "<strong>" + newMessage['Username'] + "</strong><br>" + newMessage["Content"] + "<br>"
    document.querySelector('div').appendChild(p)
};

let messageInput = document.querySelector('#message')
let nicknameInput = document.querySelector('#nickname')

document.querySelector("form").addEventListener('submit', e => {
    e.preventDefault()
    
    let message = {
        "Username" : nicknameInput.value,
        "Content" : messageInput.value,
    }
    socket.send(JSON.stringify(message))
    messageInput.value = ""
})