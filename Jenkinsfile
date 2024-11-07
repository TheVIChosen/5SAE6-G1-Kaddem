pipeline {
    agent any

    stages {
        stage('Git') {
            steps {
                script {
                    def credentialsId = 'PAT_jenkins'
                    git branch: 'ChoukNassim_5SAE6_G1', url: 'https://github.com/TheVIChosen/5SAE6-G1-Kaddem.git', credentialsId: credentialsId
                }
            }
        }
        stage('MVN Clean') {
            steps {
                echo 'Cleaning the project...'
                sh 'mvn clean'
            }
        }
        stage('MVN Compile') {
            steps {
                echo 'Compiling the project...'
                sh 'mvn compile'
            }
        }
        stage('Run Tests') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
        }
        stage('SonarQube analysis') {
            steps {
                withCredentials([string(credentialsId: 'jenkins-sonar', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }
        stage('Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
        stage('Build Image') {
            steps {
                sh 'docker build -t chouknassim_g1_kaddem .'
            }
        }
        stage('Push docker Image') {
            steps {
                script {
                    sh 'docker tag chouknassim_g1_kaddem nassim388/chouknassim_g1_kaddem:latest'
                    withDockerRegistry(credentialsId: 'docker-cred') {
                        sh 'docker push nassim388/chouknassim_g1_kaddem:latest'
                    }
                }
            }
        }
        stage('docker-compose') {
            steps {
                sh 'docker compose up -d'
            }
        }
        stage('Deploy Prometheus') {
            steps {
                sh 'docker restart prometheus'
            }
        }
        stage('Deploy Grafana') {
            steps {
                sh 'docker restart grafana'
            }
        }
    }
}
