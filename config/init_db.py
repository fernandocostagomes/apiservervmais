import requests
import json

def populate_actions():
    """ Popula a tabela action com as ações definidas na API.   """

    # Define as ações a serem inseridas
    actions = [
        {"nameAction": "Criar um novo usuário", "descriptionAction": "Cria um novo usuário no sistema"},
        {"nameAction": "Editar um usuário existente", "descriptionAction": "Edita um usuário existente no sistema"},
        {"nameAction": "Excluir um usuário", "descriptionAction": "Exclui um usuário existente do sistema"},
        {"nameAction": "Logar em um usuário", "descriptionAction": "Faz login em um usuário no sistema"},
        {"nameAction": "Deslogar um usuário", "descriptionAction": "Desloga um usuário do sistema"},
        {"nameAction": "Listar todos os usuários", "descriptionAction": "Lista todos os usuários do sistema"},
        {"nameAction": "Pesquisar um usuário por nome", "descriptionAction": "Pesquisa um usuário pelo nome no sistema"},
        {"nameAction": "Atribuir uma permissão a um usuário", "descriptionAction": "Atribui uma permissão a um usuário no sistema"},
        {"nameAction": "Remover uma permissão de um usuário", "descriptionAction": "Remove uma permissão de um usuário no sistema"},
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
            print(f"A ação {action['name']} foi inserida com sucesso!")
        else:
            print(f"Ocorreu um erro ao inserir a ação {action['name']}.")

if __name__ == "__main__":
    populate_actions()
