#!/bin/bash

#Testando o hook
# Passo 1: Atualizar o repositório local
echo "###############"
echo "### Atualizando o repositório local com 'git pull'. ###"
echo "###############"
git pull origin main
echo "###################"
echo "### Finalizado. ###"

# Passo 2: Limpar o cache do Gradle
echo "###################"
echo "### Limpando o projeto com 'gradle clean'. ###"
gradle clean
echo "###################"
echo "### Finalizado. ###"

# Passo 3: Compilar o código
echo "###################"
echo "### Construindo o projeto com 'gradle build'. ###"
gradle build

# Passo 4: Construir a imagem do Docker
echo "###################"
echo "### Construindo a imagem do Docker com 'docker buildx build . -t fcg_webapp'. ###"
docker buildx build . -t fcg_webapp
echo "###################"
echo "### Finalizado. ###"

# Passo 5: Removendo os containers de todos os serviços criados pelo compose.
echo "###################"
echo "### Removendo os containers de todos os serviços criados pelo compose. ###"
echo "###################"
echo "### Service: fcg_webapp ###"
docker-compose down --services fcg_webapp
echo "###################"
echo "### Container removido. ###"
echo "###################"
echo "### Service: fcg_postgres ###"
docker-compose down --services fcg_postgres
echo "###################"
echo "### Container removido. ###"
echo "###################"
echo "### Service: fcg_pgadmin ###"
docker-compose down --services fcg_pgadmin
echo "###################"
echo "### Container removido. ###"

# Passo 6: Removendo as redes pelo compose.
echo "###################"
echo "### Removendo as networks criadas pelo compose. ###"
echo "###################"
echo "### Network: fcg_default ###"
echo "###################"
docker network rm fcg_network

# Passo 7: Removendo orphans criados pelo compose.
echo "###################"
echo "### Remove orphans com 'docker-compose up -d --remove-orphans'. ###"
docker-compose up -d --remove-orphans
echo "###################"
echo "### Finalizado. ###"

# Passo 7: Removendo os containers que não estão em uso da aplicacao web.
echo "###################"
echo "### Removendo os containers que não estão em uso da aplicacao web. ###"
docker container prune -f
echo "###################"
echo "### Finalizado. ###"

# Passo 8: Aguardar 10 segundos para que o container seja iniciado.
echo "###################"
echo "### Aguardando inicialização dos servidores ###"
echo "##########"
sleep 1
echo "#########"
sleep 1
echo "########"
sleep 1
echo "#######"
sleep 1
echo "######"
sleep 1
echo "#####"
sleep 1
echo "####"
sleep 1
echo "###"
sleep 1
echo "##"
sleep 1
echo "#"
sleep 1
echo "##"
sleep 1
echo "###"
sleep 1
echo "####"
sleep 1
echo "#####"
sleep 1
echo "######"
sleep 1
echo "#######"
sleep 1
echo "########"
sleep 1
echo "#########"
sleep 1
echo "##########"
sleep 1
echo "###########"
sleep 5

# Passo 8: Popular o banco de dados com o script em python que esta no diretorio /config/init_db.py se os passos
# anteriores estiverem sido executados com sucesso.
echo "###################"
echo "### Populando o banco de dados com 'Python init_db.py'. ###"
echo "###################"
python3 init_db.py localhost
echo "###################"
echo "### Finalizado. ###"