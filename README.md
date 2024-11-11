# ToolsChallenge

# Detalhamento Técnico

1. Foi utilizado o padrão de projeto MVC, pela maior flexibilidade na implementação de funcionalidades.
2. Maven foi utilizado como ferramenta de gerenciamento de dependências e build do projeto.
3. Docker foi utilizado para facilitar o ambiente de desenvolvimento e a distribuição da aplicação.
4. Postgres foi utilizado pois tem bom desempenho e escalabilidade.

# Documentação da API:

A documentação da API está disponível no Swagger, que pode ser acessada em:

http://localhost:8080/swagger-ui/index.html

# Executando o Projeto com Docker:

Caso não tenha o Docker instalado, acesse https://docs.docker.com/compose/install/ para baixar e instalar.

Com o Docker instalado, siga os passos abaixo para subir o projeto:

1. Navegue até o diretório do projeto.
2. Execute o comando:

``` shell script
docker-compose up -d
```

Após a execução, a aplicação estará disponível em http://localhost:8080/v1/api/pagamento.