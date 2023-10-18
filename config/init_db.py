import requests
import json

base_url = "http://portainer.fernandocostagomes.com:8080"  # Substitua pela URL da sua API
application_json = {"Content-Type": "application/json"}


def populate_actions():
    """ Popula a tabela action com as ações definidas na API.   """

    # Define as ações a serem inseridas
    actions = [
        {"nameAction": "Create user", "descriptionAction": "Cria novo usuário no sistema."},
        {"nameAction": "Edite user", "descriptionAction": "Edita usuário no sistema."},
        {"nameAction": "Remove user", "descriptionAction": "Exclui usuário do sistema."},
        {"nameAction": "Login user", "descriptionAction": "Faz login usuário no sistema."},
        {"nameAction": "Logout user", "descriptionAction": "Desloga usuário do sistema."},
        {"nameAction": "List users", "descriptionAction": "Lista usuários do sistema."},
        {"nameAction": "Find user", "descriptionAction": "Pesquisa usuário pelo nome."},
        {"nameAction": "Setar perm. user", "descriptionAction": "Atribui permissão a usuário."},
        {"nameAction": "Remove perm. user", "descriptionAction": "Remove permissão de usuário."},
    ]

    # Faz uma solicitação HTTP POST para a API
    for action in actions:
        response = requests.post(
            url="http://portainer.fernandocostagomes.com:8080/action",
            data=json.dumps(action),
            headers=application_json
        )

        # Verifica se a solicitação foi bem-sucedida
        if response.status_code == 201:
            print(f"A ação {action['nameAction']} foi inserida com sucesso!")
        else:
            print(f"Ocorreu um erro ao inserir a ação {action['nameAction']}.")


def populate_users():
    """ Popula a tabela de usuários com dados de exemplo. """

    # Define os dados dos usuários a serem inseridos

    users = [
        {
            "userEmail": "fernandocostagomes@gmail.com",
            "userName": "Fernando Costa",
            "userPwd": "senha123",
            "userPhone": "5561985141904",
            "userNick": "Nando",
            "userBirthday": "28/05/1980",
            "userDate": "2023-10-17",
        },
        {
            "userEmail": "rafaelcostafernandes2015@gmail.com",
            "userName": "Rafael Costa",
            "userPwd": "senha456",
            "userPhone": "5561999888951",
            "userNick": "Rafa",
            "userBirthday": "08/09/2011",
            "userDate": "2023-10-17",
        },
    ]

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


def populate_parameters():
    """ Popula a tabela de parametros com dados de exemplo. """

    # Define os dados dos parametros a serem inseridos

    parameters = [
        {
            "codeParameter": "100",
            "nameParameter": "Parameter100",
            "descriptionParameter": "Parametro de teste codigo 100",
            "dataParameter": "2023-10-17",
        },
        {
            "codeParameter": "200",
            "nameParameter": "Parameter200",
            "descriptionParameter": "Parametro de teste codigo 200",
            "dataParameter": "2023-10-17",
        },
    ]

    for parameter in parameters:
        response = requests.post(
            f"{base_url}/parameter",
            data=json.dumps(parameter),
            headers={"Content-Type": "application/json"},
        )

        if response.status_code == 201:
            print(f"Parametro {parameter['nameParameter']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir parametro {parameter['nameParameter']}.")


if __name__ == "__main__":
    populate_actions()
    populate_users()
    populate_parameters()
