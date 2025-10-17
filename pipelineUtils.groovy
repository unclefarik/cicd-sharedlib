def prepareTools() {
    sh 'apk add --no-cache git bash docker-cli'
}

def determineBranchEnv() {
    def BR = env.BRANCH_NAME ?: params.TARGET_ENV
    def TAG = params.IMAGE_TAG ?: 'v1.0'

    env.BRANCH_TO_USE = BR
    env.IMAGE_TAG_TO_USE = TAG
    env.DOCKER_TAG_FINAL = "${BR}-${TAG}"

    echo "Branch/environment: ${env.BRANCH_TO_USE}"
    echo "Docker tag: ${env.DOCKER_TAG_FINAL}"
}

