FROM ibm-semeru-runtimes:open-17.0.10_7-jdk-jammy
RUN apt-get update
RUN apt-get install -y python3 python3-pip
RUN pip install numpy
RUN pip install pandas
RUN pip install torch
RUN pip install jep

COPY apache-maven-3.9.9-bin.tar.gz /usr/local
RUN tar xvf /usr/local/apache-maven-3.9.9-bin.tar.gz -C /usr/local
RUN rm -f usr/local/apache-maven-3.9.9-bin.tar.gz

ENV MAVEN_HOME=/usr/local/apache-maven-3.9.9
ENV PATH="${PATH}:${MAVEN_HOME}/bin"

RUN useradd -ms /bin/bash student
USER student

CMD ["tail","-f","/dev/null"]
