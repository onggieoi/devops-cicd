#!/usr/bin/env groovy
void call(Map pipelineParams) {

    pipeline {

        agent any

        options {
            disableConcurrentBuilds()
            disableResume()
            timeout(time: 1, unit: 'HOURS')
        }
        
        stages {
            // stage ('Build Backend') {
            //     when {
            //         allOf {
            //             // Condition Check
            //             anyOf{
            //                 // Branch Event: Nornal Flow
            //                 anyOf {
            //                     branch 'master'
            //                 }
            //                 // Manual Run: Only if checked.
            //                 allOf{
            //                     changeset "src/backend/**"
            //                 }
            //             }
            //         }
            //     }
            //     steps {
            //         script {
            //             buildBackEnd()
            //         }
            //     }
            // }

            stage ('Build Frontend') {
                when {
                    allOf {
                        anyOf{
                            anyOf {
                                branch 'master'
                            }
                            allOf{
                                changeset "src/frontend/**"
                            }
                        }
                    }
                }
                steps {
                    script {
                        buildFrontEnd()
                    }
                }
            }
        }

        post {
            cleanup {
                cleanWs()
            }
        }
    }
}
//========================================================================
// Demo CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================
