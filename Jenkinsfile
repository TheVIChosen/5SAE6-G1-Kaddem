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

        
    }
}
