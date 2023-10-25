import requests
import json
import sys

base_url = sys.argv[1]
full_url = "http://" + base_url + ":8080"
application_json = {"Content-Type": "application/json"}

# Verifica se pelo menos um argumento foi passado (o nome do script é o primeiro argumento)
if len(sys.argv) > 1:
    # O primeiro argumento (índice 0) é o nome do script, então o valor desejado é o segundo argumento (índice 1)
    valorParametro = sys.argv[1]
    print(f"O endereço passado no parâmetro passado é: {valorParametro}")
else:
    print("Nenhum parâmetro foi passado.")


def populate_actions():
    """ Popula a tabela action com as ações definidas na API.   """
    for action in getArray("actions"):
        response = requests.post(
            f"{full_url}/action",
            data=json.dumps(action),
            headers={"Content-Type": "application/json"},
        )

        # Verifica se a solicitação foi bem-sucedida
        if response.status_code == 201:
            print(f"A ação {action['nameAction']} foi inserida com sucesso!")
        else:
            print(f"Ocorreu um erro ao inserir a ação {action['nameAction']} "
                  f"Status response:  {response.status_code}")

def populate_users():
    """ Popula a tabela de usuários com dados de exemplo. """
    for user in getArray("users"):
        response = (
            requests.post(
                f"{full_url}/user",
                data=json.dumps(user),
                headers={"Content-Type": "application/json"},
            ))

        if response.status_code == 201:
            print(f"Usuário {user['userEmail']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir usuário {user['userEmail']}.")

def populate_parameters():
    """ Popula a tabela de parametros com dados de exemplo. """
    for parameter in getArray("parameters"):
        response = requests.post(
            f"{full_url}/parameter",
            data=json.dumps(parameter),
            headers={"Content-Type": "application/json"},
        )

        if response.status_code == 201:
            print(f"Parametro {parameter['nameParameter']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir parametro {parameter['nameParameter']}.")

""" Popula a tabela de address com dados de exemplo. """
def populate_address():

    for address in getArray("addresses"):
        response = requests.post(
            f"{full_url}/address",
            data=json.dumps(address),
            headers=application_json,
        )

        if response.status_code == 201:
            print(f"Address {address['nameAddress']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir address {address['nameAddress']}.")


""" Popula a tabela de roles com dados de exemplo. """
def populate_roles():
    for role in getArray("roles"):
        response = requests.post(
            f"{full_url}/role",
            data=json.dumps(role),
            headers=application_json,
        )

        if response.status_code == 201:
            print(f"Role {role['nameRole']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir role {role['nameRole']}.")

""" Popula a tabela de permissions com dados de exemplo. """
def populate_permissions():
    for permission in getArray("permissions"):
        response = requests.post(
            f"{full_url}/permission",
            data=json.dumps(permission),
            headers=application_json,
        )
        if response.status_code == 201:
            print(f"Permission {permission['namePermission']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir permission {permission['namePermission']}.")

def getUserByEmail(email):
    response = requests.get(full_url + f"/user/email/{email}")
    # Verifica se a solicitação foi bem-sucedida
    if response.status_code == 200:
        print(f"O usuário {email} foi encontrado com sucesso!")
        data = json.loads(response.text)
        return data["userId"]
    else:
        print(f"Ocorreu um erro ao buscar o usuário {email} "
              f"Status response:  {response.status_code}")
        return None

def getArray(name):
    if name == "users":
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
        return users
    elif name == "parameters":
        parameters = [
            {
                "codeParameter": "100",
                "nameParameter": "Parameter100",
                "valueParameter": "100",
                "dateParameter": "2023-10-17",
            },
            {
                "codeParameter": "200",
                "nameParameter": "Parameter200",
                "valueParameter": "200",
                "dateParameter": "2023-10-17",
            },
        ]
        return parameters
    elif name == "actions":
        actions = [
            {
                "nameAction": "Create user",
                "descriptionAction": "Cria novo usuário no sistema.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Edite user",
                "descriptionAction": "Edita usuário no sistema.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Remove user", "descriptionAction":
                "Exclui usuário do sistema.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Login user",
                "descriptionAction": "Faz login usuário no sistema.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Logout user",
                "descriptionAction": "Desloga usuário do sistema.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "List users",
                "descriptionAction": "Lista usuários do sistema.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Find user",
                "descriptionAction": "Pesquisa usuário pelo nome.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Setar perm. user",
                "descriptionAction": "Atribui permissão a usuário.",
                "dateAction": "2023-10-17"
            },
            {
                "nameAction": "Remove perm. user",
                "descriptionAction": "Remove permissão de usuário.",
                "dateAction": "2023-10-17"
            },
        ]
        return actions
    elif name == "addresses":
        addresses = [
            {
                "nameAddress": "Casa",
                "zipcodeAddress": "72.304-116",
                "addressAddress": "QN 122 conj 15 lote 01 bloco A",
                "numberAddress": "1308",
                "cityAddress": "Samambaia",
                "stateAddress": "DF",
                "idUserAddress": getUserByEmail("fernandocostagomes@gmail.com"),
                "dateAddress": "2023-10-17",
            },
            {
                "nameAddress": "Trabalho",
                "zipcodeAddress": "72.910-901",
                "addressAddress": "SCN Quadra 1 Ed. Esplanada Rossi",
                "numberAddress": "S/N",
                "cityAddress": "Asa Norte",
                "stateAddress": "DF",
                "idUserAddress": getUserByEmail("fernandocostagomes@gmail.com"),
                "dateAddress": "2023-10-17",
            },
            {
                "nameAddress": "Casa",
                "zipcodeAddress": "71.882-108",
                "addressAddress": "QC 03 conj 8 lote 3 Bloco A",
                "numberAddress": "104",
                "cityAddress": "Riacho Fundo II",
                "stateAddress": "DF",
                "idUserAddress": getUserByEmail("rafaelcostafernandes2015@gmail.com"),
                "dateAddress": "2023-10-17",
            },
        ]
        return addresses
    elif name == "roles":
        roles = [
            {
                "nameRole": "Admin",
                "descriptionRole": "Administrador do sistema",
                "dateRole": "2023-10-17",
            },
            {
                "nameRole": "User",
                "descriptionRole": "Usuário do sistema",
                "dateRole": "2023-10-17",
            },
        ]
        return roles
    elif name == "permissions":
        permissions = [
            {
                "namePermission": "Create user",
                "descriptionPermission": "Cria novo usuário no sistema.",
                "datePermission": "2023-10-17",
                "idRolePermission": "1",
                "idActionPermission": "1",
            },
            {
                "namePermission": "Edite user",
                "descriptionPermission": "Edita usuário no sistema.",
                "datePermission": "2023-10-17",
                "idRolePermission": "1",
                "idActionPermission": "2",
            },
            {
                "namePermission": "Remove user",
                "descriptionPermission": "Exclui usuário do sistema.",
                "datePermission": "2023-10-17",
                "idRolePermission": "1",
                "idActionPermission": "3",
            },
            {
                "namePermission": "Login user",
                "descriptionPermission": "Faz login usuário no sistema.",
                "datePermission": "2023-10-17",
                "idRolePermission": "1",
                "idActionPermission": "4",
            }
        ]
        return permissions
    else:
        return None

if __name__ == "__main__":
    populate_actions()
    populate_users()
    populate_parameters()
    populate_address()
    populate_roles()
    populate_permissions()