#!/bin/bash

echo "#####################################################"
echo "### Atualizando repositório local com 'git pull'. ###"
echo "#####################################################"
git pull origin main
echo "########################"
echo "### Pull Finalizado. ###"
echo "########################"

echo "############################################"
echo "### Limpando projeto com 'gradle clean'. ###"
echo "############################################"
gradle clean
echo "###############################"
echo "### Build clean Finalizado. ###"
echo "###############################"

echo "#################################################"
echo "### Construindo o projeto com 'gradle build'. ###"
echo "#################################################"
gradle build
echo "###############################"
echo "### Build finalizado. ###"
echo "###############################"

echo "#################################################################################"
echo "### Construindo a imagem do Docker com 'docker buildx build . -t fcg_webapp'. ###"
echo "#################################################################################"
docker buildx build . -t fcg_webapp
echo "################################"
echo "### Buildx build Finalizado. ###"
echo "################################"

echo "##########################################################################"
echo "### Removendo os containers de todos os serviços criados pelo compose. ###"
echo "##########################################################################"

echo "###################################"
echo "####### Service: fcg_webapp #######"
echo "###################################"
docker-compose down --services fcg_webapp

echo "###########################"
echo "### Container removido. ###"
echo "###########################"

echo "#############################"
echo "### Service: fcg_postgres ###"
echo "#############################"
docker-compose down --services fcg_postgres

echo "###########################"
echo "### Container removido. ###"
echo "###########################"

echo "############################"
echo "### Service: fcg_pgadmin ###"
echo "############################"
docker-compose down --services fcg_pgadmin

echo "###########################"
echo "### Container removido. ###"
echo "###########################"

echo "###################################################"
echo "### Removendo as networks criadas pelo compose. ###"
echo "###################################################"

echo "############################"
echo "### Network: fcg_default ###"
echo "############################"
docker network rm fcg_network

echo "###################################################################"
echo "### Remove orphans com 'docker-compose up -d --remove-orphans'. ###"
echo "###################################################################"
docker-compose up -d --remove-orphans

echo "###################"
echo "### Finalizado. ###"
echo "###################"

echo "##################################################"
echo "### Removendo containers sem uso da aplicacao. ###"
echo "##################################################"
docker container prune -f

echo "###################"
echo "### Finalizado. ###"
echo "###################"

echo "###############################################"
echo "### Aguardando inicialização dos servidores ###"
echo "###############################################"

sleep 1
echo "###############################################"
sleep 1
echo "##############################################"
sleep 1
echo "#############################################"
sleep 1
echo "############################################"
sleep 1
echo "###########################################"
sleep 1
echo "##########################################"
sleep 1
echo "#########################################"
sleep 1
echo "########################################"
sleep 1
echo "#######################################"
sleep 1
echo "######################################"
sleep 1
echo "#####################################"
sleep 1
echo "####################################"
sleep 1
echo "###################################"
sleep 1
echo "##################################"
sleep 1
echo "#################################"
sleep 1
echo "################################"
sleep 1
echo "###############################"
sleep 1
echo "##############################"
sleep 1
echo "#############################"
sleep 4

echo "###########################################################"
echo "### Populando o banco de dados com 'Python init_db.py'. ###"
echo "###########################################################"
python3 init_db.py localhost

echo "###################"
echo "### Finalizado. ###"
echo "###################"