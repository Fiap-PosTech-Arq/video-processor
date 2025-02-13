# Etapa 1: Build (Construção do app com Maven)
FROM maven:3.8.6-openjdk-17 AS build

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar o pom.xml e baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar o código-fonte da aplicação
COPY src ./src

# Compilar a aplicação Spring Boot
RUN mvn clean package -DskipTests

# Etapa 2: Imagem de execução (executar o app com FFmpeg)
FROM openjdk:17-slim

# Instalar FFmpeg
RUN apt-get update && apt-get install -y ffmpeg

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar o artefato JAR gerado pela construção para a imagem
COPY --from=build /app/target/*.jar /app/app.jar

# Expõe a porta na qual a aplicação irá rodar (ajuste se necessário)
EXPOSE 8080

# Comando para rodar o app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]