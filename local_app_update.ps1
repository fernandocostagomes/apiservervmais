# Passo 1: Limpar o cache do Gradle
clear
Write-Output "###############"
Write-Output "### Limpando o projeto com 'gradle clean'. ###"
gradle clean
Write-Output "### Finalizado. ###"

# Passo 2: Compilar o código
Write-Output "###############"
Write-Output "### Construindo o projeto com 'gradle build'. ###"
gradle build

# Passo 3: Construir a imagem do Docker
Write-Output "###############"
Write-Output "### Construindo a imagem do Docker com 'docker buildx build . -t fcg_webapp'. ###"
docker buildx build . -t fcg_webapp
Write-Output "### Finalizado. ###"

# Passo 4: Construir o container de acordo com o arquivo compose, antes excluindo as redes e parando os
# containers se estiverem iniciados ou criados.
Write-Output "###############"
Write-Output "### Construindo o container com 'docker-compose up -d --remove-orphans'. ###"
docker-compose down
docker-compose up -d --remove-orphans
Write-Output "### Finalizado. ###"

# Passo 5: Removendo as imagens que não estão em uso da aplicacao web.
Write-Output "###############"
Write-Output "### Removendo os containers sem uso do projeto. ###"
docker system prune -f
Write-Output "### Finalizado. ###"

# Passo 6: Aguardar 10 segundos para que o container seja iniciado.
Write-Output "### Aguardando inicializacao dos servidores ###"
Write-Output "##########"
Start-Sleep -s 1
Write-Output "#########"
Start-Sleep -s 1
Write-Output "########"
Start-Sleep -s 1
Write-Output "#######"
Start-Sleep -s 1
Write-Output "######"
Start-Sleep -s 1
Write-Output "#####"
Start-Sleep -s 1
Write-Output "####"
Start-Sleep -s 1
Write-Output "###"
Start-Sleep -s 1
Write-Output "##"
Start-Sleep -s 1
Write-Output "#"
Start-Sleep -s 1
Write-Output "##"
Start-Sleep -s 1
Write-Output "###"
Start-Sleep -s 1
Write-Output "####"
Start-Sleep -s 1
Write-Output "#####"
Start-Sleep -s 1
Write-Output "######"
Start-Sleep -s 1
Write-Output "#######"
Start-Sleep -s 1
Write-Output "########"
Start-Sleep -s 1
Write-Output "#########"
Start-Sleep -s 1
Write-Output "##########"
Start-Sleep -s 1
Write-Output "###########"
Start-Sleep -s 1

# Passo 7: Popular o banco de dados com o script em python que esta no diretorio /config/init_db.py se os passos
# anteriores estiverem sido executados com sucesso.
Write-Output "###############"
Write-Output "### Populando o banco de dados com 'Python init_db.py'. ###"
Invoke-Expression "py init_db.py localhost"
Write-Output "### Finalizado. ###"