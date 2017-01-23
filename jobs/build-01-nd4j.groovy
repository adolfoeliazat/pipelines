tool name: 'M339', type: 'maven'
def mvnHome = tool 'M339'

functions = load 'jobs/functions.groovy'

stage('Nd4j Preparation') {
  functions.get_project_code("${PROJECT}")
  functions.get_project_code("${LIBPROJECT}")

}

stage('Libnd4j Codecheck') {
  functions.sonar("${LIBPROJECT}")
}

stage('Nd4j Codecheck') {
  functions.sonar("${PROJECT}")
}

stage('Libnd4j build') {
  echo 'Build Native Operations'
  dir("${LIBPROJECT}") {
    functions.checktag("${LIBPROJECT}")

      echo "Building ${LIBPROJECT} version ${RELEASE_VERSION} (${SNAPSHOT_VERSION})"
      //  sh "export TRICK_NVCC=YES && export LIBND4J_HOME=${WORKSPACE}/$LIBPROJECT && ./buildnativeoperations.sh -c cpu"
      //  sh "export TRICK_NVCC=YES && export LIBND4J_HOME=${WORKSPACE}/$LIBPROJECT && ./buildnativeoperations.sh -c cuda -v 7.5"
      //  sh "export TRICK_NVCC=YES && export LIBND4J_HOME=${WORKSPACE}/$LIBPROJECT && ./buildnativeoperations.sh -c cuda -v 8.0"

  }
}
stage('Nd4j Build') {
  echo "Releasing ${PROJECT} version ${RELEASE_VERSION} (${SNAPSHOT_VERSION}) to repository ${STAGING_REPOSITORY}"

  echo ("Check if ${RELEASE_VERSION} has been released already")
  dir("${PROJECT}") {
    functions.checktag("${PROJECT}")
  }

  echo 'Build components with Maven'
  dir("${PROJECT}") {
    echo 'Set Project Version'
    //  sh ("'${mvnHome}/bin/mvn' versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=$RELEASE_VERSION")
    echo 'Maven Build, Package and Deploy'
    def check_repo = "${STAGING_REPOSITORY}"
    echo check_repo
    if (!check_repo) {
      echo 'STAGING_REPOSITORY is not set'
      sh "./change-scala-versions.sh 2.10"
      sh "./change-cuda-versions.sh 7.5"

      // configFileProvider([configFile(fileId: 'maven-release-bintray-settings-1', variable: 'MAVEN_SETTINGS'),
      //                     configFile(fileId: 'maven-release-bintray-settings-security-1', variable: 'MAVEN_SECURITY_SETTINGS')]) {
      //                       sh ("'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS clean deploy \
      //                            -Dsettings.security=$MAVEN_SECURITY_SETTINGS \
      //                            -Dgpg.executable=gpg2 -Dgpg.skip -DperformRelease \
      //                            -DskipTests -Denforcer.skip -DstagingRepositoryId=$STAGING_REPOSITORY")
      //                     }
    }
  }
}
// Messages for debugging
echo 'MARK: end of build-01-nd4j.groovy'
