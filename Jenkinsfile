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
      stage('Checkout') {
          agent { label 'master' }
          steps {
               git  url:"https://github.com/${GROUP}/${APP_NAME}.git",
                      branch :'master'
          }
      }
      stage('build') {
                agent { label 'master' }
                steps {
                    sh "mvn install"
                }
            }
    stage('Docker build') {
        steps {
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