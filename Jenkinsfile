pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        warnError(message: 'Tests have failed') {
          bat 'mvn test'
        }

      }
    }
    stage('Report') {
      steps {
        cucumber '**/*.json'
      }
    }
  }
}