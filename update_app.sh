#!/bin/bash

# Passo 1: Fazer o pull da versão mais recente do código
git pull origin main

# Passo 2: Limpar o cache do Gradle
gradle clean

# Passo 3: Compilar o código
gradle build

# Passo 4: Construir a imagem do Docker
docker buildx . -t fcg_webapp