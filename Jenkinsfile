pipeline {
    agent any
    options {
        disableConcurrentBuilds()
    }
    triggers {
        pollSCM('* * * * *')
    }

    environment {
        DOCKER_IMAGE = 'hzmasbl/taskflow'
        IMAGE_TAG    = "${BUILD_NUMBER}"
        DOCKER_CREDS = 'dockerhub-credentials-id'
    }

    stages {
        // 1. Build and package the JAR file on the host machine
        stage('Build & Package') {
            steps {
                bat './mvnw clean package'
            }
        }

        // 2. Build the Docker image in ~2 seconds and push to Docker Hub
        stage('Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDS}") {
                        def customImage = docker.build("${DOCKER_IMAGE}:${IMAGE_TAG}", ".")
                        customImage.push()
                        customImage.push("latest")
                    }
                }
            }
        }

        // 3. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                bat 'kubectl apply -f k8s/deployment.yml --kubeconfig="C:\\Users\\33695\\.kube\\config"'
                bat "kubectl set image deployment/taskflow-app taskflow=${DOCKER_IMAGE}:${IMAGE_TAG} --kubeconfig=\"C:\\Users\\33695\\.kube\\config\""
                bat 'kubectl rollout status deployment/taskflow-app --timeout=300s --kubeconfig="C:\\Users\\33695\\.kube\\config"'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Taskflow successfully built and deployed to Kubernetes!"
        }
        failure {
            echo "Pipeline failed."
        }
    }
}