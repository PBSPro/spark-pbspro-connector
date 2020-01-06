FROM pbspro/pbspro

## Install dependencies
RUN yum install -y java-1.8.0-openjdk-devel java-1.8.0-openjdk
ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk/

## Add spark project to image and configure it for PBSPro
ADD . /opt/spark
WORKDIR /opt/spark
RUN echo "spark.pbs.executor.home /opt/spark" >> conf/spark-defaults.conf
RUN echo "spark.master pbs" >> conf/spark-defaults.conf
RUN sed 's/PBS_START_MOM=0/PBS_START_MOM=1/' /etc/pbs.conf >> new.conf && mv new.conf /etc/pbs.conf
