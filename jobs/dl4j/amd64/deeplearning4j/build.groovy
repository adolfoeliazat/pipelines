timestamps {
    node('amd64&&g2&&ubuntu16') {

      step([$class: 'WsCleanup'])

      checkout scm

      // Remove .git folder from workspace
      sh("rm -rf ${WORKSPACE}/.git")
      sh("rm -f ${WORKSPACE}/.gitignore")
      sh("rm -rf ${WORKSPACE}/docs")
      sh("rm -rf ${WORKSPACE}/imgs")
      sh("rm -rf ${WORKSPACE}/ansible")
      sh("rm -f ${WORKSPACE}/README.md")

      sh("pwd")
      sh("ls -al")

        stage("${DEEPLEARNING4J_PROJECT}") {
            load "${AMD64DIR}/${DEEPLEARNING4J_PROJECT}/${DEEPLEARNING4J_PROJECT}.groovy"
        }
    }
}
