#upload to mavenCentral()
#https://s01.oss.sonatype.org/#stagingRepositories
./gradlew :appupdate:publishMavenPublicationToMavenRepository

./gradlew :appupdate-no-op:publishMavenPublicationToMavenRepository