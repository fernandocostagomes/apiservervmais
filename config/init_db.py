import requests

def populate_actions():
    """
    Popula a tabela action com as ações definidas na API.
    """

    # Define as ações a serem inseridas
    actions = [
        {"name": "Criar um novo usuário", "description": "Cria um novo usuário no sistema"},
        {"name": "Editar um usuário existente", "description": "Edita um usuário existente no sistema"},
        {"name": "Excluir um usuário", "description": "Exclui um usuário existente do sistema"},
        {"name": "Logar em um usuário", "description": "Faz login em um usuário no sistema"},
        {"name": "Deslogar um usuário", "description": "Desloga um usuário do sistema"},
        {"name": "Listar todos os usuários", "description": "Lista todos os usuários do sistema"},
        {"name": "Pesquisar um usuário por nome", "description": "Pesquisa um usuário pelo nome no sistema"},
        {"name": "Atribuir uma permissão a um usuário", "description": "Atribui uma permissão a um usuário no sistema"},
        {"name": "Remover uma permissão de um usuário", "description": "Remove uma permissão de um usuário no sistema"},
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
