FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
COPY target/learntribe-resume-processor-*.jar learntribe-resume-processor.jar
ENTRYPOINT ["java","-jar","learntribe-resume-processor.jar"]