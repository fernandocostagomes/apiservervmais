import requests
import json
import sys

base_url = sys.argv[1]
full_url = "http://" + base_url + ":8080"
application_json = {"Content-Type": "application/json"}
token = ""

# Verifica se pelo menos um argumento foi passado (o nome do script é o primeiro argumento)
if len(sys.argv) > 1:
    # O primeiro argumento (índice 0) é o nome do script, então o valor desejado é o segundo argumento (índice 1)
    valorParametro = sys.argv[1]
    print(f"O endereço passado no parâmetro passado é: {valorParametro}")
else:
    print("Nenhum parâmetro foi passado.")

def getToken():
    """ Retorna o token de acesso. """
    logins = [
        {
            "email": "admin@admin",
            "password": "admin123"
        }
    ]
    for login in logins:
        response = requests.post(full_url + f"/login", data=json.dumps(login), headers=application_json)
        if response.status_code == 200:
            token = json.loads(response.text)["token"]
            print(f"Token: {token}")
        else:
            print(f"Ocorreu um erro ao solicitar o token" f"Status response:  {response.status_code}")

def populate_actions():
    """ Popula a tabela action com as ações definidas na API.   """
    for action in getArray("actions"):
        print(token)
        response = requests.post(
            f"{full_url}/action",
            data=json.dumps(action),
            headers={"Authorization": "Bearer " + token},
        )

        # Verifica se a solicitação foi bem-sucedida
        if response.status_code == 201:
            print(f"A ação {action['actionName']} foi inserida com sucesso!")
        else:
            print(f"Ocorreu um erro ao inserir a ação {action['actionName']} "
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
            print(f"Parametro {parameter['parameterName']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir parametro {parameter['parameterName']}.")

""" Popula a tabela de address com dados de exemplo. """
def populate_address():
    for address in getArray("addresses"):
        response = requests.post(
            f"{full_url}/address",
            data=json.dumps(address),
            headers=application_json,
        )

        if response.status_code == 201:
            print(f"Address {address['addressName']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir address {address['addressName']}.")


""" Popula a tabela de roles com dados de exemplo. """
def populate_roles():
    for role in getArray("roles"):
        response = requests.post(
            f"{full_url}/role",
            data=json.dumps(role),
            headers=application_json,
        )

        if response.status_code == 201:
            print(f"Role {role['roleName']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir role {role['roleName']}.")

""" Popula a tabela de permissions com dados de exemplo. """
def populate_permissions():
    for permission in getArray("permissions"):
        response = requests.post(
            f"{full_url}/permission",
            data=json.dumps(permission),
            headers=application_json,
        )
        if response.status_code == 201:
            print(f"Permission {permission['permissionName']} inserido com sucesso!")
        else:
            print(f"Erro ao inserir permission {permission['permissionName']}.")

""" Popula a tabela de pwd com dados de exemplo. """
def teste_login():
    for pwd in getArray("pwd"):
        response = requests.post(
            f"{full_url}/login",
            data=json.dumps(pwd),
            headers=application_json,
        )
        print(f"{response.text} \n Login: {pwd['email']}.")

def getUserByEmail(tipo, email):
    response = requests.get(full_url + f"/user/email/{email}")
    # Verifica se a solicitação foi bem-sucedida
    if response.status_code == 200:
        if(tipo == "pwdCurrent"):
            data = json.loads(response.text)
            return data["pwdCurrent"]
        elif(tipo == "id"):
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
                "userPhone": "5561985141904",
                "userNick": "Nando",
                "userBirthday": "28/05/1980",
                "userDate": "2023-10-17",
                "userPwdCurrent": "senha123",
                "userPwdId": 0,
            },
            {
                "userEmail": "rafaelcostafernandes2015@gmail.com",
                "userName": "Rafael Costa",
                "userPhone": "5561999888951",
                "userNick": "Rafa",
                "userBirthday": "08/09/2011",
                "userDate": "2023-10-17",
                "userPwdCurrent": "senha456",
                "userPwdId": 0,
            },
        ]
        return users
    elif name == "parameters":
        parameters = [
            {
                "parameterCode": "100",
                "parameterName": "Parameter100",
                "parameterValue": "100",
                "parameterDate": "2023-10-17",
            },
            {
                "parameterCode": "200",
                "parameterName": "Parameter200",
                "parameterValue": "200",
                "parameterDate": "2023-10-17",
            },
        ]
        return parameters
    elif name == "actions":
        actions = [
            {
                "actionName": "Create user",
                "actionDescription": "Cria novo usuário no sistema.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Edite user",
                "actionDescription": "Edita usuário no sistema.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Remove user",
                "actionDescription":
                "Exclui usuário do sistema.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Login user",
                "actionDescription": "Faz login usuário no sistema.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Logout user",
                "actionDescription": "Desloga usuário do sistema.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "List users",
                "actionDescription": "Lista usuários do sistema.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Find user",
                "actionDescription": "Pesquisa usuário pelo nome.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Setar perm. user",
                "actionDescription": "Atribui permissão a usuário.",
                "actionDate": "2023-10-17"
            },
            {
                "actionName": "Remove perm. user",
                "actionDescription": "Remove permissão de usuário.",
                "actionDate": "2023-10-17"
            },
        ]
        return actions
    elif name == "addresses":
        addresses = [
            {
                "addressName": "Casa",
                "addressZipcode": "72.304-116",
                "addressAddress": "QN 122 conj 15 lote 01 bloco A",
                "addressNumber": "1308",
                "addressCity": "Samambaia",
                "addressState": "DF",
                "addressUserId": getUserByEmail("id", "fernandocostagomes@gmail.com"),
                "addressDate": "2023-10-17",
            },
            {
                "addressName": "Trabalho",
                "addressZipcode": "72.910-901",
                "addressAddress": "SCN Quadra 1 Ed. Esplanada Rossi",
                "addressNumber": "S/N",
                "addressCity": "Asa Norte",
                "addressState": "DF",
                "addressUserId": getUserByEmail("id", "fernandocostagomes@gmail.com"),
                "addressDate": "2023-10-17",
            },
            {
                "addressName": "Casa",
                "addressZipcode": "71.882-108",
                "addressAddress": "QC 03 conj 8 lote 3 Bloco A",
                "addressNumber": "104",
                "addressCity": "Riacho Fundo II",
                "addressState": "DF",
                "addressUserId": getUserByEmail("id", "rafaelcostafernandes2015@gmail.com"),
                "addressDate": "2023-10-17",
            },
        ]
        return addresses
    elif name == "roles":
        roles = [
            {
                "roleName": "Admin",
                "roleDescription": "Administrador do sistema",
                "roleDate": "2023-10-17",
            },
            {
                "roleName": "User",
                "roleDescription": "Usuário do sistema",
                "roleDate": "2023-10-17",
            },
        ]
        return roles
    elif name == "permissions":
        permissions = [
            {
                "permissionName": "Create user",
                "permissionDescription": "Cria novo usuário no sistema.",
                "permissionDate": "2023-10-17",
                "permissionRoleId": "1",
                "permissionActionId": "1",
            },
            {
                "permissionName": "Edite user",
                "permissionDescription": "Edita usuário no sistema.",
                "permissionDate": "2023-10-17",
                "permissionRoleId": "1",
                "permissionActionId": "2",
            },
            {
                "permissionName": "Remove user",
                "permissionDescription": "Exclui usuário do sistema.",
                "permissionDate": "2023-10-17",
                "permissionRoleId": "1",
                "permissionActionId": "3",
            },
            {
                "permissionName": "Login user",
                "permissionDescription": "Faz login usuário no sistema.",
                "permissionDate": "2023-10-17",
                "permissionRoleId": "1",
                "permissionActionId": "4",
            }
        ]
        return permissions
    elif name == "pwd":
        pwds = [
            {
                "email": "fernandocostagomes@gmail.com",
                "password": "senha123",
            },
            {
                "email": "rafaelcostafernandes2015@gmail.com",
                "password": "senha456",
            },
            {
                "email": "rafaelcostafernandes2015@gmail.com",
                "password": "senha123",
            }
        ]
        return pwds
    else:
        return None

if __name__ == "__main__":
    getToken()
    populate_actions()
    populate_users()
    populate_parameters()
    populate_address()
    populate_roles()
    populate_permissions()
    teste_login()