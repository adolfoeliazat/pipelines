# **This file describe infrastructure needed for build [deeplearning4j](https://github.com/deeplearning4j) project**
## **Project uses following platforms for build**:
- **Linux-x86_64** is hosted on [Azure cloud](https://azure.microsoft.com) (FQDN of host: master-jenkins.skymind.io)
- **Android-x86_64** is hosted on *Linux x86_64* as container with Android SDK on demand
- **Android arm** is hosted on *Linux x86_64* as container with Android SDK on demand
- **Jenkins master** is hosted on *Linux x86_64* as container
- **Nexus Repository PRO** is  hosted on *Linux x86_64* as container
- **SonarQube** is hosted on *Linux x86_64* as container
- **Artifactory-OSS** is hosted on *Linux x86_64* as container
- **Linux-ppc64le** (PowerPC) is hosted on [OSU Open Source Lab](http://osuosl.org/services/powerdev) (FQDN of host: 
power-jenkins.skymind.io)
- **Windows-x86_64** is hosted on [Azure cloud](https://azure.microsoft.com) (FQDN of host: windows-jenkins.skymind.io)
- **MacOS-x86_64** is hosted on [Macincloud](http://www.macincloud.com) (FQDN of host: mac-jenkins.skymind.io)

To all above nodes assigned labels, which Jenkins use for connect them as its slaves. Also all nodes should be synchronized on time (NTP is needed).


### **Linux x86_64**, **Android-x86**, **Androi-arm**
Labels: **amd64**, **android-arm**, **android-x86**, **sshlocal**

This platform considers as the main because all components of the project will build on its. Necessary tools/hardware for build on this 
platform:
 - GPU with latest driver and compatible with CUDA 7.5 & 8.0 ([link](http://www.nvidia.com/Download/index.aspx))
 - GCC 4.9 or 5.x
 - java sdk ([link](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
 - CMake 3.2
 - Maven ([link](https://maven.apache.org/))
 - Git
 - nvidia-docker ([link](https://github.com/NVIDIA/nvidia-docker))
 - Scala ([link](https://www.scala-lang.org))
 
 Nvidia-docker will be using for purpose where on one host we can build libnd4j for different version of CUDA independed each other. On this 
 host will build Android too.
 

#### Connect host as slave to Jenkins master:
 
- Go to **Manage Jenkins** -> **Manage Nodes** and click on **New Node**. Then fill out **Node name** and choose **Permanent Agent**
- Fill out fields:

<p align="center">
  <img src="/imgs/linux-slave.png"/>
</p>
Docker images for CUDA 7.5, CUDA 8.0, Android-x86_64, Android-arm build on this host by job 
[BuildDockerImages](http://master-jenkins.skymind.io:8080/job/devel/job/service/job/BuildDockerImages). This job use [script]
(/jobs/docker/build-push-docker-images.groovy) which do next:
- Stage **Build** pull base docker image (which will be fundament for our main images after additional needed steps) from DockerHub registry
for CUDA 7.5 (base image is [nvidia/cuda:7.5-cudnn5-devel-centos6](https://hub.docker.com/r/nvidia/cuda)), CUDA 8.0 (base image is 
[nvidia/cuda:8.0-cudnn5-devel-centos6](https://hub.docker.com/r/nvidia/cuda)), for Android-x86_64 and Android-arm (base image is 
[maven:latest](https://hub.docker.com/_/maven/)) and builds main images according [Dockerfiles](/docker) for each needed platforms.
- Stage **Test** checks that docker images are built correctly by checking the version of necessary tools inside containers.
- Stage **Push** push those images to private Docker registry ([Bintray](https://bintray.com/deeplearning4j/registry)) in case where parameter 
*PUSH_TO_REGISTRY* is chose.

After that, **Linux x86_64**, **Android-x86**, **Androi-arm** are ready to get instruction of build from Jenkins master.

### **Jenkins master**
Jenkins master is  hosted on **Linux-x86_64** as container which run with follow command:

**docker run -d --name jenkins2322 -p 8080:8080 -p 50000:50000 -v /srv/jenkins:/var/jenkins_home zensam/jenkins:v2.32.2**

where,
- assign name of container as **jenkins2322**
- with **-p** option we expose external 8080 port to 8080 port of container and external 50000 port to 50000 port of container
- with **-v** option we mount **/srv/jenkins** directory of jenkins as volume to path **/var/jenkins_home** inside container
- use **zensam/jenkins:v2.32.2** pre-configured Docker image of Jenkins

**!!ATTENTION!!** 
Don't upgrade **Docker Pipeline** plugin on Jenkins master from 1.9.1 version to new yet. New version (at this time 1.10) has some bug which has
negative influence to our builds. At least until the moment when this [bug](https://issues.jenkins-ci.org/browse/JENKINS-42322) will be fixed.

### **Nexus Repository Pro**
Nexus Repository is  hosted on **Linux-x86_64** as container which run with follow command:

**docker run -d --name nexus -p 8088:8081 -v  /srv/pv/nexus/sonatype-work:/sonatype-work sonatype/nexus:pro**

where,
- assign name of container as **nexus**
- with **-p** option we expose external 8088 port to 8081 port of container
- with **-v** option we mount **/srv/pv/nexus/sonatype-work** directory of jenkins as volume to path **/srv/pv/nexus/sonatype-work** inside 
container
- use **sonatype/nexus:pro** as Docker image of Nexus Pro

### **SonarQube**
SonarQube is  hosted on **Linux-x86_64** as container which run with follow command:

**docker run -d --name  sonarqube -p 9000:9000 -p 9092:9092 -v  /srv/pv/sq/extentions:/opt/sonarqube/extensions zensam/sonar**

where,
- assign name of container as **sonarqube**
- with **-p** option we expose external 9000 port to 9000 port of container and external 9092 port to 9092 port of container
- with **-v** option we mount **/srv/pv/sq/extentions** directory of jenkins as volume to path **/opt/sonarqube/extensions** inside 
container
- use **zensam/sonar** as Docker image of SonarQube

### **Artifactory-OSS**
Artifactory-OSS is  hosted on **Linux-x86_64** as container which run with follow command:
**docker run -d --name artifactory-oss -p 8081:8081  -v /srv/pv/artifactory/data:/var/opt/jfrog/artifactory/data -v /srv/pv/artifactory/etc:
/var/opt/jfrog/artifactory/etc -v /srv/pv/artifactory/logs:/var/opt/jfrog/artifactory/logs docker.bintray.io/jfrog/artifactory-oss:latest**

where,
- assign name of container as **artifactory-oss**
- with **-p** option we expose external 8081 port to 8081 port of container
- with **-v** option we mount **/srv/pv/artifactory/data** directory of jenkins as volume to path **/var/opt/jfrog/artifactory/data** inside
container, **/srv/pv/artifactory/etc** directory of jenkins as volume to path **/var/opt/jfrog/artifactory/etc** inside container and 
**/srv/pv/artifactory/data** directory of jenkins as volume to path **/var/opt/jfrog/artifactory/data** inside container
- use **docker.bintray.io/jfrog/artifactory-oss:latest** as Docker image of SonarQube

### **Linux-ppc64le**
Labels: **ppc**, **ppc64le**, **ubuntu**

Seeing that this host also managed by Linux OS, difference only in hardware architecture, some previous steps are actual for its too. For
example, process of connect to Jenkins master is the same:

<p align="center">
  <img src="/imgs/linux-ppc-slave.png"/>
</p>

The process of build docker images is described above and the base image for this platform is [ppc64le/ubuntu:14.04]
(https://hub.docker.com/r/ppc64le/ubuntu). Unlike  **Linux-x86_64**, CUDA 7.5 Toolkit and all necessary tools are installed during building 
main Docker image according to [Dockerfile's instruction](/docker/ubuntu14-ppc64le/Dockerfile). This node requires only ssh and java sdk for 
Jenkins master-slave connection. Also on this host we will build only two components of project: 
[libnd4j](https://github.com/deeplearning4j/libnd4j) and [nd4j](https://github.com/deeplearning4j/nd4j)

### **Windows-x86_64**
Labels: **windows-x86_64**

For build on Windows we have to install set of tools which can emulate some Linux behaviour on Windows OS family. As on the previous host we 
will build only two components of project: 
[libnd4j](https://github.com/deeplearning4j/libnd4j) and [nd4j](https://github.com/deeplearning4j/nd4j). The following tools are needed for 
build on this platform:
- Git ([link](https://git-scm.com))
- Maven ([link](https://maven.apache.org))
- java sdk ([link](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
- MSYS2 ([link](http://www.msys2.org))
- CUDA Toolkit 7.5 ([link](https://developer.nvidia.com/cuda-75-downloads-archive)) and CUDA 8.0 Toolkit ([link]
 (https://developer.nvidia.com/cuda-downloads))
- Microsoft Visual Studio Community 2013 ([link](https://www.visualstudio.com/en-us/news/releasenotes/vs2013-community-vs))
Then after installation all above tools we should modify system variables inside host.
- Create new variable *MAVEN_HOME* and *M2_HOME* and set those value as path to directory with Maven
- Create new variable *JAVA_HOME* and set value as path to java sdk

<p align="center">
  <img src="/imgs/var_1.png"/>
</p>

- Edit *Path* system variable and path to *C:\msys64\mingw64\bin* and *C:\msys64\usr\bin* in case when MSYS2 is installed in *C:\msys64* and 
add *C:\\Program Files (x86)\Microsoft Visual Studio 12.0\VC\bin\amd64* in case when Microsoft Visual Studio Community 2013 is installed in 
*C:\Program Files (x86)\Microsoft Visual Studio 12.0*

Then we should connect this host as slave to Jenkins master:
- Go to **Manage Jenkins** -> **Manage Nodes** and click on **New Node**. Then fill out **Node name** and choose **Permanent Agent**
- Fill out fields:

<p align="center">
  <img src="/imgs/win-slave.png"/>
</p>

- Log in to the host -> edit **C:\Windows\System32\drivers\etc\hosts** and add record **10.0.6.5 master-jenkins.skymind.io** at the end of file.
That needs for case when **Windows-x86_64** host will connect to Jenkins master via inside network
- Then open Jenkins in browser (NOTE: For openning browser Right click on browser icon and click **Run as 
administrator**) and click **Launch** button 
<p align="center">
  <img src="/imgs/win-slave-2.png"/>
</p>

<p align="center">
  <img src="/imgs/jenkins-win-slave.png"/>
</p>

- In opened window click **File** -> **Install as a service**

<p align="center">
  <img src="/imgs/jenkins-service.png"/>
</p>


Now host Windows-x86_64 is ready to get instruction of build from Jenkins master.

### **MacOS-x86_64**
Labels: **osx**, **macosx**, **macosx-x86_64**

As on the previous host we will build only two components of project: [libnd4j](https://github.com/deeplearning4j/libnd4j) and 
[nd4j](https://github.com/deeplearning4j/nd4j).
For preparing this host for build follow this instructions:
- Host has all the Apple updates installed
- Host has the latest XCode. You will also want to run **xcode-select --install** to ensure the correct version of Xcode is available.
- Host havs brew (if not get it [brew](http://brew.sh))
- Run **brew install gcc5 cmake** in terminal (this will install GCC). Also please note: openblas is optional, Apple Accelerate library is 
supported as well.
- Add following symbolic links:

```
ln -s /usr/local/Cellar/gcc5/5.4.0/bin/gcc-5 /usr/local/bin/gcc-5
ln -s /usr/local/Cellar/gcc5/5.4.0/bin/g++-5 /usr/local/bin/g++-5
ln -s /usr/local/Cellar/gcc5/5.4.0/bin/gfortran-5 /usr/local/bin/gfortran-5
```
- Optional: Remove gcc-6 by running

```
brew remove gcc
```

#### Connect host as slave to Jenkins master:

Connect MacOS-x86_64 host as jenkins-node via ssh or jnlp plugin either:  


<h3>ssh agent: </h3> 
<p align="center">
  <img src="/imgs/macos-slave-ssh.png"/>
</p>


<h3>jnlp agent: </h3>
<p align="center">
  <img src="/imgs/mac-slave-jlnp.png"/>
</p>

SSH-plugin doesn't require any additional action on the host.  
To make JNLP connection persistent - create launchd daemon as described [here] (https://developer.apple.com/library/content/documentation/MacOSX/Conceptual/BPSystemStartup/Chapters/CreatingLaunchdJobs.html)  
/Library/LaunchDaemons/org.jenkins-ci.slave.jnlp.plist:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>org.jenkins-ci.slave.jnlp</string>
    <key>ProgramArguments</key>
    <array>
        <string>sudo</string>
        <string>/usr/bin/java</string>
        <string>-Djava.awt.headless=true</string>
        <string>-jar</string>
        <string>/Users/admin/jenkins/slave.jar</string>
        <string>-jnlpUrl</string>
        <string>http://master-jenkins.skymind.io:8080/computer/macosx-x86_64/slave-agent.jnlp</string>
        <string>-secret</string>
        <string>XXXXXXXXXXXXXXXXXXXXXXXXXXXXX_NODE_SECRET_KEY_FROM_NODE_SETTINGS_HERE_XXXXXXXXXXXXXXXXXXXXXXXXXXX</string>
    </array>
    <key>KeepAlive</key>
    <true/>
    <key>RunAtLoad</key>
    <true/>
    <key>StandardOutPath</key>
    <string>/Users/admin/jenkins/stdout.log</string>
    <key>StandardErrorPath</key>
    <string>/Users/admin/jenkins/error.log</string>
</dict>
</plist>
```
```bash
sudo launchctl unload /Library/LaunchDaemons/org.jenkins-ci.slave.jnlp.plist
sudo launchctl load /Library/LaunchDaemons/org.jenkins-ci.slave.jnlp.plist
```