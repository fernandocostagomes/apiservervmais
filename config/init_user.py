import requests
import json


def populate_users():
    """ Popula a tabela de usuários com dados de exemplo. """

    # Define os dados dos usuários a serem inseridos

    users = [
        {
            "userEmail": "fernandocostagomes@gmail.com",
            "userName": "Fernando Costa Gomes",
            "userPwd": "senha123",
            "userPhone": "5561985141904",
            "userNick": "Nando",
            "userDate": "2023-10-17",
        },
        {
            "userEmail": "rafaelcostafernandes2015@gmail.com",
            "userName": "Rafael Costa Fernandes",
            "userPwd": "senha456",
            "userPhone": "5561999888951",
            "userNick": "Rafa",
            "userDate": "2023-10-17",
        },
    ]

    base_url = "http://portainer.fernandocostagomes.com:8080"  # Substitua pela URL da sua API

    for user in users:
        response = requests.post(
            f"{base_url}/user",
            data=json.dumps(user),
            headers={"Content-Type": "application/json"},
        )

        if response.status_code == 201:
            print(f"Usuário {user['userEmail']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir usuário {user['userEmail']}.")


if __name__ == "__main__":
    populate_users()
