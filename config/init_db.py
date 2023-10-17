import requests
import json

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
            headers={"Content-Type": "application/json"},
        )

        # Verifica se a solicitação foi bem-sucedida
        if response.status_code == 201:
            print(f"A ação {action['nameAction']} foi inserida com sucesso!")
        else:
            print(f"Ocorreu um erro ao inserir a ação {action['nameAction']}.")

if __name__ == "__main__":
    populate_actions()
