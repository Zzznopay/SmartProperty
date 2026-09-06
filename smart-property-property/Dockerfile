# ===== 阶段 1: 构建 =====
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /workspace
COPY pom.xml ./
COPY smart-property-common/pom.xml ./smart-property-common/
COPY smart-property-gateway/pom.xml ./smart-property-gateway/
COPY smart-property-system/pom.xml ./smart-property-system/
COPY smart-property-property/pom.xml ./smart-property-property/
COPY smart-property-operation/pom.xml ./smart-property-operation/
RUN mvn -B -q -DskipTests -pl smart-property-common -am dependency:go-offline || true
COPY smart-property-common/src ./smart-property-common/src
COPY smart-property-property/src ./smart-property-property/src
RUN mvn -B -DskipTests -pl smart-property-property -am package

# ===== 阶段 2: 运行时 =====
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring && chown -R spring:spring /app
USER spring:spring
COPY --from=builder /workspace/smart-property-property/target/smart-property-property.jar /app/app.jar
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseContainerSupport -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8"
EXPOSE 8002
HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -qO- http://localhost:8002/actuator/health 2>/dev/null | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
