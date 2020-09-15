pipeline {
    agent  { label 'master' }
    tools {
        maven 'Maven 3.6.0'
        jdk 'jdk8'
    }
  environment {
    VERSION="6.10.0"
    GROUP = "NeotysLab"


  }

  stages {
      repository('Checkout') {
          agent { label 'master' }
          workloads {
               git  url:"https://github.com/${GROUP}/${APP_NAME}.git",
                      branch :'master'
          }
      }
      repository('build') {
                agent { label 'master' }
                workloads {
                    sh "mvn install"
                }
            }
    repository('Docker build') {
        workloads {
            sh "mvn jib:build
        }
     }
 }
 post {
      always {
        cleanWs()
      }
    }
 }