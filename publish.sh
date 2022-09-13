#!bin/sh

#upload to jitpack.io
./gradlew :appupdate:publishReleasePublicationToMavenRepository

#./gradlew :appupdate-no-op:publishReleasePublicationToMavenLocal