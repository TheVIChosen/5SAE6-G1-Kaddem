pipeline {
    agent any

    tools {
        // Make sure Maven is installed in Jenkins and referenced here
        maven 'M2_HOME' // Adjust the Maven installation to your Jenkins setup
    }
    
    environment {
        // MySQL environment variables
        MYSQL_CONTAINER_NAME = "mysql-container"
        MYSQL_IMAGE = "mysql:latest"
        MYSQL_ROOT_PASSWORD = "0000"
        MYSQL_DATABASE = "kaddemdb"
        MYSQL_PORT = "3306"
        // Use 'nexus-username' as the ID for both username and password
        NEXUS_CREDENTIALS = credentials('nexus-username') 
        // Docker
        DOCKER_CREDENTIALS = credentials('docker-credentials')
        DOCKER_IMAGE_NAME = 'naderouali/kaddem:latest'
    }

    stages {
        stage("Start MySQL") {
            steps {
                script {
                    // Check if MySQL container with the specified name is running
                    def mysqlRunning = sh(script: "docker ps --filter 'name=${MYSQL_CONTAINER_NAME}' --filter 'status=running' -q", returnStdout: true).trim()
                    
                    // Check if a MySQL container exists (running or stopped)
                    def mysqlExists = sh(script: "docker ps -a --filter 'name=${MYSQL_CONTAINER_NAME}' -q", returnStdout: true).trim()
        
                    if (mysqlRunning) {
                        echo "MySQL container is already running"
                    } else if (mysqlExists) {
                        echo "MySQL container exists but is stopped. Starting it..."
                        sh "docker start ${MYSQL_CONTAINER_NAME}"
                    } else {
                        echo "MySQL container does not exist. Creating and starting a new one"
                        sh """
                        docker run --name ${MYSQL_CONTAINER_NAME} \
                            -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
                            -e MYSQL_DATABASE=${MYSQL_DATABASE} \
                            -p ${MYSQL_PORT}:3306 \
                            -d ${MYSQL_IMAGE}
                        """
                    }
                    // Check MySQL logs for issues
                    echo "Checking MySQL logs..."
                    sh "docker logs ${MYSQL_CONTAINER_NAME} || echo 'No logs available'"
                }
            }
        }
        //stage("Start SonarQube and Nexus") {
            //steps {
                //script {
                    //// Start existing SonarQube and Nexus containers without creating new ones
                    //sh 'docker start sonarqube'
                    //sh 'docker start nexus'
                    //// Wait a few seconds to ensure the services are fully started
                    //sleep 30
                //}
            //}
        //}
        stage("Start SonarQube and Nexus") {
            steps {
                script {
                    // Start existing SonarQube and Nexus containers if they are not already running
                    sh 'docker start sonarqube || echo "SonarQube already running"'
                    sh 'docker start nexus || echo "Nexus already running"'
                    
                    // Wait for SonarQube to be fully initialized
                    timeout(time: 2, unit: 'MINUTES') {
                        waitUntil {
                            def sonarqubeReady = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:9000", returnStdout: true).trim() == '200'
                            def nexusRunning = sh(script: "docker inspect -f '{{.State.Running}}' nexus", returnStdout: true).trim() == 'true'
                            return sonarqubeReady && nexusRunning
                        }
                    }
                }
            }
        }
        stage("Start Grafana and Prometheus") {
            steps {
                script {
                    // Start only Grafana and Prometheus services with network_mode set to host
                    sh 'docker compose -f ./docker-compose.yml up -d grafana prometheus'
                }
            }
        }
        stage("Start Docker Compose Services") {
            steps {
                script {
                    // Start all necessary Docker Compose services
                    sh 'docker compose -f ./docker-compose.yml up -d'
                }
            }
        }
        stage("Build/Clean with Maven") {
            steps {
                // Runs Maven clean, compile, test, and package commands
                sh "mvn clean verify"
                // List contents of target directory
                sh "ls -la target/"
            }
        }
        stage("Run Tests with JUnit") {
            steps {
                // Runs JUnit tests and generates JaCoCo coverage reports
                sh "mvn test jacoco:report"
            }
            post {
                // Publish JUnit test results in Jenkins
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage("SonarQube Analysis") {
            steps {
                script {
                    // Use the SonarQube environment defined in Jenkins
                    withSonarQubeEnv('SonarQube') { // 'SonarQube' should match your configured SonarQube Server name
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }
        stage("Deploy to Nexus") {
            steps {
                script {
                    // Use the credentials stored in 'NEXUS_CREDENTIALS' for deployment
                    sh "mvn deploy -DskipTests -Dnexus.username=${NEXUS_CREDENTIALS_USR} -Dnexus.password=${NEXUS_CREDENTIALS_PSW}"
                }
            }
        }
        stage("Build Docker Image") {
            steps {
                script {
                    // Build the Docker image
                    sh "docker build -t ${DOCKER_IMAGE_NAME} ."
                }
            }
        }
        stage("Push Docker Image to Docker Hub") {
            steps {
                script {
                    // Log in to Docker Hub
                    sh "echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin"
                    
                    // Push the Docker image
                    sh "docker push ${DOCKER_IMAGE_NAME}"
                }
            }
        }
        stage("Deploy Kaddem Application") {
            steps {
                script {
                    // Deploys the Kaddem container using the image from Docker Hub
                    sh 'docker compose -f ./docker-compose.yml up -d kaddem-app'
                }
            }
        }
        stage("Stop Restarting Containers") {
            steps {
                script {
                    // Stop the containers that are in a "restarting" state
                    sh 'docker stop naderouali_5sae6_g1-nexus-1 || true'
                    sh 'docker stop naderouali_5sae6_g1-sonarqube-1 || true'
                }
            }
        }
        stage("Run Spring Boot Application as Docker Container") {
            steps {
                script {
                    // Ensure the latest image is pulled from Docker Hub
                    sh "docker pull ${DOCKER_IMAGE_NAME}"
                    
                    // Check if a container with this image is already running
                    def containerId = sh(script: "docker ps -aqf 'ancestor=${DOCKER_IMAGE_NAME}'", returnStdout: true).trim()
                    
                    if (containerId) {
                        echo "Starting the existing container for '${DOCKER_IMAGE_NAME}'"
                        sh "docker start ${containerId}"
                    } else {
                        echo "Running a new container from the pulled image '${DOCKER_IMAGE_NAME}'"
                        // Run a new container from the pulled image with specific configurations
                        sh "docker run -d -p 8089:8089 ${DOCKER_IMAGE_NAME}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build and tests were successful!'
        }
        
        failure {
            echo 'Build or tests failed!'
            
            // Send email on failure
            emailext (
                subject: "Jenkins Pipeline - Build #${env.BUILD_NUMBER} - FAILED",
                body: """The build ${env.BUILD_NUMBER} has failed.
                         Check details at: ${env.BUILD_URL}""",
                to: 'nader.ouali@esprit.tn'  // Recipient's email address
            )
        }
    
        always {
            // This block sends an email regardless of success or failure
            emailext (
                subject: "Jenkins Pipeline - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                body: """The build ${env.BUILD_NUMBER} has completed with status: ${currentBuild.currentResult}.
                         Check details at: ${env.BUILD_URL}""",
                to: 'nader.ouali@esprit.tn'  // Recipient's email address
            )
        }
    }
}
