stage("${ND4S_PROJECT}-DependenciesCheck") {
    if (!isSnapshot) {
        echo "Copying nd4j artifacts from userContent"
        functions.copy_nd4j_native_from_user_content()

        int ND4J_NATIVE_COUNT = 0
        while (ND4J_NATIVE_COUNT < 5) {
            sh("rm -rf ${WORKSPACE}/nd4j-native-${VERSION}*")
            node("master") {
                dir("${JENKINS_HOME}/userContent") {
                    stash includes: '*.jar', name: "nd4j-${PLATFORM_NAME}-${BUILD_NUMBER}"
                }
            }
            unstash "nd4j-${PLATFORM_NAME}-${BUILD_NUMBER}"
            ND4J_NATIVE_COUNT = sh(script: 'ls -la ${WORKSPACE}/nd4j-native-${VERSION}* | wc -l', returnStdout: true).trim().toInteger()
            println(ND4J_NATIVE_COUNT)
            sleep unit: "MINUTES", time: 10
        }

        functions.install_nd4j_native_to_local_maven_repository("${VERSION}")
    }
}


stage("${ND4S_PROJECT}-checkout-sources") {
    functions.get_project_code("${ND4S_PROJECT}")
}

stage("${ND4S_PROJECT}-build") {
    echo "Building ${ND4S_PROJECT} version ${VERSION}"
    dir("${ND4S_PROJECT}") {
        functions.checktag("${ND4S_PROJECT}")
//        sh ("sed -i 's/version := \".*\",/version := \"${VERSION}\",/' build.sbt")
//        sh ("sed -i 's/nd4jVersion := \".*\",/nd4jVersion := \"${ND4J_VERSION}\",/' build.sbt")
        sh("test -d ${WORKSPACE}/.ivy2 || mkdir ${WORKSPACE}/.ivy2")
        configFileProvider([configFile(fileId: "sbt-local-nexus-id-1", variable: 'SBT_CREDENTIALS')]) {
            sh("cp ${SBT_CREDENTIALS}  ${WORKSPACE}/.ivy2/.nexus")
        }
        configFileProvider([configFile(fileId: "sbt-local-jfrog-id-1", variable: 'SBT_CREDENTIALS')]) {
            sh("cp ${SBT_CREDENTIALS}  ${WORKSPACE}/.ivy2/.jfrog")
        }
        configFileProvider([configFile(fileId: "sbt-oss-sonatype-id-1", variable: 'SBT_CREDENTIALS')]) {
            sh("cp ${SBT_CREDENTIALS}  ${WORKSPACE}/.ivy2/.sonatype")
        }
        configFileProvider([configFile(fileId: "sbt-oss-bintray-id-1", variable: 'SBT_CREDENTIALS')]) {
            sh("cp ${SBT_CREDENTIALS}  ${WORKSPACE}/.ivy2/.bintray")
        }

        docker.image(dockerImage).inside(dockerParams) {
            sh '''
              cp -a ${WORKSPACE}/.ivy2 ${HOME}/
              cp ${HOME}/.ivy2/.${PROFILE_TYPE} ${HOME}/.ivy2/.credentials
              sbt -DrepoType=${PROFILE_TYPE} -DstageRepoId=${STAGE_REPO_ID} -DcurrentVersion=${VERSION} -Dnd4jVersion=${VERSION} +publish
              find ${WORKSPACE}/.ivy2 ${HOME}/.ivy2  -type f -name  ".credentials"  -delete -o -name ".nexus"  -delete -o -name ".jfrog" -delete -o -name ".sonatype" -delete -o -name ".bintray" -delete;
              '''
        }

    }
}

echo 'MARK: end of nd4s.groovy'
