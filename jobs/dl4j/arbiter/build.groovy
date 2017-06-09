def notifyFailed() {
  emailext (
      subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
      Check console output at '${env.BUILD_URL}'""",
      to: "${MAIL_RECIPIENT}"
    )
}

env.PLATFORM_NAME = env.PLATFORM_NAME ?: "master"
node("${PLATFORM_NAME}") {
  try {
    currentBuild.displayName = "#${currentBuild.number} ${PLATFORM_NAME}"
    ws(WORKSPACE + "_" + PLATFORM_NAME) {
        properties([
                [$class: "BuildDiscarderProperty", strategy: [$class: "LogRotator", artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "", numToKeepStr: "10"]],
                [$class: "ParametersDefinitionProperty", parameterDefinitions:
                        [
                                [$class: "StringParameterDefinition", name: "VERSION", defaultValue: "0.8.1-SNAPSHOT", description: "Deeplearning component release version"],
//                                [$class: "ChoiceParameterDefinition", name: "PLATFORM_NAME", choices: "linux-x86_64\nandroid-arm\nandroid-x86\nlinux-ppc64le\nmacosx-x86_64\nwindows-x86_64", description: "Build project on architecture"],
                                [$class: "ChoiceParameterDefinition", name: "PLATFORM_NAME", choices: "linux-x86_64", description: "Build project on architecture"],
//                            [$class: "LabelParameterDefinition", name: "DOCKER_NODE", defaultValue: "jenkins-slave-cuda", description: "Correct parameters:\nFor x86_64-jenkins-slave-cuda,amd64\nfor PowerPC - ppc,power8"],
                                [$class: "BooleanParameterDefinition", name: "SKIP_TEST", defaultValue: true, description: "Select to skip tests during mvn execution"],
                                [$class: "BooleanParameterDefinition", name: "SONAR", defaultValue: false, description: "Select to check code with SonarQube"],
                                [$class: "StringParameterDefinition", name: "STAGE_REPO_ID", defaultValue: "", description: "Staging repository Id"],
//                            [$class: "BooleanParameterDefinition", name: "CREATE_TAG", defaultValue: false, description: "Select to create tag for release in git repository"],
//                            [$class: "StringParameterDefinition", name: "ND4J_VERSION", defaultValue: "", description: "Set preferred nd4j version, leave it empty to use VERSION"],
//                            [$class: "StringParameterDefinition", name: "DL4J_VERSION", defaultValue: "", description: "Set preferred dl4j version, leave it empty to use VERSION"],
//                            [$class: "StringParameterDefinition", name: "DATAVEC_VERSION", defaultValue: "", description: "Set preferred datavec version, leave it empty to use VERSION"],
                                // [$class: "ChoiceParameterDefinition", name: "SCALA_VERSION", choices: "2.10\n2.11", description: "Scala version definition"],
                                // [$class: "ChoiceParameterDefinition", name: "CUDA_VERSION", choices: "7.5\n8.0", description: "Cuda version definition"],
                                [$class: "StringParameterDefinition", name: "GIT_BRANCHNAME", defaultValue: "master", description: "Default Git branch value"],
                                [$class: "CredentialsParameterDefinition", name: "GITCREDID", required: false, defaultValue: "github-private-deeplearning4j-id-1", description: "Credentials to be used for cloning, pushing and tagging deeplearning4j repositories"],
//                            [$class: "StringParameterDefinition", name: "PDIR", defaultValue: "jobs/dl4j", description: "Path to groovy scripts"],
                                [$class: "ChoiceParameterDefinition", name: "PROFILE_TYPE", choices: "sonatype\nnexus\njfrog\nbintray", description: "Profile type"]
                        ]
                ]
        ])

        echo "Cleanup WS"
        step([$class: 'WsCleanup'])

        checkout scm

        load "jobs/dl4j/vars.groovy"

        functions = load "${PDIR}/functions.groovy"

        // Remove .git folder from workspace
        functions.rm()

        // Set docker image and parameters for current platform
        functions.def_docker()

        stage("${ARBITER_PROJECT}") {
            load "${PDIR}/${ARBITER_PROJECT}/${ARBITER_PROJECT}-${PLATFORM_NAME}.groovy"
        }


        stage('RELEASE') {

            if (isSnapshot) {
                echo "End of building and publishing of the ${ARBITER_PROJECT}-${VERSION}"
            } else {
                // timeout(time:1, unit:'HOURS') {
                timeout(20) {
                    input message: "Approve release of version ${ARBITER_PROJECT}-${VERSION} ?"
                }

                functions.tag("${ARBITER_PROJECT}")
            }

        }

        // step([$class: 'WsCleanup'])

      }
    // send email about successful finishing
    functions.notifySuccessful(currentBuild.displayName)

    } catch (e) {
      currentBuild.result = "FAILED"
      notifyFailed()
      throw e

      }
}


ansiColor('xterm') {
    echo "\033[42m MARK: end of arbiter/build.groovy \033[0m"
}
