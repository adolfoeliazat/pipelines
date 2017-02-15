node("${DOCKER_NODE}") {

    step([$class: 'WsCleanup'])

    // dockerParams = "-v ${WORKSPACE}:${WORKSPACE}:rw -v ${WORKSPACE}/.m2:/home/jenkins/.m2:rw --device=/dev/nvidiactl --device=/dev/nvidia-uvm --device=/dev/nvidia0 --volume=nvidia_driver_367.57:/usr/local/nvidia:ro"

    checkout scm

    // Remove .git folder from workspace
    sh("rm -rf ${WORKSPACE}/.git")
    sh("rm -f ${WORKSPACE}/.gitignore")
    sh("rm -rf ${WORKSPACE}/docs")
    sh("rm -rf ${WORKSPACE}/imgs")
    sh("rm -rf ${WORKSPACE}/ansible")
    sh("rm -f ${WORKSPACE}/README.md")

    load "${PDIR}/vars.groovy"
    functions = load "${PDIR}/functions.groovy"

    sh ("mkdir ${WORKSPACE}/.m2 || true")

    stage("${SCALNET_PROJECT}") {
      load "${PDIR}/${SCALNET_PROJECT}/${SCALNET_PROJECT}-docker.groovy"
    }


    stage('RELEASE') {
      def isSnapshot = RELEASE_VERSION.endsWith('SNAPSHOT')

      if(!isSnapshot) {
      // timeout(time:1, unit:'HOURS') {
        timeout(20) {
            input message:"Approve release of version ${SCALNET_PROJECT}-${RELEASE_VERSION} ?"
        }

        functions.release("${SCALNET_PROJECT}")
      }
      else {
        println "End of building and publishing of the ${SCALNET_PROJECT}-${RELEASE_VERSION}"
      }

    }

    sh "rm -rf $HOME/.sonar"
    step([$class: 'WsCleanup'])

}