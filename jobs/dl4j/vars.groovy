settings_xml = 'maven-settings-id-1'
dockerParams = "-v ${WORKSPACE}:${WORKSPACE}:rw -v ${WORKSPACE}/.m2:/home/jenkins/.m2:rw --device=/dev/nvidiactl --device=/dev/nvidia-uvm --device=/dev/nvidia0 --volume=nvidia_driver_367.57:/usr/local/nvidia:ro"
dockerParams_tmpfs = "-v ${WORKSPACE}:${WORKSPACE}:rw -v ${WORKSPACE}/.m2:/home/jenkins/.m2:rw --tmpfs /tmp:size=3g --device=/dev/nvidiactl --device=/dev/nvidia-uvm --device=/dev/nvidia0 --volume=nvidia_driver_367.57:/usr/local/nvidia:ro"
dockerParams_ppc = "-v ${WORKSPACE}:${WORKSPACE}:rw -v ${WORKSPACE}/.m2:/home/jenkins/.m2:rw -v /mnt/libnd4j:/libnd4j"


// gitcredid = 'github-private-deeplearning4j-id-1'
// env.GITCREDID = "github-private-deeplearning4j-id-1"
env.PDIR = "jobs/dl4j"
env.ACCOUNT = "deeplearning4j"
env.PROJECT = "nd4j"
env.LIBPROJECT = "libnd4j"
env.ARBITER_PROJECT = "arbiter"
env.DEEPLEARNING4J_PROJECT = "deeplearning4j"
env.GYM_JAVA_CLIENT_PROJECT = "gym-java-client"
env.ND4S_PROJECT = "nd4s"
env.RL4J_PROJECT = "rl4j"
env.SCALNET_PROJECT = "scalnet"
env.DATAVEC_PROJECT = "datavec"
env.DOCKER_UBUNTU14_CUDA75_AMD64 = "deeplearning4j-docker-registry.bintray.io/ubuntu14cuda75:latest"
env.DOCKER_UBUNTU14_CUDA80_AMD64 = "deeplearning4j-docker-registry.bintray.io/ubuntu14cuda80:latest"
env.DOCKER_CENTOS6_CUDA75_AMD64 = "deeplearning4j-docker-registry.bintray.io/centos6cuda75:latest"
env.DOCKER_CENTOS6_CUDA80_AMD64 = "deeplearning4j-docker-registry.bintray.io/centos6cuda80:latest"
// env.DOCKER_MAVEN_PPC = "ppc64le/maven:ready"
env.DOCKER_MAVEN_PPC = "test_mvn:latest"
// env.DOCKER_CUDA_PPC = "ubuntu_cuda_ready:14.04"
env.DOCKER_CUDA_PPC = "test_image:latest"


env.ARTFACT_URL = "http://ec2-54-200-65-148.us-west-2.compute.amazonaws.com:8081/artifactory"
env.ARTFACT_SNAPSHOT = "libs-snapshot-local"
env.ARTFACT_RELEASES = "libs-release-local"
env.ARTFACT_GROUP_ID = "org/nd4j"
env.ARTFACT_USER= "admin"
env.ARTFACT_PASS= "password"
env.SBTCREDID = "sbt-local-artifactory-id-1"
// env.SBTCREDID = "SBT_CREDENTIALS_DO-192"

env.BINTRAY_MAVEN="https://api.bintray.com/maven/deeplearning4j/maven"
