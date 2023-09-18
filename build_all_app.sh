#!/bin/bash

# Passo 1: Fazer o pull da versão mais recente do código
git pull origin main

# Passo 2: Limpar o cache do Gradle
gradle clean

# Passo 3: Compilar o código
gradle build

# Passo 4: Construir a imagem do Docker
docker buildx build . -t fcg_webapp

# Passo 5: Parar o container fcg_webapp
docker stop fcg_webapp

# Passo 6: Remover o container fcg_webapp
docker rm fcg_webapp

# Passo 7: Rodar o compose que para executar o container fcg_webapp
docker-compose up -d