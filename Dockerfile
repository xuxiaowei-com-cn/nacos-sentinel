FROM openjdk:8

WORKDIR /home

ARG GROUP_ID=${GROUP_ID}
ARG ARTIFACT_ID=${ARTIFACT_ID}
ARG VERSION=${VERSION}
ARG CI_PIPELINE_ID=${CI_PIPELINE_ID}
ARG CI_JOB_ID=${CI_JOB_ID}

RUN printf "GROUP_ID: %s\n" "$GROUP_ID" \
    && printf "ARTIFACT_ID: %s\n" "$ARTIFACT_ID" \
    && printf "VERSION: %s\n" "$VERSION" \
    && printf "CI_PIPELINE_ID: %s\n" "$CI_PIPELINE_ID" \
    && printf "CI_JOB_ID: %s\n" "$CI_JOB_ID"

ADD target/$ARTIFACT_ID-$VERSION.jar /home/app.jar

EXPOSE 8080

ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8 \
    GROUP_ID=${GROUP_ID} \
    ARTIFACT_ID=${ARTIFACT_ID} \
    VERSION=${VERSION} \
    CI_PIPELINE_ID=${CI_PIPELINE_ID} \
    CI_JOB_ID=${CI_JOB_ID}

CMD java -jar /home/app.jar
