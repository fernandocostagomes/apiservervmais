#!/bin/bash

#Testando o hook
# Passo 1: Atualizar o repositório local
echo "###############"
echo "### Atualizando o repositório local com 'git pull'. ###"
git pull
echo "### Finalizado. ###"

# Passo 2: Limpar o cache do Gradle
echo "###############"
echo "### Limpando o projeto com 'gradle clean'. ###"
gradle clean
echo "### Finalizado. ###"

# Passo 3: Compilar o código
echo "###############"
echo "### Construindo o projeto com 'gradle build'. ###"
gradle build

# Passo 4: Construir a imagem do Docker
echo "###############"
echo "### Construindo a imagem do Docker com 'docker buildx build . -t fcg_webapp'. ###"
docker buildx build . -t fcg_webapp
echo "### Finalizado. ###"

# Passo 5: Construir o container de acordo com o arquivo compose, antes excluindo as redes e parando os
# containers se estiverem iniciados ou criados.
echo "###############"
echo "### Construindo o container com 'docker-compose up -d --remove-orphans'. ###"
docker-compose down
docker-compose up -d --remove-orphans
echo "### Finalizado. ###"

# Passo 6: Removendo os containers que não estão em uso da aplicacao web.
echo "###############"
echo "### Removendo os containers que não estão em uso da aplicacao web. ###"
docker container prune -f
echo "### Finalizado. ###"

# Passo 7: Aguardar 10 segundos para que o container seja iniciado.
echo "### Aguardando inicialização dos servidores ###"
echo "##########"
Start-Sleep -s 1
echo "#########"
Start-Sleep -s 1
echo "########"
Start-Sleep -s 1
echo "#######"
Start-Sleep -s 1
echo "######"
Start-Sleep -s 1
echo "#####"
Start-Sleep -s 1
echo "####"
Start-Sleep -s 1
echo "###"
Start-Sleep -s 1
echo "##"
Start-Sleep -s 1
echo "#"
Start-Sleep -s 1
echo "##"
Start-Sleep -s 1
echo "###"
Start-Sleep -s 1
echo "####"
Start-Sleep -s 1
echo "#####"
Start-Sleep -s 1
echo "######"
Start-Sleep -s 1
echo "#######"
Start-Sleep -s 1
echo "########"
Start-Sleep -s 1
echo "#########"
Start-Sleep -s 1
echo "##########"
Start-Sleep -s 1
echo "###########"
Start-Sleep -s 1

# Passo 8: Popular o banco de dados com o script em python que esta no diretorio /config/init_db.py se os passos
# anteriores estiverem sido executados com sucesso.
echo "###############"
echo "### Populando o banco de dados com 'Python init_db.py'. ###"
Invoke-Expression "py init_db.py localhost"
echo "### Finalizado. ###"