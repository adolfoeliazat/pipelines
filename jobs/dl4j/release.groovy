properties([
        [$class: "BuildDiscarderProperty", strategy: [$class: "LogRotator", artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "", numToKeepStr: "10"]],
        [$class: "ParametersDefinitionProperty", parameterDefinitions:
                [
                        [$class: "StringParameterDefinition", name: "VERSION", defaultValue: "0.7.3-SNAPSHOT", description: "Deeplearning component release version"],
                        [$class: "ChoiceParameterDefinition", name: "PLATFORM_NAME", choices: "linux-x86_64\nlinux-ppc64le\nandroid-arm\nandroid-x86\nlinux-x86", description: "Build project on architecture"],
                        [$class: "BooleanParameterDefinition", name: "TESTS", defaultValue: false, description: "Select to run tests during mvn execution"],
                        [$class: "BooleanParameterDefinition", name: "SONAR", defaultValue: false, description: "Select to check code with SonarQube"],
                        [$class: "BooleanParameterDefinition", name: "CREATE_TAG", defaultValue: false, description: "Select to create tag for release in git repository"],
                        [$class: "StringParameterDefinition", name: "ND4J_VERSION", defaultValue: "", description: "Set preferred nd4j version, leave it empty to use VERSION"],
                        [$class: "StringParameterDefinition", name: "DL4J_VERSION", defaultValue: "", description: "Set preferred dl4j version, leave it empty to use VERSION"],
                        [$class: "StringParameterDefinition", name: "DATAVEC_VERSION", defaultValue: "", description: "Set preferred datavec version, leave it empty to use VERSION"],
                        [$class: "ChoiceParameterDefinition", name: "SCALA_VERSION", choices: "2.10\n2.11", description: "Scala version definition"],
                        [$class: "ChoiceParameterDefinition", name: "CUDA_VERSION", choices: "7.5\n8.0", description: "Cuda version definition"],
                        [$class: "StringParameterDefinition", name: "GIT_BRANCHNAME", defaultValue: "intropro072-01", description: "Default Git branch value"],
                        [$class: "CredentialsParameterDefinition", name: "GITCREDID", required: false, defaultValue: "github-private-deeplearning4j-id-1", description: "Credentials to be used for cloning, pushing and tagging deeplearning4j repositories"],
                        [$class: "StringParameterDefinition", name: "PDIR", defaultValue: "jobs/dl4j", description: "Path to groovy scripts"],
                        [$class: "ChoiceParameterDefinition", name: "PROFILE_TYPE", choices: "nexus\njfrog\nbintray\nsonatype", description: "Profile type"]
                ]
        ]
])

node("master") {

    stage("BuildBaseLibs") {
        parallel (
            "Stream 0 x86_64" : {
                build job: 'devel/dl4j/amd64/base-libs', parameters:
                    [[$class: 'StringParameterValue', name: 'param1', value:'test_param'],
                    [$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "linux-x86_64"],
                    [$class: 'StringParameterValue', name:'DOCKER_NODE', value: "linux-x86_64"]]
            },
            "Stream 1 ppc64le" : {
                build job: 'devel/dl4j/ppc/base-libs', parameters:
                    [[$class: 'StringParameterValue', name: 'param1', value: 'test_param'],
                    [$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "linux-ppc64le"],
                    [$class: 'StringParameterValue', name:'DOCKER_NODE', value: "ppc64le"]]
            }
        )
    }
}

node("linux-x86_64") {

    step([$class: 'WsCleanup'])
    checkout scm

    echo "Load variables"
    load "${PDIR}/vars.groovy"

    echo "load functions"
    functions = load "${PDIR}/functions.groovy"

    // Remove .git folder from workspace
    functions.rm()

    // Create .m2 direcory
    functions.dirm2()

    // Set docker image and parameters for current platform
    functions.def_docker()

    stage("${DATAVEC_PROJECT}") {
      load "${PDIR}/${DATAVEC_PROJECT}/${DATAVEC_PROJECT}-docker.groovy"
    }

    stage("${DEEPLEARNING4J_PROJECT}") {
        load "${PDIR}/${DEEPLEARNING4J_PROJECT}/${DEEPLEARNING4J_PROJECT}-docker.groovy"
    }

    stage ("${ARBITER_PROJECT}") {
      load "${PDIR}/${ARBITER_PROJECT}/${ARBITER_PROJECT}-docker.groovy"
    }

    stage("${ND4S_PROJECT}") {
        load "${PDIR}/${ND4S_PROJECT}/${ND4S_PROJECT}-docker.groovy"
    }

    stage("${GYM_JAVA_CLIENT_PROJECT}") {
      load "${PDIR}/${GYM_JAVA_CLIENT_PROJECT}/${GYM_JAVA_CLIENT_PROJECT}-docker.groovy"
    }

    stage("${RL4J_PROJECT}") {
      load "${PDIR}/${RL4J_PROJECT}/${RL4J_PROJECT}-docker.groovy"
    }

    // depends on nd4j and deeplearning4j-core
    stage("${SCALNET_PROJECT}") {
    	load "${PDIR}/${SCALNET_PROJECT}/${SCALNET_PROJECT}-docker.groovy"
    }


    stage('RELEASE') {

      // def isSnapshot = VERSION.endsWith('SNAPSHOT')

      if(isSnapshot) {
        echo "End of building and publishing of the ${VERSION}"
      }
      else {
        // timeout(time:1, unit:'HOURS') {
        timeout(20) {
            input message:"Approve release of version ${VERSION} ?"
        }

        // functions.release("${LIBPROJECT}")
        functions.release("${PROJECT}")
        functions.release("${DATAVEC_PROJECT}")
        functions.release("${DEEPLEARNING4J_PROJECT}")
        functions.release("${ARBITER_PROJECT}")
        functions.release("${ND4S_PROJECT}")
        functions.release("${GYM_JAVA_CLIENT_PROJECT}")
        functions.release("${RL4J_PROJECT}")
        functions.release("${SCALNET_PROJECT}")

      }

    }

    echo 'MARK: end of release.groovy'
}
