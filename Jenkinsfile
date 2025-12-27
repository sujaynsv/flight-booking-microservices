pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.6'
    }
    
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        DOCKER_HUB_USERNAME = 'sujaynsv'
        PATH = "/usr/local/bin:${env.PATH}"
        SONAR_ORG="sujaynsv"
        SONAR_TOKEN=4084cc84bb3dcf9b38adc9446ddacb2148fcade9
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code'
                checkout scm
            }
        }

    stage('SonarCloud Analysis') {
        steps {
            echo 'SonarCloud Report'
                sh """
                    mvn sonar:sonar \
                      -Dsonar.projectKey=${SONAR_ORG}/flight-booking-microservices \
                      -Dsonar.organization=${SONAR_ORG} \
                      -Dsonar.host.url=https://sonarcloud.io \
                      -Dsonar.token=${SONAR_TOKEN}
                """
            }
        }
        
        stage('Build JARs') {
            steps {
                echo 'Building JAR files'
                sh 'mvn package -DskipTests'

            }
        }
        
        stage('Stop Old Containers') {
            steps {
                echo 'Stopping old containers...'
                sh '/usr/local/bin/docker-compose down || true'
            }
        }
        
        stage('Build and Start Services') {
            steps {
                echo 'Building images and starting services...'
                sh '/usr/local/bin/docker-compose up -d --build'
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo 'Verifying services are running'
                sh 'sleep 10'
                sh '/usr/local/bin/docker-compose ps'
                sh '/usr/local/bin/docker ps'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed!'
        }
        success {
            echo 'Pipeline succeeded! Services are running.'
            sh '/usr/local/bin/docker-compose ps'
        }
        failure {
            echo 'Pipeline failed!'
            sh '/usr/local/bin/docker-compose logs --tail=50'
        }
    }
}
