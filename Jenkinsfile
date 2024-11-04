pipeline {
    agent any

    tools {
        // Make sure Maven is installed in Jenkins and referenced here nnnn
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
        stage("SonarQube Analysis and Quality Gate") {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') { // Ensure 'SonarQube' matches your configured server name
                        // Run SonarQube analysis using Maven
                        sh "mvn sonar:sonar"
                    }
        
                    // Polling mechanism for quality gate status
                    def qualityGateStatus = 'PENDING'
                    def maxRetries = 10 // Maximum number of retries
                    def delayBetweenRetries = 30 // Delay between retries in seconds
        
                    for (int i = 0; i < maxRetries; i++) {
                        // Wait for the quality gate status
                        qualityGateStatus = waitForQualityGate().status
        
                        if (qualityGateStatus == 'OK') {
                            echo "Quality gate passed successfully!"
                            break
                        } else if (qualityGateStatus == 'ERROR') {
                            error "Quality gate failed with status: ${qualityGateStatus}"
                        }
        
                        // If not yet OK, wait before checking again
                        sleep(delayBetweenRetries)
                        echo "Waiting for quality gate status... (Attempt ${i + 1}/${maxRetries})"
                    }
        
                    // Check if we've exhausted our retries
                    if (qualityGateStatus != 'OK') {
                        error "Quality gate status is still '${qualityGateStatus}' after ${maxRetries} attempts."
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
                    
                    // Check if port 8089 is free
                    def portInUse = sh(script: "lsof -i :8089", returnStatus: true) == 0
        
                    def assignedPort = "8089" // Default port
                    if (portInUse) {
                        echo "Port 8089 is already in use. Assigning a different port."
                        // Find an available port between 8000 and 9000
                        assignedPort = sh(script: "comm -23 <(seq 8000 9000) <(ss -tan | awk '{print \$4}' | cut -d':' -f2) | head -n 1", returnStdout: true).trim()
                        echo "Using dynamic port: ${assignedPort}"
                    } else {
                        echo "Port 8089 is free. Running container on port 8089."
                    }
                    
                    // Check if a container with this image is already running
                    def containerId = sh(script: "docker ps -aqf 'ancestor=${DOCKER_IMAGE_NAME}'", returnStdout: true).trim()
                    
                    if (containerId) {
                        echo "Starting the existing container for '${DOCKER_IMAGE_NAME}' on port ${assignedPort}"
                        sh "docker start ${containerId}"
                    } else {
                        echo "Running a new container from the pulled image '${DOCKER_IMAGE_NAME}' on port ${assignedPort}"
                        // Run a new container from the pulled image with dynamic port assignment
                        sh "docker run -d -p ${assignedPort}:8089 ${DOCKER_IMAGE_NAME}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build and tests were successful!'
            emailext(
                to: 'oualinader@gmail.com',
                from: 'nader.ouali@esprit.tn', // Set the sender email address
                replyTo: 'nader.ouali@esprit.tn', // Set the reply-to address
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS",
                body: "The build ${env.BUILD_NUMBER} has completed successfully.\nCheck details at: ${env.BUILD_URL}"
            )
        }
        
        failure {
            echo 'Build or tests failed!'
            emailext(
                to: 'oualinader@gmail.com',
                from: 'nader.ouali@esprit.tn', // Set the sender email address
                replyTo: 'nader.ouali@esprit.tn', // Set the reply-to address
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE",
                body: "The build ${env.BUILD_NUMBER} has failed.\nCheck details at: ${env.BUILD_URL}"
            )
        }
    }
}
