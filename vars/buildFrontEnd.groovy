#!/usr/bin/env groovy
void call() {
    String name = "frontend"
    String buildFolder = "frontend"
    String baseImage     = "node"
    String baseTag       = "lts-buster"
    String demoRegistry = "361555779387.dkr.ecr.ap-southeast-1.amazonaws.com"
    String awsRegion = "ap-southeast-1"
    String ecrRegistryUrl = "https://361555779387.dkr.ecr.ap-southeast-1.amazonaws.com"
    String awsCredential = 'aws-credential'
    String eksName = "eks-dev"

//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/Dockerfile', text: libraryResource('node/Dockerfile')
        }
    }

    stage('SonarQube analysis') {
        echo "Run SonarQube Analysis"
    }

    stage ("Build Solution") {
        docker.build("ecr-nashtech-devops-${name}:${BUILD_NUMBER}", " -f ./.ci/Dockerfile \
        --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}/src/${buildFolder}") 
    }

    stage ('Run Unit Tests') {
        echo "Run Unit Tests"
    }

    stage ('Run Integration Tests') {
        echo "Run Integration Tests"
    }

    stage ('Process Test Results') {
        echo "Export Test Results"
    }

    stage ("Push Docker Images") {
        docker.withRegistry(ecrRegistryUrl, "ecr:${awsRegion}:${awsCredential}") {
            sh "docker tag ecr-nashtech-devops-${name}:${BUILD_NUMBER} ${demoRegistry}/ecr-nashtech-devops-${name}:${BUILD_NUMBER}"
            sh "docker push ${demoRegistry}/ecr-nashtech-devops-${name}:${BUILD_NUMBER}"
        }
    }

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/service/deployment.yml', text: libraryResource('deploy/eks/service/deployment.yml')
            writeFile file: '.ci/service/service.yml', text: libraryResource('deploy/eks/service/service.yml')
        }
    }

    stage ("Deploy frontend To K8S") {
        docker.withRegistry(ecrRegistryUrl, "ecr:${awsRegion}:${awsCredential}") {
            sh "export registry=${demoRegistry}; export appname=${name}; export tag=${BUILD_NUMBER}; \
            envsubst < .ci/service/deployment.yml > deployment.yml; envsubst < .ci/service/service.yml > service.yml"
            sh "echo deployment.yml"
            sh "aws eks --region ${awsRegion} update-kubeconfig --name ${eksName}"
            sh "kubectl apply -f deployment.yml"
            sh "kubectl apply -f service.yml"
        }
    }
}

//========================================================================
// node CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================