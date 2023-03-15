FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
COPY target/classes/security.crt security.crt
RUN keytool -importcert -file security.crt -cacerts -storepass changeit -noprompt -alias smilebat
COPY target/learntribe-resume-processor-*.jar learntribe-resume-processor.jar
ENTRYPOINT ["java","-jar","learntribe-resume-processor.jar"]