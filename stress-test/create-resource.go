package main

import (
	"encoding/json"
	"github.com/brianvoe/gofakeit/v6"
	"os"
	"strings"
)

type CreateUserRequest struct {
	Nick      string   `json:"nick"`
	Name      string   `json:"name"`
	BirthDate string   `json:"birth_date"`
	Stack     []string `json:"stack"`
}

func main() {
	var users []CreateUserRequest

	for i := 0; i < 30000; i++ {
		users = append(users, CreateUserRequest{
			Nick:      gofakeit.LetterN(30),
			Name:      gofakeit.LetterN(200),
			BirthDate: gofakeit.Date().Format("2006-01-02T15:04:05"),
			Stack:     []string{gofakeit.ProgrammingLanguage(), gofakeit.ProgrammingLanguage()},
		})
	}

	usersJson, _ := json.Marshal(users)
	usersJsonString := string(usersJson)

	usersJsonString = strings.ReplaceAll(usersJsonString, "},{", "}\n{")
	usersJsonString = usersJsonString[1:]
	usersJsonString = usersJsonString[:len(usersJsonString)-1]
	usersJsonString = "payload\n" + usersJsonString

	err := os.WriteFile("create-users.tsv", []byte(usersJsonString), 0644)
	println(err)
}
