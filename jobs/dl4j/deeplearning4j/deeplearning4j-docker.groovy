stage("${DEEPLEARNING4J_PROJECT}-CheckoutSources") {

  functions.get_project_code("${DEEPLEARNING4J_PROJECT}")
  dir("${DEEPLEARNING4J_PROJECT}") {
      functions.checktag("${DEEPLEARNING4J_PROJECT}")
  }

  checkout([$class                           : 'GitSCM',
            branches                         : [[name: '*/master']],
            doGenerateSubmoduleConfigurations: false,
            extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: "dl4j-test-resources"], [$class: 'CloneOption', honorRefspec: true, noTags: true, reference: '', shallow: true]],
            submoduleCfg                     : [],
            userRemoteConfigs                : [[url: "https://github.com/${ACCOUNT}/dl4j-test-resources.git"]]
  ])
}

if (!TESTS) {
  docker.image('ubuntu14cuda80').inside(dockerParams) {
    configFileProvider([configFile(fileId: 'MAVEN_SETTINGS_DO-192', variable: 'MAVEN_SETTINGS')]) {
      stage("${DEEPLEARNING4J_PROJECT} Build test resources"){
          sh'''
          cd dl4j-test-resources
          mvn clean install
          '''
      }
      stage("${DEEPLEARNING4J_PROJECT}-Build-withDocker") {
          echo "Releasing ${DEEPLEARNING4J_PROJECT} version ${RELEASE_VERSION}"
          sh'''
          cd deeplearning4j
          mvn -B versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=${RELEASE_VERSION}
          ./change-scala-versions.sh ${SCALA_VERSION}
          ./change-cuda-versions.sh ${CUDA_VERSION}
          mvn -B -s ${MAVEN_SETTINGS} clean deploy -DskipTests -Dnd4j.version=${ND4J_VERSION} -Ddatavec.version=${DATAVEC_VERSION}
          '''
      }
    }
  }
}
else {
  configFileProvider([configFile(fileId: 'MAVEN_SETTINGS_DO-192', variable: 'MAVEN_SETTINGS')]) {
    docker.image('ubuntu14cuda80').inside(dockerParams) {
      stage("${DEEPLEARNING4J_PROJECT} Build test resources"){
          sh'''
          cd dl4j-test-resources
          mvn -q clean install
          '''
      }
      stage("${DEEPLEARNING4J_PROJECT}-Build-withDocker") {
          echo "Releasing ${DEEPLEARNING4J_PROJECT} version ${RELEASE_VERSION}"
          sh'''
          cd deeplearning4j
          mvn -B versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=${RELEASE_VERSION}
          ./change-scala-versions.sh ${SCALA_VERSION}
          ./change-cuda-versions.sh ${CUDA_VERSION}
          mvn -B -s ${MAVEN_SETTINGS} clean deploy -Dnd4j.version=${ND4J_VERSION} -Ddatavec.version=${DATAVEC_VERSION}
          '''
      }
    }
  }
}

if (SONAR) {
    functions.sonar("${DEEPLEARNING4J_PROJECT}")
}


// if (SONAR) {
//   stage("${DEEPLEARNING4J_PROJECT}-Codecheck") {
//     functions.sonar("${DEEPLEARNING4J_PROJECT}")
//   }
// }

echo 'MARK: end of deeplearning4j.groovy'
