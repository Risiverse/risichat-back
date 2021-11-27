package main

import (
	"encoding/json"
	"fmt"
	"html"
	"log"
	"net/http"
	"os"
	"path/filepath"

	"github.com/gorilla/websocket"
	"github.com/joho/godotenv"
)

type User struct {
	webs *websocket.Conn
}

type Message struct {
	Username string
	Content  string
}

var UserPool = make([]User, 0)

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func ws(w http.ResponseWriter, r *http.Request) {
	upgrader.CheckOrigin = func(r *http.Request) bool { return true }

	// upgrade this connection to a WebSocket
	ws, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
	}

	var newUser = User{webs: ws}
	UserPool = append(UserPool, newUser)

	log.Println("New client connected :", newUser)

	reader(ws)
}

func removeElem(uPool []User, i int) []User {
	uPool[i] = UserPool[len(UserPool)-1]
	return uPool[:len(uPool)-1]
}

func testMessageIntegrity(message Message) bool {
	//fmt.Printf("%+v\n", message)
	return message.Username == "" || message.Content == ""
}

func reader(conn *websocket.Conn) {
	for {
		// read in a message
		messageType, p, err := conn.ReadMessage()
		// when client disconnect
		if err != nil {
			log.Println(err)
			// remove user from UserPool
			for i, element := range UserPool {
				if element.webs == conn {
					UserPool = removeElem(UserPool, i)
					return
				}
			}
		}

		if err := conn.WriteMessage(messageType, p); err != nil {
			log.Println(err)
			return
		}

		var newMessage Message
		err = json.Unmarshal(p, &newMessage)
		if err != nil {
			log.Println(err)
		}

		newMessage.Content = html.EscapeString(newMessage.Content) // XSS

		if testMessageIntegrity(newMessage) {
			continue
		}

		validMessage, err := json.Marshal(newMessage)
		if err != nil {
			log.Fatal(err)
		}

		broadcast(validMessage, conn)
	}
}

func broadcast(message []byte, conn *websocket.Conn) {
	for _, element := range UserPool {
		if element.webs != conn {
			writer(element.webs, message)
		}
	}
}

func writer(conn *websocket.Conn, message []byte) {
	err := conn.WriteMessage(websocket.TextMessage, message)
	if err != nil {
		log.Println(err)
		return
	}
}

// TODO Mettre sur une URL random recupéré en .env
func initServer() {
	http.HandleFunc(os.Getenv("ROUTE"), ws)
}

func main() {
	err := godotenv.Load(filepath.Join("cmd", ".env"))
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("Starting server")
	initServer()
	log.Fatal(http.ListenAndServe(":8080", nil))
}
