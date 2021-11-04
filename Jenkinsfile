import java.text.SimpleDateFormat

def REGISTRY_DOMAIN = "https://192.168.7.238:5000"
def REGISTRY_DOMAIN_URI = "192.168.7.238:5000"
def REGISTRY_REPO = "apps"
def DOCKER_IMAGE_NAME = "k8sep"
def NAMESPACE = "apps"
def IMG_PULL_SECRET = "imgpullsecret"
def SVC_IN_PORT = "8080"
def SVC_EX_PORT = "80"
def SVC_TYPE = "LoadBalancer"
def PVC_NAME = "${DOCKER_IMAGE_NAME}-pvc"
def PVC_SIZE = "1Gi"
def NFS_NAME = "nfs-storage"

def IMAGE = "${REGISTRY_DOMAIN_URI}/${REGISTRY_REPO}/${DOCKER_IMAGE_NAME}"
def dateFormat = new SimpleDateFormat("yyyyMMddHHmmss")
def DATE = new Date();
def today = dateFormat.format(DATE)

podTemplate(label: 'builder',
            containers: [
                containerTemplate(name: 'maven', image: '192.168.7.238:5000/images/maven:3.6.2-jdk-8', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'podman', image: '192.168.7.238:5000/images/podman:ubuntu-16.04', ttyEnabled: true, command: 'cat', privileged: true),
                containerTemplate(name: 'kubectl', image: '192.168.7.238:5000/images/k8s-kubectl:latest', command: 'cat', ttyEnabled: true)
            ]
            ) {
    node('builder') {
        try {
            stage('Source Checkout') {
                checkout scm
            }
            stage('Maven Build') {
                container('maven') {
                    withSonarQubeEnv('sonar_server') {
                        sh "mvn -version"
                        sh "mvn clean package -DskipTests sonar:sonar"
                    }
                }
            }
            stage('Make image & push') {
                container('podman') {
                    withCredentials([usernamePassword(
                        credentialsId: 'nexus_auth',
                        usernameVariable: 'USERNAME',
                        passwordVariable: 'PASSWORD')]) {
                            sh "podman version"
                            sh "podman build -t ${IMAGE}:${today} . --tls-verify=false"
                            sh "podman login -u ${USERNAME} -p ${PASSWORD} ${REGISTRY_DOMAIN} --tls-verify=false"
                            sh "podman push ${IMAGE}:${today} --tls-verify=false"
                    }
                }
            }
            //stage('Image Vulnerability Scanning') {
            //    container('podman') {
            //        aquaMicroscanner imageName: "${IMAGE}:${today}", notCompliesCmd: "", onDisallowed: "ignore", outputFormat: "html"
            //    }
            //}
            stage('k8s deploy') {
                container('kubectl') {
                    withCredentials([usernamePassword(
                        credentialsId: 'nexus_auth',
                        usernameVariable: 'USERNAME',
                        passwordVariable: 'PASSWORD')]) {
                            sh "kubectl version"
                            sh """
                                kubectl get ns ${NAMESPACE} || kubectl create ns ${NAMESPACE}
                                kubectl get secret ${IMG_PULL_SECRET} -n ${NAMESPACE} || \
                                kubectl create secret docker-registry ${IMG_PULL_SECRET} \
                                --docker-server=https://index.docker.io/v1/ \
                                --docker-username=${USERNAME} \
                                --docker-password=${PASSWORD} \
                                --docker-email=freeson01@growin.co.kr \
                                -n ${NAMESPACE}

                                sed -i 's#PVC-NAME#${PVC_NAME}#' ./k8s/pvc.yaml
                                sed -i 's#APPS-NAME#${DOCKER_IMAGE_NAME}#' ./k8s/pvc.yaml
                                sed -i 's#APPS-NS#${NAMESPACE}#' ./k8s/pvc.yaml
                                sed -i 's#NFS-NAME#${NFS_NAME}#' ./k8s/pvc.yaml
                                sed -i 's#PVC-SIZE#${PVC_SIZE}#' ./k8s/pvc.yaml

                                kubectl get pvc -n ${NAMESPACE} ${PVC_NAME} || kubectl apply -f ./k8s/pvc.yaml -n ${NAMESPACE}
                                
                                sed -i 's#DATE_STRING#${today}#' ./k8s/deploy.yaml
                                sed -i 's#APPS-NAME#${DOCKER_IMAGE_NAME}#' ./k8s/deploy.yaml
                                sed -i 's#APPS-NS#${NAMESPACE}#' ./k8s/deploy.yaml
                                sed -i 's#APPS-IMAGE#${IMAGE}:${today}#' ./k8s/deploy.yaml
                                sed -i 's#IMG-SECRET#${IMG_PULL_SECRET}#' ./k8s/deploy.yaml
                                sed -i 's#IN-PORT#${SVC_IN_PORT}#' ./k8s/deploy.yaml
                                sed -i 's#EX-PORT#${SVC_EX_PORT}#' ./k8s/deploy.yaml
                                sed -i 's#SVC-TYPE#${SVC_TYPE}#' ./k8s/deploy.yaml
                                sed -i 's#PVC-NAME#${PVC_NAME}#' ./k8s/deploy.yaml
                                
                                kubectl apply -f ./k8s/deploy.yaml
                            """
                    }
                }
            }
        } catch(e) {
            currentBuild.result = "FAILED"
        }
    }
}

