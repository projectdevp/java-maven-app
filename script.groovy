#!/usr/bin/env groovy
def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t tawfiqnajib/java-maven-app:jma-3.0 .'
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh 'docker push tawfiqnajib/java-maven-app:jma-3.0'
    }
}

def deployApp() {
    echo 'Deploying the application...'

    // Configuration des informations de connexion à votre instance EC2
    def remoteUser = 'ec2-user'
    def remoteHost = '35.180.128.54'
    def dockercmd= 'docker run -d -p 8080:8080 tawfiqnajib/java-maven-app:jma-3.0'

    // Définition des clés SSH à utiliser (doit être configuré dans Jenkins sous "Manage Jenkins" -> "Manage Credentials")
    def sshCredentialsId = 'your-ssh-credentials-id'

    // Utilisation du plugin SSH Agent pour gérer les clés SSH
    sshagent(['ec2-credentials']) {
        // Commandes SSH pour déployer l'application sur votre instance EC2
        sh "ssh -o StrictHostKeyChecking=no ${remoteUser}@${remoteHost} ${dockercmd}"

    }
}


return this
