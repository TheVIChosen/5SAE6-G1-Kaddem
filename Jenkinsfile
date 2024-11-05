pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "admin" 
    }

    stages {
        stage('Initialization') {
            steps {
                echo "Start Building Pipeline"
                git branch: 'omarbenfathallah-5SAE6-G1',
                url: 'https://github.com/TheVIChosen/5SAE6-G1-Kaddem.git'
            }
        }
        stage('Clean ') {
            steps {
                echo 'Cleaning previous builds and cache...'
                sh 'mvn clean'
            }
        }
           stage('Build project') {
            steps {
                echo 'Building the application...'

        sh 'mvn package -DskipTests'
            }
        }
       stage('Static Analysis') {
            environment {
                scannerHome = tool 'sonarqubeScanner'
            }
            steps {
                withCredentials([string(credentialsId: 'sonartoken', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('Sonarqube') {
                        sh "${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=springproject \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.host.url=http://10.0.2.15:9002 \
                            -Dsonar.login=${SONAR_TOKEN}"
                    }
                }
            }
        }
        
    }
}
