FROM amazoncorretto:19 as build
WORKDIR /workspace/app

RUN yum -y install gzip

RUN if [ "$(uname -m)" = "aarch64" ]; then url="https://github.com/VirtusLab/coursier-m1/releases/latest/download/cs-aarch64-pc-linux.gz"; else url="https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz"; fi && curl -fL $url | gzip -d > cs && chmod +x cs && ./cs setup --yes

COPY project/plugins.sbt project/plugins.sbt
COPY build.sbt build.sbt
COPY src src

RUN ~/.local/share/coursier/bin/sbt assembly
RUN mv target/scala*/app.jar app.jar

FROM  amazoncorretto:19
VOLUME /tmp
COPY --from=build /workspace/app/app.jar /app.jar
ENTRYPOINT ["java","-jar","app.jar"]