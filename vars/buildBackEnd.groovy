#!/usr/bin/env groovy
void call() {
    String name = "backend"
    String buildFolder = "backend"
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
        docker.build("ecr-nashtech-devops:${BUILD_NUMBER}", " -f ./.ci/Dockerfile \
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
            sh "docker tag ecr-nashtech-devops:${BUILD_NUMBER} ${demoRegistry}/ecr-nashtech-devops:${BUILD_NUMBER}"
            sh "docker push ${demoRegistry}/ecr-nashtech-devops:${BUILD_NUMBER}"
            sh "docker tag ${demoRegistry}/ecr-nashtech-devops:${BUILD_NUMBER} ${demoRegistry}/ecr-nashtech-devops:latest"
            sh "docker push ${demoRegistry}/ecr-nashtech-devops:latest"
        }
    }

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/service/deployment.yml', text: libraryResource('deploy/eks/service/deployment-backend.yml')
            writeFile file: '.ci/service/service.yml', text: libraryResource('deploy/eks/service/service.yml')
        }
    }

    // stage ("Deploy BackEnd To K8S") {
    //     docker.withRegistry(ecrRegistryUrl, "ecr:${awsRegion}:${awsCredential}") {
    //         sh "export registry=${demoRegistry}; export appname=${name}; export tag=latest; \
    //         envsubst < .ci/service/deployment.yml > deployment.yml; envsubst < .ci/service/service.yml > service.yml"
    //         sh "aws eks --region ${awsRegion} update-kubeconfig --name ${eksName}"
    //         sh "kubectl config current-context"
    //         sh "kubectl version"
    //         sh "kubectl auth can-i '*' '*' --all-namespaces"
    //         sh "kubectl config view --minify"
    //         sh "kubectl get nodes"
    //         sh "kubectl config get-contexts"
    //         sh "kubectl apply -f deployment.yml"
    //         sh "kubectl apply -f service.yml"
    //     }
    // }
    
    stage ("Deploy BackEnd To K8S") {
        script {
            sh "export registry=${demoRegistry}; export appname=${name}; export tag=latest; \
                envsubst < .ci/service/deployment.yml > deployment.yml; envsubst < .ci/service/service.yml > service.yml"
            sh "aws eks --region ${awsRegion} update-kubeconfig --name ${eksName}"
            sh "kubectl config get-contexts"
            sh "kubectl config current-context"
            sh "kubectl version"
            sh "kubectl auth can-i '*' '*' --all-namespaces"
            sh "kubectl config view --minify"
            sh "kubectl get nodes"
            sh "/usr/local/bin/kubectl apply -f deployment.yml"
            sh "/usr/local/bin/kubectl apply -f service.yml"
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