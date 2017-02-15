stage("${DATAVEC_PROJECT}-CheckoutSources") {
    functions.get_project_code("${DATAVEC_PROJECT}")
}

// stage("${DATAVEC_PROJECT}-Codecheck") {
//   functions.sonar("${DATAVEC_PROJECT}")
// }

stage("${DATAVEC_PROJECT}-Build-${PLATFORM_NAME}") {

  echo "Building ${DATAVEC_PROJECT} version ${RELEASE_VERSION}"

  dir("${DATAVEC_PROJECT}") {
    functions.checktag("${DATAVEC_PROJECT}")
    functions.verset("${RELEASE_VERSION}", true)
    //sh "sed -i 's/<nd4j.version>.*<\\/nd4j.version>/<nd4j.version>${RELEASE_VERSION}<\\/nd4j.version>/' pom.xml"

    sh "./change-scala-versions.sh ${SCALA_VERSION}"
    configFileProvider([configFile(fileId: settings_xml, variable: 'MAVEN_SETTINGS')]) {
      switch(PLATFORM_NAME) {
          case "linux-x86_64":
              if (TESTS) {
                docker.image("${DOCKER_CENTOS6_CUDA80_AMD64}").inside(dockerParams) {
                    sh'''
                    mvn -B -s ${MAVEN_SETTINGS} clean deploy -Dnd4j.version=${ND4J_VERSION}
                    '''
                }
              }
              else {
                docker.image("${DOCKER_CENTOS6_CUDA80_AMD64}").inside(dockerParams) {
                    sh'''
                    mvn -B -s ${MAVEN_SETTINGS} clean deploy -DskipTests -Dnd4j.version=${ND4J_VERSION}
                    '''
                }
              }
          break
            case "linux-ppc64le":
              if (TESTS) {
                docker.image("${DOCKER_MAVEN_PPC}").inside(dockerParams_ppc) {
                    sh'''
                    sudo mvn -B -s ${MAVEN_SETTINGS} clean install
                    '''
                }
              }
              else {
                docker.image("${DOCKER_MAVEN_PPC}").inside(dockerParams_ppc) {
                    sh'''
                    sudo mvn -B -s ${MAVEN_SETTINGS} clean install -DskipTests
                    '''
                }
              }
          break
          default:
          break
      }
    }
  }
  if (SONAR) {
      functions.sonar("${DATAVEC_PROJECT}")
  }
}

// if (SONAR) {
//   stage("${GYM_JAVA_CLIENT_PROJECT}-Codecheck") {
//     functions.sonar("${GYM_JAVA_CLIENT_PROJECT}")
//   }
// }
// Messages for debugging
echo 'MARK: end of datavec.groovy'