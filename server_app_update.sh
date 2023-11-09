#!/bin/bash

function show() {
  echo ""
  echo "### $1 ###"
  echo ""
}
function apaga_imprime() {
  # Limpa a linha
  echo -en "\r"
  # Imprime o texto recebido
  echo -n "$1"
}

show "Atualizando repositório local com 'git pull'."
git pull origin main
show "Pull Finalizado."

show "Limpando o projeto com 'gradle clean'."
gradle clean
show "Build clean Finalizado."

show "Construindo o projeto com 'gradle build'."
gradle build
show "Build finalizado."

show "Construindo a imagem do Docker com 'docker buildx build . -t fcg_webapp'."
docker buildx build . -t fcg_webapp
show "Buildx build Finalizado."

show "Removendo os containers de todos os serviços criados pelo compose."
show "Service: fcg_webapp"
docker-compose down --services fcg_webapp
show "Container removido."

show "Service: fcg_postgres"
docker-compose down --services fcg_postgres
show "Container removido."

show "Service: fcg_pgadmin"
docker-compose down --services fcg_pgadmin
show "Container removido."

show "Removendo as networks criadas pelo compose."
show "Network: fcg_default"
docker network rm fcg_network

show "Remove orphans com 'docker-compose up -d --remove-orphans'."
docker-compose up -d --remove-orphans
show "Finalizado delete orphans."

show "Removendo containers sem uso da aplicacao."
docker container prune -f
show "Finalizado."

show "Aguardando inicialização dos servidores"

show "Iniciando servers: 10"

for i in {20..1}; do
  # Chama a função com o texto da linha
  apaga_imprime "$i"
  # Aguarda 1 segundo
  sleep 1
done

show "Populando banco de dados com 'init_db.py'."

python3 init_db.py localhost

show "Finalizado."