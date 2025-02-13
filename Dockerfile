# Etapa 1: Construção do app com Maven (usando OpenJDK 17)
FROM eclipse-temurin:17-jdk AS build

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Copiar o pom.xml e baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar o código-fonte da aplicação
COPY src ./src

# Construa a aplicação Spring Boot com JDK 17
RUN mvn clean package -DskipTests

# Etapa 2: Imagem de execução (executar o app com OpenJDK 17 e FFmpeg)
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