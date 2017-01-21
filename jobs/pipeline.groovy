timestamps {
  node ('master') {
    step([$class: 'WsCleanup'])

    // def GITCREDID = 'github-private-deeplearning4j-id-1'

    checkout scm

    sh ("env")

    stage('ND4J') {
      load 'jobs/build-01-nd4j.groovy'
    }

    stage('DATAVEC') {
      load 'jobs/build-02-datavec.groovy'
    }

    stage('DEEPLEARNING4J') {
    	load  'jobs/build-03-deeplearning4j.groovy'
    }

    stage('ARBITER') {
    	load 'jobs/build-04-arbiter.groovy'
    }

    stage('ND4S') {
    	load 'jobs/build-05-nd4s.groovy'
    }

    stage('GYM-JAVA-CLIENT') {
    	load 'jobs/build-06-gym-java-client.groovy'
    }

    stage('RL4J') {
    	load 'jobs/build-07-rl4j.groovy'
    }

    stage('SCALNET') {
    	load 'jobs/build-08-scalnet.groovy'
    }

    stage('RELEASE') {
      // timeout(time:1, unit:'HOURS') {
      timeout(10) {
          input message:"Approve release of version ${RELEASE_VERSION} ?"
      }

      echo "Adding tag ${PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${PROJECT}"
      dir("${PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          // sh "ls -la ${pwd()}"
          // TODO: send command to bintray to mirror release to Maven Central
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${PROJECT}-${RELEASE_VERSION} -m ${PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${LIBPROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${LIBPROJECT}"
      dir("${LIBPROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          // sh "ls -la ${pwd()}"
          // Define what to do with linbnd4j build
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${LIBPROJECT}-${RELEASE_VERSION} -m ${LIBPROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${LIBPROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${DATAVEC_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${DATAVEC_PROJECT}"
      dir("${DATAVEC_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          // sh "ls -la ${pwd()}"
          // TODO: send command to bintray to mirror release to Maven Central
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${DATAVEC_PROJECT}-${RELEASE_VERSION} -m ${DATAVEC_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${DATAVEC_PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${DEEPLEARNING4J_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${DEEPLEARNING4J_PROJECT}"
      dir("${DEEPLEARNING4J_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          // sh "ls -la ${pwd()}"
          // TODO: send command to bintray to mirror release to Maven Central
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${DEEPLEARNING4J_PROJECT}-${RELEASE_VERSION} -m ${DEEPLEARNING4J_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${DEEPLEARNING4J_PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${ARBITER_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${ARBITER_PROJECT}"
      dir("${ARBITER_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          // sh "ls -la ${pwd()}"
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${ARBITER_PROJECT}-${RELEASE_VERSION} -m ${ARBITER_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${ARBITER_PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${ND4S_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${ND4S_PROJECT}"
      dir("${ND4S_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${ND4S_PROJECT}-${RELEASE_VERSION} -m ${ND4S_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${ND4S_PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${GYM_JAVA_CLIENT_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${GYM_JAVA_CLIENT_PROJECT}"
      dir("${GYM_JAVA_CLIENT_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git tag -a ${GYM_JAVA_CLIENT_PROJECT}-${RELEASE_VERSION} -m ${GYM_JAVA_CLIENT_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${GYM_JAVA_CLIENT_PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${RL4J_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${RL4J_PROJECT}"
      dir("${RL4J_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // sh ("echo ${RL4J_PROJECT} ${RELEASE_VERSION}")
          // DO NOT ENABLE TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh ("git commit -a -m 'Update to version ${RELEASE_VERSION}'")
          // sh 'git tag -a ${RL4J_PROJECT}-${RELEASE_VERSION} -m ${RL4J_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${RL4J_PROJECT}-${RELEASE_VERSION}'
        }
      }

      echo "Adding tag ${SCALNET_PROJECT}-${RELEASE_VERSION} to github.com/${ACCOUNT}/${SCALNET_PROJECT}"
      dir("${SCALNET_PROJECT}") {
        sshagent(credentials: ["${GITCREDID}"]) {
          sh 'git config user.email "jenkins@skymind.io"'
          sh 'git config user.name "Jenkins"'
          sh 'git status'
          // DO NOT ENABLE COMMIT AND TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
          // sh 'git commit -a -m "Update to version ${RELEASE_VERSION}"'
          // sh 'git tag -a ${SCALNET_PROJECT}-${RELEASE_VERSION} -m ${SCALNET_PROJECT}-${RELEASE_VERSION}'
          // sh 'git push origin ${SCALNET_PROJECT}-${RELEASE_VERSION}'
        }
      }
    }
      step([$class: 'WsCleanup'])
  }
  // Messages for debugging
  echo 'MARK: end of pipeline.groovy'
}
