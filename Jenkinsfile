pipeline {
    environment {
        registry = "registry-medialab.afp.com"
        registryCredential = "Medialab_Docker_Registry"
        version = ""
        dockerImage = ""
        buidImage = ""
    }
    agent any
    
    stages {
    	stage ('Build package') {
                    
            steps {
                sh '${M2_HOME}/bin/mvn -B -DskipTests clean package' 
            }
        }
        stage ('Build Docker Image') {
        	steps{
	        	script {
	                def version = sh script: '${M2_HOME}/bin/mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
	                println "version ${version}"
	                def dockerImage = "registry-medialab.afp.com/weverify-wrapper:${version}"    
	                println "image ${dockerImage}"
	                def buidImage = docker.build('${dockerImage}','./docker/delivery')           	                          
	        	}
              }                
        }

      
    
       

    }
}